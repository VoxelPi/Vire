package net.voxelpi.vire.api.simulation.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.statemachine.annotation.StateMachineTemplate
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface StateMachineFactory {

    /**
     * Creates a new state machine.
     */
    fun create(
        id: Identifier,
        init: StateMachine.Builder.() -> Unit,
    ): StateMachine

    /**
     * Generates a new state machine from the given [type].
     */
    fun generate(type: KClass<out StateMachineTemplate>): StateMachine

    /**
     * Creates a new input.
     */
    fun createInput(
        name: String,
        initialSize: StateMachineIOState.InitialSizeProvider,
    ): StateMachineInput

    /**
     * Creates a new output.
     */
    fun createOutput(
        name: String,
        initialSize: StateMachineIOState.InitialSizeProvider,
        initialValue: LogicState,
    ): StateMachineOutput

    /**
     * Creates a new variable.
     */
    fun <T> createVariable(
        name: String,
        type: KType,
        initialValue: T,
    ): StateMachineVariable<T>

    /**
     * Creates a new unconstrained parameter.
     */
    fun <T> createUnconstrainedParameter(
        name: String,
        type: KType,
        initialValue: T,
    ): StateMachineParameter.Unconstrained<T>

    /**
     * Creates a new predicate parameter.
     */
    fun <T> createPredicateParameter(
        name: String,
        type: KType,
        initialValue: T,
        predicate: (value: T) -> Boolean,
    ): StateMachineParameter.Predicate<T>

    /**
     * Creates a new selection parameter.
     */
    fun <T> createSelectionParameter(
        name: String,
        type: KType,
        initialValue: T,
        selection: Collection<T>,
    ): StateMachineParameter.Selection<T>

    /**
     * Creates a new range parameter.
     */
    fun <T : Comparable<T>> createRangeParameter(
        name: String,
        type: KType,
        initialValue: T,
        min: T,
        max: T,
    ): StateMachineParameter.Range<T>
}
