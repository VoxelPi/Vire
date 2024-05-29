package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.BooleanState
import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.LogicValue
import net.voxelpi.vire.engine.SignalEdge
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.Field
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.field
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.setting
import net.voxelpi.vire.stdlib.VIRE_STDLIB_ID

public object DFlipFlop : KernelProvider {

    public val clockEdge: Setting<SignalEdge> = setting("clock_edge", initialization = { SignalEdge.FALLING_EDGE })
    public val clockPrevious: Field<Boolean> = field("clock_previous", initialization = { false })

    public val data: InputScalar = input("data")
    public val clock: InputScalar = input("clock")
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel(Identifier(VIRE_STDLIB_ID, "d_flip_flop")) {
        declare(clockEdge)
        declare(clockPrevious)

        declare(data)
        declare(clock)
        declare(output)

        onUpdate { context ->
            // Skip if the clock signal has not changed.
            val clockPreviousState = context[clockPrevious]
            val clockCurrentState = context[clock].toBoolean()
            context[clockPrevious] = clockCurrentState

            // Update to output if a clock edge is detected.
            if (context[clockEdge].isTriggered(clockPreviousState, clockCurrentState)) {
                context[output] = context[data]
            }
        }
    }
}

public object TFlipFlop : KernelProvider {

    public val clockEdge: Setting<SignalEdge> = setting("clock_edge", initialization = { SignalEdge.FALLING_EDGE })
    public val clockPrevious: Field<Boolean> = field("clock_previous", initialization = { false })

    public val toggle: InputScalar = input("toggle")
    public val clock: InputScalar = input("clock")
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel(Identifier(VIRE_STDLIB_ID, "t_flip_flop")) {
        declare(clockEdge)
        declare(clockPrevious)

        declare(toggle)
        declare(clock)
        declare(output)

        onUpdate { context ->
            // Skip if the clock signal has not changed.
            val clockPreviousState = context[clockPrevious]
            val clockCurrentState = context[clock].toBoolean()
            context[DFlipFlop.clockPrevious] = clockCurrentState

            // Update to output if a clock edge is detected.
            if (context[clockEdge].isTriggered(clockPreviousState, clockCurrentState)) {
                if (context[toggle].toBoolean()) {
                    context[output] = BooleanState.value(!context[output].toBoolean())
                }
            }
        }
    }
}

public object SRFlipFlop : KernelProvider {

    public val clockEdge: Setting<SignalEdge> = setting("clock_edge", initialization = { SignalEdge.FALLING_EDGE })
    public val clockPrevious: Field<Boolean> = field("clock_previous", initialization = { false })

    public val set: InputScalar = input("set")
    public val reset: InputScalar = input("reset")
    public val clock: InputScalar = input("clock")
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel(Identifier(VIRE_STDLIB_ID, "sr_flip_flop")) {
        declare(clockEdge)
        declare(clockPrevious)

        declare(set)
        declare(reset)
        declare(clock)
        declare(output)

        onUpdate { context ->
            // Skip if the clock signal has not changed.
            val clockPreviousState = context[clockPrevious]
            val clockCurrentState = context[clock].toBoolean()
            context[clockPrevious] = clockCurrentState

            // Update to output if a clock edge is detected.
            if (context[clockEdge].isTriggered(clockPreviousState, clockCurrentState)) {
                // Update the state.
                val setState = context[set].toBoolean()
                val resetState = context[reset].toBoolean()
                if (setState) {
                    if (resetState) {
                        context[output] = LogicState.value(LogicValue.INVALID)
                    } else {
                        context[output] = BooleanState.value(true)
                    }
                } else {
                    context[output] = BooleanState.value(false)
                }
            }
        }
    }
}

public object JKFlipFlop : KernelProvider {

    public val clockEdge: Setting<SignalEdge> = setting("clock_edge", initialization = { SignalEdge.FALLING_EDGE })
    public val clockPrevious: Field<Boolean> = field("clock_previous", initialization = { false })

    public val j: InputScalar = input("j")
    public val k: InputScalar = input("k")
    public val clock: InputScalar = input("clock")
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel(Identifier(VIRE_STDLIB_ID, "jk_flip_flop")) {
        declare(clockEdge)
        declare(clockPrevious)

        declare(j)
        declare(k)
        declare(clock)
        declare(output)

        onUpdate { context ->
            // Skip if the clock signal has not changed.
            val clockPreviousState = context[clockPrevious]
            val clockCurrentState = context[clock].toBoolean()
            context[DFlipFlop.clockPrevious] = clockCurrentState

            // Update to output if a clock edge is detected.
            if (context[clockEdge].isTriggered(clockPreviousState, clockCurrentState)) {
                val jActive = context[j].toBoolean()
                val kActive = context[k].toBoolean()

                context[output] = when {
                    !jActive && !kActive -> context[output]
                    !jActive && kActive -> BooleanState.value(false).logicState()
                    jActive && !kActive -> BooleanState.value(true).logicState()
                    else -> BooleanState.value(!context[output].toBoolean()).logicState()
                }
            }
        }
    }
}
