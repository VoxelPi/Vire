package net.voxelpi.vire.engine.circuit.kernel

public interface KernelState {

    /**
     * The kernel configuration from which this state was generated.
     */
    public val kernelInstance: KernelInstance

    public companion object {

        /**
         * Creates a new state of the given [kernelInstance].
         * @param kernelInstance the kernelInstance of which a new state should be created.
         */
        public fun create(kernelInstance: KernelInstance): KernelState {
            require(kernelInstance is KernelInstanceImpl)
            return KernelStateImpl.create(kernelInstance)
        }

        /**
         * Creates a new state of the given [kernelInstance] that is initialized using the given [block].
         * Values are initialized to their default values before the block is applied.
         * @param kernelInstance the kernelInstance of which a new state should be created.
         * @param block the code that should be applied to the kernel configuration.
         */
        public fun create(kernelInstance: KernelInstance, block: KernelInitialization.() -> Unit): KernelState {
            require(kernelInstance is KernelInstanceImpl)
            return KernelStateImpl.create(kernelInstance, block)
        }

        /**
         * Creates a new state of the given [kernelInstance] that is initialized using the given [values].
         * Values are initialized to their default values if not specified in the values map.
         * @param kernelInstance the kernelInstance of which a new state should be created.
         * @param values the values that should be applied to the kernel configuration.
         */
        public fun create(kernelInstance: KernelInstance, values: Map<String, Any?>): KernelState {
            require(kernelInstance is KernelInstanceImpl)
            return KernelStateImpl.create(kernelInstance, values)
        }
    }
}

internal class KernelStateImpl(
    override val kernelInstance: KernelInstance,
) : KernelState {

    companion object {
        fun create(kernelInstance: KernelInstanceImpl): KernelStateImpl {
//        val state = KernelStateImpl(kernelInstance, kernel.generateDefaultConfiguration())
//        return state
            TODO()
        }

        fun create(kernelInstance: KernelInstanceImpl, block: KernelInitialization.() -> Unit): KernelStateImpl {
//        val config = kernel.generateDefaultConfiguration()
//        config.block()
//        return KernelStateImpl(kernelInstance, config)
            TODO()
        }

        fun create(kernelInstance: KernelInstanceImpl, values: Map<String, Any?>): KernelStateImpl {
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
}
