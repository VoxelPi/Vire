package net.voxelpi.vire.api.circuit.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.circuit.Circuit
import net.voxelpi.vire.api.circuit.statemachine.annotation.StateMachineTemplate
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
     * Generates a new state machine from the given circuit.
     * For every component that is tagged with the [CircuitStateMachine.CIRCUIT_INPUT_TAG] an input variable is generated,
     * and for every component that is tagged with the [CircuitStateMachine.CIRCUIT_OUTPUT_TAG] an output variable is generated.
     */
    fun createFromCircuit(id: Identifier, circuit: Circuit): StateMachine

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
