package net.voxelpi.vire.api.circuit.component

/**
 * An element of an input or output vector that is bound to a port.
 * @property vector the input or output vector instance.
 * @property index the index of the element in the vector.
 */
data class ComponentPortVectorVariable(
    val vector: ComponentPortVector,
    val index: Int,
) {

    /**
     * The name of the variable. Consists of the name of the vector name and the element index.
     */
    val name: String
        get() = "${vector.name}[$index]"
}
