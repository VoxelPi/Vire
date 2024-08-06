package net.voxelpi.vire.serialization

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.voxelpi.vire.engine.BooleanState
import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.kernel.KernelInstance
import net.voxelpi.vire.engine.kernel.KernelState
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.InputVector
import net.voxelpi.vire.engine.kernel.variable.InputVectorElement
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.serialization.adapter.BooleanStateAdapter
import net.voxelpi.vire.serialization.adapter.CircuitAdapter
import net.voxelpi.vire.serialization.adapter.IdentifierAdapter
import net.voxelpi.vire.serialization.adapter.LogicStateAdapter
import net.voxelpi.vire.serialization.adapter.UUIDAdapter
import java.util.UUID
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

@OptIn(ExperimentalStdlibApi::class)
public object VireSerialization {

    public fun serialize(environment: Environment, circuit: Circuit, formatted: Boolean = false): String {
        val gson = GsonBuilder().apply {
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            if (formatted) {
                setPrettyPrinting()
            }
            registerTypeAdapter(UUID::class.java, UUIDAdapter)
            registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
            registerTypeAdapter(LogicState::class.java, LogicStateAdapter)
            registerTypeAdapter(BooleanState::class.java, BooleanStateAdapter)
            registerTypeAdapter(Circuit::class.java, CircuitAdapter(environment))
        }.create()

        return gson.toJson(circuit, Circuit::class.java)
    }

    public fun deserialize(environment: Environment, serializedCircuit: String): Result<Circuit> {
        val gson = GsonBuilder().apply {
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            registerTypeAdapter(UUID::class.java, UUIDAdapter)
            registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
            registerTypeAdapter(LogicState::class.java, LogicStateAdapter)
            registerTypeAdapter(BooleanState::class.java, BooleanStateAdapter)
            registerTypeAdapter(Circuit::class.java, CircuitAdapter(environment))
        }.create()

        return try {
            Result.success(gson.fromJson(serializedCircuit, Circuit::class.java))
        } catch (exception: Exception) {
            Result.failure(exception)
        }
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
        for (field in kernelState.variableProvider.fields()) {
            val state = kernelState[field]
            fieldsStateData.add(field.name, gson.toJsonTree(state, field.type.javaType))
        }
        kernelStateData.add("fields", fieldsStateData)

        val inputsStateData = JsonObject()
        for (input in kernelState.variableProvider.inputs()) {
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
        for (output in kernelState.variableProvider.outputs()) {
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
