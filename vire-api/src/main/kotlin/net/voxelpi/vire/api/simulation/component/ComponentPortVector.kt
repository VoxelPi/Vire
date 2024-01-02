package net.voxelpi.vire.api.simulation.component

/**
 * An io vector that can be assigned to a component port.
 */
interface ComponentPortVector {

    /**
     * The name of the vector.
     */
    val name: String

    /**
     * Returns a variable that points to the element at the specified [index] of the vector.
     */
    fun variable(index: Int = 0): ComponentPortVectorVariable {
        return ComponentPortVectorVariable(this, index)
    }
}
