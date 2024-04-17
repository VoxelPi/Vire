package net.voxelpi.vire.engine.circuit.kernel.variable

/**
 * Defines how a kernel variable is initialized.
 */
public sealed interface VariableInitialization<T> {

    /**
     * The kernel variable is initialized to the constant [value].
     */
    public data class Constant<T>(
        val value: T,
    ) : VariableInitialization<T>

    /**
     * The kernel variable is initialized to the value returned by the [provider]-
     */
    public data class Dynamic<T>(
        val provider: () -> T,
    ) : VariableInitialization<T>

    // TODO: Initialization to a kernel parameter or kernel variable (possible of circuit if part of a state machine)

    public companion object {

        public fun <T> constant(value: T): Constant<T> {
            return Constant(value)
        }

        public fun <T> dynamic(provider: () -> T): Dynamic<T> {
            return Dynamic(provider)
        }
    }
}
