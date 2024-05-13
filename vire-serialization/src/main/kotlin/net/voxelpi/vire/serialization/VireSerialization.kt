package net.voxelpi.vire.serialization

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import net.voxelpi.vire.engine.BooleanState
import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.component.ComponentConfiguration
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.kernel.KernelInstance
import net.voxelpi.vire.engine.kernel.KernelState
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.InterfaceVariable
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSize
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.variableOfKind
import net.voxelpi.vire.serialization.adapter.BooleanStateAdapter
import net.voxelpi.vire.serialization.adapter.IdentifierAdapter
import net.voxelpi.vire.serialization.adapter.LogicStateAdapter
import net.voxelpi.vire.serialization.adapter.UUIDAdapter
import java.util.UUID
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

@OptIn(ExperimentalStdlibApi::class)
public object VireSerialization {

    public fun serialize(circuit: Circuit, formatted: Boolean = false): String {
        val gson = GsonBuilder().apply {
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            if (formatted) {
                setPrettyPrinting()
            }
            registerTypeAdapter(UUID::class.java, UUIDAdapter)
            registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
            registerTypeAdapter(LogicState::class.java, LogicStateAdapter)
            registerTypeAdapter(BooleanState::class.java, BooleanStateAdapter)
        }.create()

        val circuitData = JsonObject()
        circuitData.add("id", gson.toJsonTree(circuit.id))
        circuitData.add("tags", gson.toJsonTree(circuit.tags))
        circuitData.add("properties", gson.toJsonTree(circuit.properties))

        val componentsData = JsonArray()
        for (component in circuit.components()) {
            val componentData = JsonObject()

            val kernelVariantData = JsonObject()
            kernelVariantData.add("kernel", gson.toJsonTree(component.kernel.id))
            val parameterStateData = JsonObject()
            for (parameter in component.kernel.parameters()) {
                parameterStateData.add(parameter.name, gson.toJsonTree(component.kernelVariant[parameter], parameter.type.javaType))
            }
            kernelVariantData.add("parameters", parameterStateData)
            componentData.add("kernel_variant", kernelVariantData)

            val settingsData = JsonObject()
            for (setting in component.kernel.settings()) {
                when (val settingValue = component.configuration[setting]) {
                    is ComponentConfiguration.Entry.Value -> {
                        settingsData.add(setting.name, gson.toJsonTree(settingValue.value, setting.type.javaType))
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
                portData.add("unique_id", gson.toJsonTree(port.uniqueId))
                portData.add("variable", port.variable?.name?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
                portsData.add(portData)
            }
            componentData.add("ports", portsData)

            componentData.add("unique_id", gson.toJsonTree(component.uniqueId))

            componentsData.add(componentData)
        }
        circuitData.add("components", componentsData)

        val networksData = JsonArray()
        circuit.networks().map { network ->
            val networkData = JsonObject()
            networkData.add("unique_id", gson.toJsonTree(network.uniqueId))
            networkData.add("initialization", gson.toJsonTree(network.initialization))
            networkData.add("nodes", gson.toJsonTree(network.nodes().map { it.uniqueId }.toList()))
            networkData.add(
                "connections",
                gson.toJsonTree(
                    network.connections().map { arrayOf(it.node1.uniqueId, it.node2.uniqueId) }.toList()
                )
            )
            networksData.add(networkData)
        }
        circuitData.add("networks", networksData)

        val terminalsData = JsonArray()
        for (terminal in circuit.terminals()) {
            val terminalData = JsonObject()
            terminalData.add("unique_id", gson.toJsonTree(terminal.uniqueId))
            terminalData.add("variable", terminal.variable?.name?.let { JsonPrimitive(it) } ?: JsonNull.INSTANCE)
            terminalsData.add(terminalData)
        }
        circuitData.add("terminals", terminalsData)

        val inputsData = JsonArray()
        for (input in circuit.inputs()) {
            val size = when (input) {
                is InputScalar -> JsonNull.INSTANCE
                is InputVector -> when (val size = input.size) {
                    is VectorVariableSize.Parameter -> throw Exception("The input vector \"${input.name}\" uses a parameter as size")
                    is VectorVariableSize.Value -> JsonPrimitive(size.value)
                }
                is InputVectorElement -> throw IllegalStateException("Input vector elements cannot be serialized (\"${input.name}\")")
            }
            val inputData = JsonObject()
            inputData.addProperty("name", input.name)
            inputData.add("size", size)
            inputsData.add(inputData)
        }
        circuitData.add("inputs", inputsData)

        val outputsData = JsonArray()
        for (output in circuit.outputs()) {
            val size = when (output) {
                is OutputScalar -> JsonNull.INSTANCE
                is OutputVector -> when (val size = output.size) {
                    is VectorVariableSize.Parameter -> throw Exception("The output vector \"${output.name}\" uses a parameter as size")
                    is VectorVariableSize.Value -> JsonPrimitive(size.value)
                }
                is OutputVectorElement -> throw IllegalStateException("Output vector elements cannot be serialized (\"${output.name}\")")
            }
            val outputData = JsonObject()
            outputData.addProperty("name", output.name)
            outputData.add("size", size)
            outputsData.add(outputData)
        }
        circuitData.add("outputs", outputsData)

        return gson.toJson(circuitData)
    }

    @Suppress("UNCHECKED_CAST")
    public fun deserialize(environment: Environment, serializedCircuit: String): Result<Circuit> {
        val gson = GsonBuilder().apply {
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            registerTypeAdapter(UUID::class.java, UUIDAdapter)
            registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
            registerTypeAdapter(LogicState::class.java, LogicStateAdapter)
            registerTypeAdapter(BooleanState::class.java, BooleanStateAdapter)
        }.create()

        val circuitData = JsonParser.parseString(serializedCircuit)
        if (circuitData !is JsonObject) {
            return Result.failure(Exception("Invalid format, root node should be an object"))
        }

        val circuitId = gson.fromJson(circuitData["id"], Identifier::class.java)
        val circuit = environment.createCircuit(circuitId)

        val specialNodes = mutableSetOf<UUID>()

        // Create all inputs.
        for (inputData in circuitData["inputs"].asJsonArray) {
            check(inputData is JsonObject)
            val input = if (inputData.has("size")) {
                input(inputData["name"].asString, inputData["size"].asInt)
            } else {
                input(inputData["name"].asString)
            }
            circuit.declareVariable(input)
        }

        // Create all outputs.
        for (outputData in circuitData["outputs"].asJsonArray) {
            check(outputData is JsonObject)

            val output = if (outputData.has("size")) {
                output(outputData["name"].asString, outputData["size"].asInt)
            } else {
                output(outputData["name"].asString)
            }
            circuit.declareVariable(output)
        }

        // Create all components.
        for (componentData in circuitData["components"].asJsonArray) {
            check(componentData is JsonObject)

            // Create the kernel variant.
            val kernelVariantData = componentData["kernel_variant"].asJsonObject

            val kernelId = gson.fromJson(kernelVariantData["kernel"], Identifier::class.java)
            val kernel = environment.kernel(kernelId)
                ?: return Result.failure(Exception("Unknown kernel \"$kernelId\""))

            val parameterStates = mutableMapOf<String, Any?>()
            for ((parameterName, parameterStateData) in kernelVariantData["parameters"].asJsonObject.entrySet()) {
                val parameter = kernel.parameter(parameterName) ?: continue
                val data = gson.fromJson<Any?>(parameterStateData, parameter.type.javaType)
                parameterStates[parameterName] = data
            }

            val kernelVariant = kernel.createVariant(parameterStates).getOrElse {
                return Result.failure(it)
            }

            // Create the component.
            val componentUniqueId = gson.fromJson(componentData["unique_id"], UUID::class.java)
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
                                ?: return Result.failure(Exception("Unknown circuit setting \"$circuitSettingName\" for \"$settingName\""))
                            component.configuration[setting] = ComponentConfiguration.Entry.CircuitSetting(circuitSetting)
                        }
                    }
                } else {
                    val value = gson.fromJson<Any?>(settingStateData, setting.type.javaType)
                    component.configuration[setting] = ComponentConfiguration.Entry.Value(value)
                }
            }

            // Create all ports.
            for (portData in componentData["ports"].asJsonArray) {
                check(portData is JsonObject)
                val portUniqueId = gson.fromJson(portData["unique_id"], UUID::class.java)
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
            val terminalUniqueId = gson.fromJson(terminalData["unique_id"], UUID::class.java)
            val variable = terminalData["variable"]?.let {
                circuit.variableOfKind<InterfaceVariable>(it.asString) ?: throw Exception("Unknown circuit variable \"$it\"")
            }
            circuit.createTerminal(variable, terminalUniqueId)
            specialNodes += terminalUniqueId
        }

        // Create all networks.
        for (networkData in circuitData["networks"].asJsonArray) {
            check(networkData is JsonObject)

            val nodesUniqueIds = gson.fromJson<List<UUID>>(networkData["nodes"], typeOf<List<UUID>>().javaType)
            val connectionsUniqueIds = networkData["connections"].asJsonArray.map {
                check(it is JsonArray)
                val uniqueId1 = gson.fromJson(it[0], UUID::class.java)
                val uniqueId2 = gson.fromJson(it[1], UUID::class.java)
                Pair(uniqueId1, uniqueId2)
            }
            val initialization = gson.fromJson(networkData["initialization"], LogicState::class.java)
            val networkUniqueId = gson.fromJson(networkData["unique_id"], UUID::class.java)
            circuit.createNetwork(nodesUniqueIds, connectionsUniqueIds, initialization, networkUniqueId)
        }

        // Return the created circuit.
        return Result.success(circuit)
    }

    public fun serialize(kernelState: KernelState, formatted: Boolean = false): String {
        val gson = GsonBuilder().apply {
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            if (formatted) {
                setPrettyPrinting()
            }
            registerTypeAdapter(UUID::class.java, UUIDAdapter)
            registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
            registerTypeAdapter(LogicState::class.java, LogicStateAdapter)
            registerTypeAdapter(BooleanState::class.java, BooleanStateAdapter)
        }.create()

        val kernelStateData = JsonObject()

        // TODO: Handle nested states.
        val fieldsStateData = JsonObject()
        for (field in kernelState.kernelVariant.fields()) {
            val state = kernelState[field]
            fieldsStateData.add(field.name, gson.toJsonTree(state, field.type.javaType))
        }
        kernelStateData.add("fields", fieldsStateData)

        val inputsStateData = JsonObject()
        for (input in kernelState.kernelVariant.inputs()) {
            when (input) {
                is InputScalar -> {
                    val state = kernelState[input]
                    inputsStateData.add(input.name, gson.toJsonTree(state))
                }
                is InputVector -> {
                    val state = kernelState[input]
                    inputsStateData.add(input.name, gson.toJsonTree(state))
                }
                is InputVectorElement -> throw UnsupportedOperationException()
            }
        }
        kernelStateData.add("inputs", inputsStateData)

        val outputsStateData = JsonObject()
        for (output in kernelState.kernelVariant.outputs()) {
            when (output) {
                is OutputScalar -> {
                    val state = kernelState[output]
                    outputsStateData.add(output.name, gson.toJsonTree(state))
                }
                is OutputVector -> {
                    val state = kernelState[output]
                    outputsStateData.add(output.name, gson.toJsonTree(state))
                }
                is OutputVectorElement -> throw UnsupportedOperationException()
            }
        }
        kernelStateData.add("outputs", outputsStateData)

        return gson.toJson(kernelStateData)
    }

    @Suppress("UNCHECKED_CAST")
    public fun deserialize(kernelInstance: KernelInstance, serializedKernelState: String): Result<KernelState> {
        val gson = GsonBuilder().apply {
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            registerTypeAdapter(UUID::class.java, UUIDAdapter)
            registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
            registerTypeAdapter(LogicState::class.java, LogicStateAdapter)
            registerTypeAdapter(BooleanState::class.java, BooleanStateAdapter)
        }.create()

        val kernelStateData = JsonParser.parseString(serializedKernelState)
        if (kernelStateData !is JsonObject) {
            return Result.failure(Exception("Invalid format, root node should be an object"))
        }

        val kernelState = kernelInstance.initialKernelState()

        // TODO: Handle nested states.
        val fieldsStateData = kernelStateData["fields"].asJsonObject
        for (field in kernelInstance.kernelVariant.fields()) {
            val fieldData = gson.fromJson<Any?>(fieldsStateData[field.name], field.type.javaType)
            kernelState[field as Field<Any?>] = fieldData
        }

        val inputsStateData = kernelStateData["inputs"].asJsonObject
        for (input in kernelInstance.kernelVariant.inputs()) {
            when (input) {
                is InputScalar -> {
                    val inputData = gson.fromJson(inputsStateData[input.name], LogicState::class.java)
                    kernelState[input] = inputData
                }
                is InputVector -> {
                    val inputData = gson.fromJson<Array<LogicState>>(inputsStateData[input.name], typeOf<Array<LogicState>>().javaType)
                    kernelState[input] = inputData
                }
                is InputVectorElement -> throw UnsupportedOperationException()
            }
        }

        val outputsStateData = kernelStateData["outputs"].asJsonObject
        for (output in kernelInstance.kernelVariant.outputs()) {
            when (output) {
                is OutputScalar -> {
                    val outputData = gson.fromJson(outputsStateData[output.name], LogicState::class.java)
                    kernelState[output] = outputData
                }
                is OutputVector -> {
                    val outputData = gson.fromJson<Array<LogicState>>(outputsStateData[output.name], typeOf<Array<LogicState>>().javaType)
                    kernelState[output] = outputData
                }
                is OutputVectorElement -> throw UnsupportedOperationException()
            }
        }

        return Result.success(kernelState)
    }
}
