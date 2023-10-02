package net.voxelpi.vire.api.simulation.component

/**
 * A variable that can be assigned to a component interface.
 */
sealed interface ComponentPortVariable {

    /**
     * The name of the variable.
     */
    val name: String
}
