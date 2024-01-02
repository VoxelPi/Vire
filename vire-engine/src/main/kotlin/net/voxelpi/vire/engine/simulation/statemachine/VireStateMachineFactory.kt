package net.voxelpi.vire.engine.simulation.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.statemachine.StateMachine
import net.voxelpi.vire.api.simulation.statemachine.StateMachineFactory
import net.voxelpi.vire.api.simulation.statemachine.StateMachineStateVector
import net.voxelpi.vire.api.simulation.statemachine.annotation.StateMachineTemplate
import kotlin.reflect.KClass
import kotlin.reflect.KType

class VireStateMachineFactory : StateMachineFactory {

    override fun create(
        id: Identifier,
        init: StateMachine.Builder.() -> Unit,
    ): VireStateMachine {
        val builder = VireStateMachine.Builder(id)
        builder.init()
        return builder.create()
    }

    override fun create(
        type: KClass<StateMachineTemplate>,
    ) {
        TODO("Not yet implemented")
    }

    override fun createInput(
        name: String,
    ): VireStateMachineInput {
        return VireStateMachineInput(name)
    }

    override fun createInputVector(
        name: String,
        initialSize: StateMachineStateVector.InitialSizeProvider,
    ): VireStateMachineInputVector {
        return VireStateMachineInputVector(name, initialSize)
    }

    override fun createOutput(
        name: String,
    ): VireStateMachineOutput {
        return VireStateMachineOutput(name)
    }

    override fun createOutputVector(
        name: String,
        initialSize: StateMachineStateVector.InitialSizeProvider,
    ): VireStateMachineOutputVector {
        return VireStateMachineOutputVector(name, initialSize)
    }

    override fun <T> createVariable(
        name: String,
        type: KType,
        initialValue: T,
    ): VireStateMachineVariable<T> {
        return VireStateMachineVariable(name, type, initialValue)
    }

    override fun <T> createVariableVector(
        name: String,
        type: KType,
        initialSize: StateMachineStateVector.InitialSizeProvider,
        initialValue: T,
    ): VireStateMachineVariableVector<T> {
        return VireStateMachineVariableVector(name, type, initialSize, initialValue)
    }

    override fun <T> createUnconstrainedParameter(
        name: String,
        type: KType,
        initialValue: T,
    ): VireStateMachineParameter.Unconstrained<T> {
        return VireStateMachineParameter.Unconstrained(name, type, initialValue)
    }

    override fun <T> createPredicateParameter(
        name: String,
        type: KType,
        initialValue: T,
        predicate: (value: T) -> Boolean,
    ): VireStateMachineParameter.Predicate<T> {
        return VireStateMachineParameter.Predicate(name, type, initialValue, predicate)
    }

    override fun <T> createSelectionParameter(
        name: String,
        type: KType,
        initialValue: T,
        selection: Collection<T>,
    ): VireStateMachineParameter.Selection<T> {
        return VireStateMachineParameter.Selection(name, type, initialValue, selection)
    }

    override fun <T : Comparable<T>> createRangeParameter(
        name: String,
        type: KType,
        initialValue: T,
        min: T,
        max: T,
    ): VireStateMachineParameter.Range<T> {
        return VireStateMachineParameter.Range(name, type, initialValue, min, max)
    }
}
