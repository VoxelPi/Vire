package net.voxelpi.vire.stdlib.kernel

import net.voxelpi.vire.engine.BooleanState
import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.LogicValue
import net.voxelpi.vire.engine.SignalActivation
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider
import net.voxelpi.vire.engine.kernel.kernel
import net.voxelpi.vire.engine.kernel.variable.InputScalar
import net.voxelpi.vire.engine.kernel.variable.OutputScalar
import net.voxelpi.vire.engine.kernel.variable.Setting
import net.voxelpi.vire.engine.kernel.variable.input
import net.voxelpi.vire.engine.kernel.variable.output
import net.voxelpi.vire.engine.kernel.variable.setting
import net.voxelpi.vire.stdlib.VIRE_STDLIB_ID

public object DLatch : KernelProvider {

    public val gateActivation: Setting<SignalActivation> = setting("gate_activation", initialization = { SignalActivation.ACTIVE_LOW })
    public val gate: InputScalar = input("gate")

    public val data: InputScalar = input("data")
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel(Identifier(VIRE_STDLIB_ID, "d_latch")) {
        declare(gateActivation)
        declare(gate)

        declare(data)
        declare(output)

        onUpdate { context ->
            if (context[gateActivation].isActive(context[gate])) {
                context[output] = context[data]
            }
        }
    }
}

public object SRLatch : KernelProvider {

    public val gateActivation: Setting<SignalActivation> = setting("gate_activation", initialization = { SignalActivation.ACTIVE_LOW })
    public val gate: InputScalar = input("gate")

    public val set: InputScalar = input("set")
    public val reset: InputScalar = input("reset")
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel(Identifier(VIRE_STDLIB_ID, "sr_latch")) {
        declare(gateActivation)
        declare(gate)

        declare(set)
        declare(reset)
        declare(output)

        onUpdate { context ->
            // Check if the gate is open.
            if (!context[gateActivation].isActive(context[gate])) {
                return@onUpdate
            }

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

public object JKLatch : KernelProvider {

    public val gateActivation: Setting<SignalActivation> = setting("gate_activation", initialization = { SignalActivation.ACTIVE_LOW })
    public val gate: InputScalar = input("gate")

    public val j: InputScalar = input("j")
    public val k: InputScalar = input("k")
    public val output: OutputScalar = output("output")

    override val kernel: Kernel = kernel(Identifier(VIRE_STDLIB_ID, "jk_latch")) {
        declare(gateActivation)
        declare(gate)

        declare(j)
        declare(k)
        declare(output)

        onUpdate { context ->
            // Check if the gate is open.
            if (!context[gateActivation].isActive(context[gate])) {
                return@onUpdate
            }

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
