package net.voxelpi.vire.engine.circuit.kernel

import net.voxelpi.vire.engine.circuit.kernel.variable.IOVectorSizeProvider
import net.voxelpi.vire.engine.circuit.kernel.variable.Input
import net.voxelpi.vire.engine.circuit.kernel.variable.Output
import net.voxelpi.vire.engine.circuit.kernel.variable.Parameter
import net.voxelpi.vire.engine.circuit.kernel.variable.ParameterStateProvider
import net.voxelpi.vire.engine.circuit.kernel.variable.Setting
import net.voxelpi.vire.engine.circuit.kernel.variable.SettingStateProvider

public interface KernelInstance : ParameterStateProvider, SettingStateProvider, IOVectorSizeProvider {

    /**
     * The kernel configuration from which this state was generated.
     */
    public val kernelVariant: KernelVariant



    public companion object {

        /**
         * Creates a new state of the given [kernelVariant].
         * @param kernelVariant the kernelInstance of which a new state should be created.
         */
        public fun create(kernelVariant: KernelVariant): KernelInstance {
            require(kernelVariant is KernelVariantImpl)
            return KernelInstanceImpl.create(kernelVariant)
        }

        /**
         * Creates a new state of the given [kernelVariant] that is initialized using the given [block].
         * Values are initialized to their default values before the block is applied.
         * @param kernelVariant the kernelInstance of which a new state should be created.
         * @param block the code that should be applied to the kernel configuration.
         */
        public fun create(kernelVariant: KernelVariant, block: KernelInitialization.() -> Unit): KernelInstance {
            require(kernelVariant is KernelVariantImpl)
            return KernelInstanceImpl.create(kernelVariant, block)
        }

        /**
         * Creates a new state of the given [kernelVariant] that is initialized using the given [values].
         * Values are initialized to their default values if not specified in the values map.
         * @param kernelVariant the kernelInstance of which a new state should be created.
         * @param values the values that should be applied to the kernel configuration.
         */
        public fun create(kernelVariant: KernelVariant, values: Map<String, Any?>): KernelInstance {
            require(kernelVariant is KernelVariantImpl)
            return KernelInstanceImpl.create(kernelVariant, values)
        }
    }
}

internal class KernelInstanceImpl(
    override val kernelVariant: KernelVariant,
) : KernelInstance {

    companion object {
        fun create(kernelInstance: KernelVariantImpl): KernelInstanceImpl {
//        val state = KernelStateImpl(kernelInstance, kernel.generateDefaultConfiguration())
//        return state
            TODO()
        }

        fun create(kernelInstance: KernelVariantImpl, block: KernelInitialization.() -> Unit): KernelInstanceImpl {
//        val config = kernel.generateDefaultConfiguration()
//        config.block()
//        return KernelStateImpl(kernelInstance, config)
            TODO()
        }

        fun create(kernelInstance: KernelVariantImpl, values: Map<String, Any?>): KernelInstanceImpl {
//        val config = kernelInstance.generateDefaultConfiguration()
//        for ((parameterName, parameterValue) in values) {
//            // Check that only existing parameters are specified.
//            val parameter = kernelInstance.parameter(parameterName)
//                ?: throw IllegalArgumentException("Unknown parameter '$parameterName'")
//
//            // Check that the value is valid for the parameter.
//            require(parameter.isValidValue(parameterValue)) { "Invalid value for the parameter ${parameter.name}" }
//            config[parameterName] = parameterValue
//        }
//        return KernelStateImpl(kernel, config)
            TODO()
        }
    }

    override fun <T> get(parameter: Parameter<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T> get(setting: Setting<T>): T {
        TODO("Not yet implemented")
    }

    override fun size(input: Input): Int {
        TODO("Not yet implemented")
    }

    override fun size(output: Output): Int {
        TODO("Not yet implemented")
    }
}
