package net.voxelpi.vire.api.simulation.event.simulation.component

import net.voxelpi.vire.api.simulation.component.Component
import net.voxelpi.vire.api.simulation.component.StateMachineParameter

/**
 * An event that is called when a parameter of a state machine is modified.
 * Note that this function is not called when the parameter modification is caused by a component reset.
 * @property newValue the new variable.
 * @property oldValue the old variable.
 */
data class ComponentModifyParameterEvent<T>(
    override val component: Component,
    val parameter: StateMachineParameter<T>,
    val newValue: T,
    val oldValue: T,
) : ComponentEvent
