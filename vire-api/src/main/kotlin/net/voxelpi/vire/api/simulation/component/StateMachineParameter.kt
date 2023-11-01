package net.voxelpi.vire.api.simulation.component

/**
 * A parameter of a state machine.
 * Parameters can be used the configure the state machine as they can be accessed and modified externally.
 *
 * @property name the name of the parameter.
 * @property initialValue the initial value of the variable.
 * @property predicate the predicate a value must satisfy to be allowed.
 */
data class StateMachineParameter<T>(
    val name: String,
    val initialValue: T,
    val predicate: (value: T, context: StateMachineParameterContext) -> Boolean = { _, _ -> true },
) {

    /**
     * Creates a parameter, whose predicate doesn't depend on the parameter context.
     */
    constructor(name: String, initialValue: T, predicate: (value: T) -> Boolean) :
        this(name, initialValue, { value, _ -> predicate(value) })

    companion object {

        /**
         * A boolean parameter with the given [name] and [initialValue].
         */
        @Suppress("FunctionName")
        fun Boolean(
            name: String,
            initialValue: Boolean,
        ): StateMachineParameter<Boolean> {
            return StateMachineParameter(name, initialValue)
        }

        /**
         * A byte parameter with the given [name] and [initialValue]. The value of the parameter must be between [min] and [max].
         */
        @Suppress("FunctionName")
        fun Byte(
            name: String,
            initialValue: Byte,
            min: Byte = Byte.MIN_VALUE,
            max: Byte = Byte.MAX_VALUE,
        ): StateMachineParameter<Byte> {
            require(min <= max)
            return StateMachineParameter(name, initialValue) { value ->
                value in min..max
            }
        }

        /**
         * A short parameter with the given [name] and [initialValue]. The value of the parameter must be between [min] and [max].
         */
        @Suppress("FunctionName")
        fun Short(
            name: String,
            initialValue: Short,
            min: Short = Short.MIN_VALUE,
            max: Short = Short.MAX_VALUE,
        ): StateMachineParameter<Short> {
            require(min <= max)
            return StateMachineParameter(name, initialValue) { value ->
                value in min..max
            }
        }

        /**
         * An int parameter with the given [name] and [initialValue]. The value of the parameter must be between [min] and [max].
         */
        @Suppress("FunctionName")
        fun Int(
            name: String,
            initialValue: Int,
            min: Int = Int.MIN_VALUE,
            max: Int = Int.MAX_VALUE,
        ): StateMachineParameter<Int> {
            require(min <= max)
            return StateMachineParameter(name, initialValue) { value ->
                value in min..max
            }
        }

        /**
         * A long parameter with the given [name] and [initialValue]. The value of the parameter must be between [min] and [max].
         */
        @Suppress("FunctionName")
        fun Long(
            name: String,
            initialValue: Long,
            min: Long = Long.MIN_VALUE,
            max: Long = Long.MAX_VALUE,
        ): StateMachineParameter<Long> {
            require(min <= max)
            return StateMachineParameter(name, initialValue) { value ->
                value in min..max
            }
        }

        /**
         * An unsigned byte parameter with the given [name] and [initialValue]. The value of the parameter must be between [min] and [max].
         */
        @Suppress("FunctionName")
        fun UByte(
            name: String,
            initialValue: UByte,
            min: UByte = UByte.MIN_VALUE,
            max: UByte = UByte.MAX_VALUE,
        ): StateMachineParameter<UByte> {
            require(min <= max)
            return StateMachineParameter(name, initialValue) { value ->
                value in min..max
            }
        }

        /**
         * An unsigned short parameter with the given [name] and [initialValue]. The value of the parameter must be between [min] and [max].
         */
        @Suppress("FunctionName")
        fun UShort(
            name: String,
            initialValue: UShort,
            min: UShort = UShort.MIN_VALUE,
            max: UShort = UShort.MAX_VALUE,
        ): StateMachineParameter<UShort> {
            require(min <= max)
            return StateMachineParameter(name, initialValue) { value ->
                value in min..max
            }
        }

        /**
         * An unsigned int parameter with the given [name] and [initialValue]. The value of the parameter must be between [min] and [max].
         */
        @Suppress("FunctionName")
        fun UInt(
            name: String,
            initialValue: UInt,
            min: UInt = UInt.MIN_VALUE,
            max: UInt = UInt.MAX_VALUE,
        ): StateMachineParameter<UInt> {
            require(min <= max)
            return StateMachineParameter(name, initialValue) { value ->
                value in min..max
            }
        }

        /**
         * An unsigned long parameter with the given [name] and [initialValue]. The value of the parameter must be between [min] and [max].
         */
        @Suppress("FunctionName")
        fun ULong(
            name: String,
            initialValue: ULong,
            min: ULong = ULong.MIN_VALUE,
            max: ULong = ULong.MAX_VALUE,
        ): StateMachineParameter<ULong> {
            require(min <= max)
            return StateMachineParameter(name, initialValue) { value ->
                value in min..max
            }
        }
    }
}
