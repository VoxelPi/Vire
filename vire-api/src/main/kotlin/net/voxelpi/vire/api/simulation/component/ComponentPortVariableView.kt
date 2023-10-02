package net.voxelpi.vire.api.simulation.component

/**
 * Information about the variable that is bound to the port.
 * @property variable the variable vector instance.
 * @property index the index of the variable inside the variable vector.
 */
data class ComponentPortVariableView(
    val variable: ComponentPortVariable,
    val index: Int,
) {

    /**
     * A string representing both the variable and the index.
     */
    val identifier: String
        get() = "${variable.name}[$index]"
}
