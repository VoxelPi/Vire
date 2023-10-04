package net.voxelpi.vire.api.simulation.component

/**
 * A variable that can be assigned to a component interface.
 */
sealed interface ComponentPortVariable {

    /**
     * The name of the variable.
     */
    val name: String

    /**
     * Creates a view of the variable.
     */
    fun createView(index: Int = 0): ComponentPortVariableView {
        return ComponentPortVariableView(this, index)
    }
}
