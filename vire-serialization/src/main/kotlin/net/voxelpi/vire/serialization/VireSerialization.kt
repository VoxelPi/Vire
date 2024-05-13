package net.voxelpi.vire.serialization

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.InterfaceVariable
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.engine.kernel.variable.VectorVariableSize
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.variableOfKind
import net.voxelpi.vire.serialization.adapter.IdentifierAdapter
import net.voxelpi.vire.serialization.adapter.UUIDAdapter
import net.voxelpi.vire.serialization.model.CircuitData
import net.voxelpi.vire.serialization.model.ComponentData
import net.voxelpi.vire.serialization.model.ComponentPortData
import net.voxelpi.vire.serialization.model.IOVariableData
import net.voxelpi.vire.serialization.model.KernelVariantData
import net.voxelpi.vire.serialization.model.NetworkData
import net.voxelpi.vire.serialization.model.TerminalData
import java.util.UUID

public object VireSerialization {

    private val moshi = Moshi.Builder()
        .add(IdentifierAdapter)
        .add(UUIDAdapter)
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Suppress("UNUSED_PARAMETER")
    @OptIn(ExperimentalStdlibApi::class)
    public fun serialize(environment: Environment, circuit: Circuit, indent: String = ""): String {
        val networks = circuit.networks().map { network ->
            NetworkData(
                network.uniqueId,
                network.initialization,
                network.nodes().map { it.uniqueId },
                network.connections().map { arrayOf(it.node1.uniqueId, it.node2.uniqueId) }
            )
        }
        val terminals = circuit.terminals().map { terminal ->
            TerminalData(terminal.uniqueId, terminal.variable?.name)
        }
        val components = circuit.components().map { component ->
            val kernelVariantData = KernelVariantData(
                component.kernel.id,
                component.kernelVariant.parameters().associate { it.name to component.kernelVariant[it] },
            )
            // TODO: Settings
            val ports = component.ports().map { port ->
                ComponentPortData(port.uniqueId, port.variable?.name)
            }

            ComponentData(component.uniqueId, kernelVariantData, ports)
        }
        val inputs = circuit.inputs().map { input ->
            val size = when (input) {
                is InputScalar -> null
                is InputVector -> when (val size = input.size) {
                    is VectorVariableSize.Parameter -> throw Exception("The input vector \"${input.name}\" references a parameter as size")
                    is VectorVariableSize.Value -> size.value
                }
                is InputVectorElement -> throw IllegalStateException("Input vector elements cannot be serialized (\"${input.name}\")")
            }
            IOVariableData(input.name, size)
        }
        val outputs = circuit.outputs().map { output ->
            val size = when (output) {
                is OutputScalar -> null
                is OutputVector -> when (val size = output.size) {
                    is VectorVariableSize.Parameter -> throw Exception("Output vector \"${output.name}\" references a parameter as size")
                    is VectorVariableSize.Value -> size.value
                }
                is OutputVectorElement -> throw IllegalStateException("Output vector elements cannot be serialized (\"${output.name}\")")
            }
            IOVariableData(output.name, size)
        }
        val circuitData = CircuitData(
            circuit.id,
            circuit.tags,
            circuit.properties,
            components,
            networks,
            terminals,
            inputs,
            outputs,
        )

        return moshi.adapter<CircuitData>().indent(indent).toJson(circuitData)
    }

    @OptIn(ExperimentalStdlibApi::class)
    public fun deserialize(environment: Environment, serializedCircuit: String, indent: String = ""): Result<Circuit> {
        val circuitData = moshi.adapter<CircuitData>().indent(indent).fromJson(serializedCircuit)
            ?: return Result.failure(Exception("Invalid format"))

        val circuit = environment.createCircuit(circuitData.id)
        val specialNodes = mutableSetOf<UUID>()

        // Create all inputs.
        for (inputData in circuitData.inputs) {
            val input = if (inputData.size == null) {
                input(inputData.name)
            } else {
                input(inputData.name, inputData.size)
            }
            circuit.declareVariable(input)
        }

        // Create all outputs.
        for (outputData in circuitData.outputs) {
            val output = if (outputData.size == null) {
                output(outputData.name)
            } else {
                output(outputData.name, outputData.size)
            }
            circuit.declareVariable(output)
        }

        // Create all components.
        for (componentData in circuitData.components) {
            // Create the kernel variant.
            val kernel = environment.kernel(componentData.kernelVariant.id)
                ?: return Result.failure(Exception("Unknown kernel \"${componentData.kernelVariant.id}\""))
            val parameterStates = mutableMapOf<String, Any?>() // TODO read parameters
            val kernelVariant = kernel.createVariant(parameterStates).getOrElse {
                return Result.failure(it)
            }

            // Create the component.
            val component = circuit.createComponent(kernelVariant, componentData.uniqueId)

            // TODO: Component configuration

            // Create all ports.
            for (portData in componentData.ports) {
                val variable = portData.variable?.let {
                    kernel.variableOfKind<InterfaceVariable>(it) ?: throw Exception("Unknown port variable \"$it\"")
                }
                component.createPort(variable, portData.uniqueId)
                specialNodes += portData.uniqueId
            }
        }

        // Create all terminals.
        for (terminalData in circuitData.terminals) {
            val variable = terminalData.variable?.let {
                circuit.variableOfKind<InterfaceVariable>(it) ?: throw Exception("Unknown circuit variable \"$it\"")
            }
            circuit.createTerminal(variable, terminalData.uniqueId)
            specialNodes += terminalData.uniqueId
        }

        // Create all networks.
        for (networkData in circuitData.networks) {
            val nodesData = networkData.nodes
            val connectionsData = networkData.connections.map { Pair(it[0], it[1]) }
            circuit.createNetwork(nodesData, connectionsData, networkData.initialization, networkData.uniqueId)
        }

        // Return the created circuit.
        return Result.success(circuit)
    }
}
