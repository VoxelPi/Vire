package net.voxelpi.vire.engine.kernel.variable.patch

import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.kernel.KernelVariantImpl
import net.voxelpi.vire.engine.kernel.variable.Input
import net.voxelpi.vire.engine.kernel.variable.Output
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputScalarInitializationContext
import net.voxelpi.vire.engine.kernel.variable.OutputVector
import net.voxelpi.vire.engine.kernel.variable.OutputVectorElement
import net.voxelpi.vire.engine.kernel.variable.OutputVectorInitializationContext
import net.voxelpi.vire.engine.kernel.variable.VariableProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutableOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.MutablePartialOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.OutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.PartialOutputStateProvider
import net.voxelpi.vire.engine.kernel.variable.provider.SettingStateProvider
import net.voxelpi.vire.engine.kernel.variable.storage.MutableOutputStateMap
import net.voxelpi.vire.engine.kernel.variable.storage.OutputStateMap

internal interface OutputStatePatch : PartialOutputStateProvider {

    override val variableProvider: VariableProvider

    val data: OutputStateMap

    fun copy(): OutputStatePatch

    fun mutableCopy(): MutableOutputStatePatch

    override fun get(output: OutputScalar): LogicState {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(output)) { "Unknown output ${output.name}" }

        // Return the value of the output.
        return data[output.name]!![0]
    }

    override fun get(outputVector: OutputVector): Array<LogicState> {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(outputVector)) { "Unknown output vector ${outputVector.name}" }

        // Return the value of the output.
        return data[outputVector.name]!!
    }

    override fun get(outputVector: OutputVector, index: Int): LogicState {
        return get(outputVector)[index]
    }

    override fun hasValue(output: Output): Boolean {
        return output.name in data
    }
}

internal class MutableOutputStatePatch(
    override val variableProvider: VariableProvider,
    override val data: MutableOutputStateMap,
) : OutputStatePatch, MutablePartialOutputStateProvider {

    override fun copy(): OutputStatePatch = mutableCopy()

    override fun mutableCopy(): MutableOutputStatePatch {
        return MutableOutputStatePatch(variableProvider, data.toMutableMap())
    }

    override fun set(output: OutputScalar, value: LogicState) {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(output)) { "Unknown output ${output.name}" }

        // Update the value of the output.
        data[output.name]!![0] = value
    }

    override fun set(outputVector: OutputVector, value: Array<LogicState>) {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(outputVector)) { "Unknown output vector ${outputVector.name}" }

        // Update the value of the output.
        data[outputVector.name] = value
    }

    override fun set(outputVector: OutputVector, index: Int, value: LogicState) {
        // Check that an output with the given name exists.
        require(variableProvider.hasOutput(outputVector)) { "Unknown output vector ${outputVector.name}" }

        // Return the value of the output.
        data[outputVector.name]!![index] = value
    }

    fun update(data: OutputStateMap) {
        for ((outputName, value) in data) {
            // Check that only existing outputs are specified.
            val output = variableProvider.output(outputName)
                ?: throw IllegalArgumentException("Unknown output '$outputName'")

            // Update the value of the output.
            when (output) {
                is OutputScalar -> this[output] = value[0]
                is OutputVector -> this[output] = value
                is OutputVectorElement -> throw IllegalArgumentException("Output vector elements may not be specified ('$outputName')")
            }
        }
    }
}

internal fun outputStatePatch(variableProvider: VariableProvider, data: OutputStateMap): OutputStatePatch {
    return mutableOutputStatePatch(variableProvider, data)
}

internal fun outputStatePatch(variableProvider: VariableProvider, dataProvider: OutputStateProvider): OutputStatePatch {
    return mutableOutputStatePatch(variableProvider, dataProvider)
}

internal fun mutableOutputStatePatch(variableProvider: VariableProvider, data: OutputStateMap): MutableOutputStatePatch {
    val processedData: MutableOutputStateMap = mutableMapOf()
    for (output in variableProvider.outputs()) {
        // Check that the output has an assigned value.
        require(output.name in data) { "No value provided for the output ${output.name}" }

        // Get the value from the map.
        val value = data[output.name]!!

        // Put value into map.
        processedData[output.name] = value
    }
    return MutableOutputStatePatch(variableProvider, processedData)
}

internal fun mutableOutputStatePatch(variableProvider: VariableProvider, dataProvider: OutputStateProvider): MutableOutputStatePatch {
    val processedData: MutableOutputStateMap = mutableMapOf()
    for (output in variableProvider.outputs()) {
        // Check that the output has an assigned value.
        require(dataProvider.variableProvider.hasVariable(output)) { "No value provided for the output ${output.name}" }

        // Get the value from the provider.
        val value = when (output) {
            is OutputScalar -> arrayOf(dataProvider[output])
            is OutputVector -> dataProvider[output]
            else -> throw IllegalStateException()
        }

        // Put value into map.
        processedData[output.name] = value
    }
    return MutableOutputStatePatch(variableProvider, processedData)
}

internal fun generateInitialOutputStateStorage(
    kernelVariant: KernelVariantImpl,
    settingStateProvider: SettingStateProvider,
): MutableOutputStatePatch {
    val scalarInitializationContext = OutputScalarInitializationContext(kernelVariant, settingStateProvider)
    val vectorInitializationContext = OutputVectorInitializationContext(kernelVariant, settingStateProvider)
    val outputStateStorage = mutableOutputStatePatch(
        kernelVariant,
        kernelVariant.outputs().associate { output ->
            when (output) {
                is OutputScalar -> {
                    output.name to arrayOf(
                        output.initialization(scalarInitializationContext)
                    )
                }
                is OutputVector -> {
                    output.name to Array(kernelVariant.size(output)) { index ->
                        output.initialization(vectorInitializationContext, index)
                    }
                }
                is OutputVectorElement -> {
                    throw UnsupportedOperationException("Vector elements cannot be initialized directly")
                }
            }
        }
    )
    return outputStateStorage
}
