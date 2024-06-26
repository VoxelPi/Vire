package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.BooleanState
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
import net.voxelpi.vire.engine.kernel.variable.createField
import net.voxelpi.vire.engine.kernel.variable.createInput
import net.voxelpi.vire.engine.kernel.variable.createOutput
import net.voxelpi.vire.engine.kernel.variable.createSetting

public object DFlipFlop : KernelProvider {

    public val clockEdge: Setting<SignalEdge> = createSetting("clock_edge") {
        initialization = { SignalEdge.FALLING_EDGE }
    }
    public val clockPrevious: Field<Boolean> = createField("clock_previous") {
        initialization = { false }
    }

    public val data: InputScalar = createInput("data")
    public val clock: InputScalar = createInput("clock")
    public val output: OutputScalar = createOutput("output")

    override val kernel: Kernel = kernel {
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

    public val clockEdge: Setting<SignalEdge> = createSetting("clock_edge") {
        initialization = { SignalEdge.FALLING_EDGE }
    }
    public val clockPrevious: Field<Boolean> = createField("clock_previous") {
        initialization = { false }
    }

    public val toggle: InputScalar = createInput("toggle")
    public val clock: InputScalar = createInput("clock")
    public val output: OutputScalar = createOutput("output")

    override val kernel: Kernel = kernel {
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

    public val clockEdge: Setting<SignalEdge> = createSetting("clock_edge") {
        initialization = { SignalEdge.FALLING_EDGE }
    }
    public val clockPrevious: Field<Boolean> = createField("clock_previous") {
        initialization = { false }
    }

    public val set: InputScalar = createInput("set")
    public val reset: InputScalar = createInput("reset")
    public val clock: InputScalar = createInput("clock")
    public val output: OutputScalar = createOutput("output")

    override val kernel: Kernel = kernel {
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

    public val clockEdge: Setting<SignalEdge> = createSetting("clock_edge") {
        initialization = { SignalEdge.FALLING_EDGE }
    }
    public val clockPrevious: Field<Boolean> = createField("clock_previous") {
        initialization = { false }
    }

    public val j: InputScalar = createInput("j")
    public val k: InputScalar = createInput("k")
    public val clock: InputScalar = createInput("clock")
    public val output: OutputScalar = createOutput("output")

    override val kernel: Kernel = kernel {
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
