package net.voxelpi.vire.api.simulation.statemachine

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.Vire
import net.voxelpi.vire.api.simulation.LogicState
import net.voxelpi.vire.api.simulation.LogicValue
import net.voxelpi.vire.api.simulation.statemachine.annotation.StateMachineTemplate
import kotlin.reflect.KClass

interface StateMachine {

    /**
     * The id of the state machine.
     */
    val id: Identifier

    /**
     * All registered parameters of the state machine.
     */
    val parameters: Map<String, StateMachineParameter<*>>

    /**
     * All registered variables of the state machine.
     */
    val variables: Map<String, StateMachineVariable<*>>

    /**
     * All registered inputs of the state machine.
     */
    val inputs: Map<String, StateMachineInput>

    /**
     * All registered outputs of the state machine.
     */
    val outputs: Map<String, StateMachineOutput>

    /**
     * The configuration action of the state machine.
     */
    val configure: (StateMachineConfigureContext) -> Unit

    /**
     * The update action fo the state machine.
     */
    val update: (StateMachineUpdateContext) -> Unit

    /**
     * Builder for a state machine.
     */
    abstract class Builder {

        /**
         * The id of the state machine.
         */
        abstract val id: Identifier

        /**
         * The configuration action of the state machine.
         */
        abstract var configure: (StateMachineConfigureContext) -> Unit

        /**
         * The update action fo the state machine.
         */
        abstract var update: (StateMachineUpdateContext) -> Unit

        /**
         * Declares the given [parameter] on the state machine.
         */
        abstract fun <T, U : StateMachineParameter<T>> declare(parameter: U): U

        /**
         * Declares the given [variable] on the state machine.
         */
        abstract fun <T> declare(variable: StateMachineVariable<T>): StateMachineVariable<T>

        /**
         * Declares the given [input] on the state machine.
         */
        abstract fun declare(input: StateMachineInput): StateMachineInput

        /**
         * Declares the given [output] on the state machine.
         */
        abstract fun declare(output: StateMachineOutput): StateMachineOutput

        // region specific functions for state declaration

        /**
         * Declares a new unconstrained parameter with the given [name] and [initialValue].
         */
        inline fun <reified T> declareParameter(
            name: String,
            initialValue: T,
        ): StateMachineParameter.Unconstrained<T> {
            return declare(parameter(name, initialValue))
        }

        /**
         * Declares a new parameter with the given [name], [initialValue] and [predicate].
         */
        inline fun <reified T> declareParameter(
            name: String,
            initialValue: T,
            noinline predicate: (value: T) -> Boolean,
        ): StateMachineParameter.Predicate<T> {
            return declare(parameter(name, initialValue, predicate))
        }

        /**
         * Declares a new selection parameter with the given [name] and [initialValue].
         * The value of the parameter must be one of [selection].
         */
        inline fun <reified T> declareParameter(
            name: String,
            initialValue: T,
            selection: Collection<T>,
        ): StateMachineParameter.Selection<T> {
            return declare(parameter(name, initialValue, selection))
        }

        /**
         * Declares a new selection parameter with the given [name] and [initialValue].
         * The value of the parameter must be one of [selection].
         */
        inline fun <reified T> declareParameter(
            name: String,
            initialValue: T,
            vararg selection: T,
        ): StateMachineParameter.Selection<T> {
            return declare(parameter(name, initialValue, selection.toList()))
        }

        /**
         * Declares a new byte parameter with the given [name] and [initialValue].
         * The value must be in the range of [min] to [max].
         */
        fun declareParameter(
            name: String,
            initialValue: Byte,
            min: Byte = Byte.MIN_VALUE,
            max: Byte = Byte.MAX_VALUE,
        ): StateMachineParameter.Range<Byte> {
            return declare(parameter(name, initialValue, min, max))
        }

        /**
         * Declares a new short with the given [name] and [initialValue].
         * The value must be in the range of [min] to [max].
         */
        fun declareParameter(
            name: String,
            initialValue: Short,
            min: Short = Short.MIN_VALUE,
            max: Short = Short.MAX_VALUE,
        ): StateMachineParameter.Range<Short> {
            return declare(parameter(name, initialValue, min, max))
        }

        /**
         * Declares a new int parameter with the given [name] and [initialValue].
         * The value must be in the range of [min] to [max].
         */
        fun declareParameter(
            name: String,
            initialValue: Int,
            min: Int = Int.MIN_VALUE,
            max: Int = Int.MAX_VALUE,
        ): StateMachineParameter.Range<Int> {
            return declare(parameter(name, initialValue, min, max))
        }

        /**
         * Declares a new int parameter with the given [name] and [initialValue].
         * The value must be in the given [range].
         */
        fun declareParameter(
            name: String,
            initialValue: Int,
            range: IntRange,
        ): StateMachineParameter.Range<Int> {
            return declare(parameter(name, initialValue, range))
        }

        /**
         * Declares a new long parameter with the given [name] and [initialValue].
         * The value must be in the range of [min] to [max].
         */
        fun declareParameter(
            name: String,
            initialValue: Long,
            min: Long = Long.MIN_VALUE,
            max: Long = Long.MAX_VALUE,
        ): StateMachineParameter.Range<Long> {
            return declare(parameter(name, initialValue, min, max))
        }

        /**
         * Declares a new long parameter with the given [name] and [initialValue].
         * The value must be in the given [range].
         */
        fun declareParameter(
            name: String,
            initialValue: Long,
            range: LongRange,
        ): StateMachineParameter.Range<Long> {
            return declare(parameter(name, initialValue, range))
        }

        /**
         * Declares a new unsigned byte parameter with the given [name] and [initialValue].
         * The value must be in the range of [min] to [max].
         */
        fun declareParameter(
            name: String,
            initialValue: UByte,
            min: UByte = UByte.MIN_VALUE,
            max: UByte = UByte.MAX_VALUE,
        ): StateMachineParameter.Range<UByte> {
            return declare(parameter(name, initialValue, min, max))
        }

        /**
         * Declares a new unsigned short parameter with the given [name] and [initialValue].
         * The value must be in the range of [min] to [max].
         */
        fun declareParameter(
            name: String,
            initialValue: UShort,
            min: UShort = UShort.MIN_VALUE,
            max: UShort = UShort.MAX_VALUE,
        ): StateMachineParameter.Range<UShort> {
            return declare(parameter(name, initialValue, min, max))
        }

        /**
         * Declares a new unsigned int parameter with the given [name] and [initialValue].
         * The value must be in the range of [min] to [max].
         */
        fun declareParameter(
            name: String,
            initialValue: UInt,
            min: UInt = UInt.MIN_VALUE,
            max: UInt = UInt.MAX_VALUE,
        ): StateMachineParameter.Range<UInt> {
            return declare(parameter(name, initialValue, min, max))
        }

        /**
         * Declares a new unsigned int parameter with the given [name] and [initialValue].
         * The value must be in the given [range].
         */
        fun declareParameter(
            name: String,
            initialValue: UInt,
            range: UIntRange,
        ): StateMachineParameter.Range<UInt> {
            return declare(parameter(name, initialValue, range))
        }

        /**
         * Declares a new unsigned long parameter with the given [name] and [initialValue].
         * The value must be in the range of [min] to [max].
         */
        fun declareParameter(
            name: String,
            initialValue: ULong,
            min: ULong = ULong.MIN_VALUE,
            max: ULong = ULong.MAX_VALUE,
        ): StateMachineParameter.Range<ULong> {
            return declare(parameter(name, initialValue, min, max))
        }

        /**
         * Declares a new unsigned long parameter with the given [name] and [initialValue].
         * The value must be in the range of [min] to [max].
         */
        fun declareParameter(
            name: String,
            initialValue: ULong,
            range: ULongRange,
        ): StateMachineParameter.Range<ULong> {
            return declare(parameter(name, initialValue, range))
        }

        /**
         * Declares a new float parameter with the given [name] and [initialValue].
         * The value must be in the range of [min] to [max].
         */
        fun declareParameter(
            name: String,
            initialValue: Float,
            min: Float = Float.NEGATIVE_INFINITY,
            max: Float = Float.POSITIVE_INFINITY,
        ): StateMachineParameter.Range<Float> {
            return declare(parameter(name, initialValue, min, max))
        }

        /**
         * Declares a new float parameter with the given [name] and [initialValue].
         * The value must be in the given [range].
         */
        fun declareParameter(
            name: String,
            initialValue: Float,
            range: ClosedFloatingPointRange<Float>,
        ): StateMachineParameter.Range<Float> {
            return declare(parameter(name, initialValue, range))
        }

        /**
         * Declares a new double parameter with the given [name] and [initialValue].
         * The value must be in the range of [min] to [max].
         */
        fun declareParameter(
            name: String,
            initialValue: Double,
            min: Double = Double.NEGATIVE_INFINITY,
            max: Double = Double.POSITIVE_INFINITY,
        ): StateMachineParameter.Range<Double> {
            return declare(parameter(name, initialValue, min, max))
        }

        /**
         * Declares a new double parameter with the given [name] and [initialValue].
         * The value must be in the given [range].
         */
        fun declareParameter(
            name: String,
            initialValue: Double,
            range: ClosedFloatingPointRange<Double>,
        ): StateMachineParameter.Range<Double> {
            return declare(parameter(name, initialValue, range))
        }

        /**
         * Declares a new variable.
         */
        inline fun <reified T> declareVariable(
            name: String,
            initialValue: T,
        ): StateMachineVariable<T> {
            return declare(variable(name, initialValue))
        }

        /**
         * Declares a new input.
         */
        fun declareInput(
            name: String,
            initialSize: Int = 1,
        ): StateMachineInput {
            return declare(input(name, initialSize))
        }

        /**
         * Declares a new input.
         */
        fun declareInput(
            name: String,
            initialSize: StateMachineParameter<out Number>,
        ): StateMachineInput {
            return declare(input(name, initialSize))
        }

        /**
         * Declares a new output.
         */
        fun declareOutput(
            name: String,
            initialSize: Int = 1,
            initialValue: LogicState = LogicState.value(LogicValue.NONE),
        ): StateMachineOutput {
            return declare(output(name, initialSize, initialValue))
        }

        /**
         * Declares a new output.
         */
        fun declareOutput(
            name: String,
            initialSize: StateMachineParameter<out Number>,
            initialValue: LogicState = LogicState.value(LogicValue.NONE),
        ): StateMachineOutput {
            return declare(output(name, initialSize, initialValue))
        }

        // endregion

        /**
         * Creates the state machine.
         */
        abstract fun create(): StateMachine
    }

    companion object {

        /**
         * Creates a new state machine.
         */
        fun create(id: Identifier, init: Builder.() -> Unit): StateMachine {
            return Vire.stateMachineFactory.get().create(id, init)
        }

        /**
         * Creates a new state machine from the given template [type].
         */
        fun create(type: KClass<out StateMachineTemplate>): StateMachine {
            return Vire.stateMachineFactory.get().create(type)
        }

        /**
         * Creates a new state machine from the given template [type].
         */
        inline fun <reified T : StateMachineTemplate> create(): StateMachine {
            return Vire.stateMachineFactory.get().create(T::class)
        }
    }
}
