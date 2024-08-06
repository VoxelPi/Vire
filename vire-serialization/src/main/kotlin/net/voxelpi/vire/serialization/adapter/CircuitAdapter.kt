package net.voxelpi.vire.serialization.adapter

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.component.ComponentConfiguration
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.kernel.registered.RegisteredKernel
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.InterfaceVariable
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.engine.kernel.variable.Parameter
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.createInput
import net.voxelpi.vire.engine.kernel.variable.createInputVector
import net.voxelpi.vire.engine.kernel.variable.createOutput
import net.voxelpi.vire.engine.kernel.variable.createOutputVector
import net.voxelpi.vire.engine.kernel.variable.patch.MutableParameterStatePatch
import net.voxelpi.vire.engine.kernel.variable.variableOfKind
import java.lang.reflect.Type
import java.util.UUID
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

@OptIn(ExperimentalStdlibApi::class)
public class CircuitAdapter(
    private val environment: Environment,
) : JsonSerializer<Circuit>, JsonDeserializer<Circuit> {

    override fun serialize(circuit: Circuit, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        // Serialize meta data.
        val circuitData = JsonObject()
        circuitData.add("tags", context.serialize(circuit.tags))
        circuitData.add("properties", context.serialize(circuit.properties))

        // Serialize components.
        val componentsData = JsonArray()
        for (component in circuit.components()) {
            val componentData = JsonObject()
            val kernel = component.kernel
            require(kernel is RegisteredKernel) { "Cannot serialize unregistered kernels" }

            val kernelVariantData = JsonObject()
            kernelVariantData.add("kernel", context.serialize(kernel.id))
            val parameterStateData = JsonObject()
            for (parameter in component.kernel.parameters()) {
                parameterStateData.add(parameter.name, context.serialize(component.kernelVariant[parameter], parameter.type.javaType))
            }
            kernelVariantData.add("parameters", parameterStateData)
            componentData.add("kernel_variant", kernelVariantData)

            val settingsData = JsonObject()
            for (setting in component.kernel.settings()) {
                when (val settingValue = component.configuration[setting]) {
                    is ComponentConfiguration.Entry.Value -> {
                        settingsData.add(setting.name, context.serialize(settingValue.value, setting.type.javaType))
                    }
                    is ComponentConfiguration.Entry.CircuitSetting -> {
                        val data = JsonObject()
                        data.addProperty("setting_value_type", "circuit_setting")
                        data.addProperty("setting", settingValue.setting.name)
                        settingsData.add(setting.name, data)
                    }
                }
            }
            componentData.add("settings", settingsData)

            val portsData = JsonArray()
            for (port in component.ports()) {
                val portData = JsonObject()
                portData.add("unique_id", context.serialize(port.uniqueId))
                portData.add("variable", port.variable?.name?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
                portsData.add(portData)
            }
            componentData.add("ports", portsData)

            componentData.add("unique_id", context.serialize(component.uniqueId))

            componentsData.add(componentData)
        }
        circuitData.add("components", componentsData)

        // Serialize networks.
        val networksData = JsonArray()
        circuit.networks().map { network ->
            val networkData = JsonObject()
            networkData.add("unique_id", context.serialize(network.uniqueId))
            networkData.add("nodes", context.serialize(network.nodes().map { it.uniqueId }.toList()))
            networkData.add(
                "connections",
                context.serialize(
                    network.connections().map { arrayOf(it.node1.uniqueId, it.node2.uniqueId) }.toList()
                )
            )
            networksData.add(networkData)
        }
        circuitData.add("networks", networksData)

        // Serialize terminals.
        val terminalsData = JsonArray()
        for (terminal in circuit.terminals()) {
            val terminalData = JsonObject()
            terminalData.add("unique_id", context.serialize(terminal.uniqueId))
            terminalData.add("variable", terminal.variable?.name?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
            terminalsData.add(terminalData)
        }
        circuitData.add("terminals", terminalsData)

        // Serialize inputs.
        val inputsData = JsonArray()
        for (input in circuit.inputs()) {
            val size = when (input) {
                is InputScalar -> JsonNull.INSTANCE
                is InputVector -> JsonPrimitive(circuit.size(input))
                is InputVectorElement -> throw IllegalStateException("Input vector elements cannot be serialized (\"${input.name}\")")
            }
            val inputData = JsonObject()
            inputData.addProperty("name", input.name)
            inputData.add("size", size)
            inputsData.add(inputData)
        }
        circuitData.add("inputs", inputsData)

        // Serialize outputs.
        val outputsData = JsonArray()
        for (output in circuit.outputs()) {
            val size = when (output) {
                is OutputScalar -> JsonNull.INSTANCE
                is OutputVector -> JsonPrimitive(circuit.size(output))
                is OutputVectorElement -> throw IllegalStateException("Output vector elements cannot be serialized (\"${output.name}\")")
            }
            val outputData = JsonObject()
            outputData.addProperty("name", output.name)
            outputData.add("size", size)
            outputsData.add(outputData)
        }
        circuitData.add("outputs", outputsData)

        return circuitData
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(circuitData: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Circuit {
        require(circuitData is JsonObject)

        val circuit = environment.createCircuit()

        val specialNodes = mutableSetOf<UUID>()

        // Create all inputs.
        for (inputData in circuitData["inputs"].asJsonArray) {
            check(inputData is JsonObject)
            val input = if (inputData.has("size")) {
                createInputVector(inputData["name"].asString) {
                    size = { inputData["size"].asInt }
                }
            } else {
                createInput(inputData["name"].asString)
            }
            circuit.declareVariable(input)
        }

        // Create all outputs.
        for (outputData in circuitData["outputs"].asJsonArray) {
            check(outputData is JsonObject)

            val output = if (outputData.has("size")) {
                createOutputVector(outputData["name"].asString) {
                    size = { outputData["size"].asInt }
                }
            } else {
                createOutput(outputData["name"].asString)
            }
            circuit.declareVariable(output)
        }

        // Create all components.
        for (componentData in circuitData["components"].asJsonArray) {
            check(componentData is JsonObject)

            // Create the kernel variant.
            val kernelVariantData = componentData["kernel_variant"].asJsonObject

            val kernelId = context.deserialize<Identifier>(kernelVariantData["kernel"], Identifier::class.java)
            val kernel = environment.kernel(kernelId) ?: throw IllegalStateException("Unknown kernel \"$kernelId\"")

            val parameterStates = MutableParameterStatePatch(kernel)
            for ((parameterName, parameterStateData) in kernelVariantData["parameters"].asJsonObject.entrySet()) {
                val parameter = kernel.parameter(parameterName) ?: continue
                val data = context.deserialize<Any?>(parameterStateData, parameter.type.javaType)
                parameterStates[parameter as Parameter<Any?>] = data
            }

            val kernelVariant = kernel.createVariant(parameterStates).getOrThrow()

            // Create the component.
            val componentUniqueId = context.deserialize<UUID>(componentData["unique_id"], UUID::class.java)
            val component = circuit.createComponent(kernelVariant, componentUniqueId)

            // Update settings
            for ((settingName, settingStateData) in componentData["settings"].asJsonObject.entrySet()) {
                val setting = kernel.setting(settingName) as Setting<Any?>? ?: continue
                if (settingStateData is JsonObject && settingStateData.has("setting_value_type")) {
                    val type = settingStateData["setting_value_type"].asString
                    when (type) {
                        "circuit_setting" -> {
                            val circuitSettingName = settingStateData["setting"].asString
                            val circuitSetting = circuit.setting(circuitSettingName) as Setting<Any?>?
                                ?: throw Exception("Unknown circuit setting \"$circuitSettingName\" for \"$settingName\"")
                            component.configuration[setting] = ComponentConfiguration.Entry.CircuitSetting(circuitSetting)
                        }
                    }
                } else {
                    val value = context.deserialize<Any?>(settingStateData, setting.type.javaType)
                    component.configuration[setting] = ComponentConfiguration.Entry.Value(value)
                }
            }

            // Create all ports.
            for (portData in componentData["ports"].asJsonArray) {
                check(portData is JsonObject)
                val portUniqueId = context.deserialize<UUID>(portData["unique_id"], UUID::class.java)
                val variable = portData["variable"]?.let {
                    kernel.variableOfKind<InterfaceVariable>(it.asString) ?: throw Exception("Unknown port variable \"$it\"")
                }
                component.createPort(variable, portUniqueId)
                specialNodes += portUniqueId
            }
        }

        // Create all terminals.
        for (terminalData in circuitData["terminals"].asJsonArray) {
            check(terminalData is JsonObject)
            val terminalUniqueId = context.deserialize<UUID>(terminalData["unique_id"], UUID::class.java)
            val variable = terminalData["variable"]?.let {
                circuit.variableOfKind<InterfaceVariable>(it.asString) ?: throw Exception("Unknown circuit variable \"$it\"")
            }
            circuit.createTerminal(variable, terminalUniqueId)
            specialNodes += terminalUniqueId
        }

        // Create all networks.
        for (networkData in circuitData["networks"].asJsonArray) {
            check(networkData is JsonObject)

            val nodesUniqueIds = context.deserialize<List<UUID>>(networkData["nodes"], typeOf<List<UUID>>().javaType)
            val connectionsUniqueIds = networkData["connections"].asJsonArray.map {
                check(it is JsonArray)
                val uniqueId1 = context.deserialize<UUID>(it[0], UUID::class.java)
                val uniqueId2 = context.deserialize<UUID>(it[1], UUID::class.java)
                Pair(uniqueId1, uniqueId2)
            }
            val networkUniqueId = context.deserialize<UUID>(networkData["unique_id"], UUID::class.java)
            circuit.createNetwork(nodesUniqueIds, connectionsUniqueIds, networkUniqueId)
        }

        // Return the created circuit.
        return circuit
    }
}
