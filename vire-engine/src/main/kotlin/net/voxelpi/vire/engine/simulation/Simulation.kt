package net.voxelpi.vire.engine.simulation

import net.voxelpi.event.EventScope
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.environment.EnvironmentImpl
import net.voxelpi.vire.engine.kernel.KernelVariant
import net.voxelpi.vire.engine.kernel.KernelVariantImpl

/**
 * A simulation of a logic circuit.
 */
public interface Simulation {

    /**
     * The environment of the simulation.
     */
    public val environment: Environment

    /**
     * The simulated kernel.
     */
    public val kernelVariant: KernelVariant

    /**
     * The event scope of the environment.
     */
    public val eventScope: EventScope

    /**
     * Simulates the given number over [steps].
     */
    public fun simulateSteps(steps: Int)

    public fun stepBack(steps: Int): SimulationState

    public fun stepForward(steps: Int): SimulationState

    public fun configureInputs(block: SimulationInputConfiguration.() -> Unit)

    public fun configureInputs(values: Map<String, Array<LogicState>>)

    /**
     * The number of steps that have been simulated.
     */
    public val simulatedSteps: Int

    /**
     * Returns the state of the simulation in the given [step].
     * @param step the step of which the state should be returned. Must be 1<=n<=[simulatedSteps].
     */
    public fun state(step: Int): SimulationState

    public val latestState: SimulationState

    public val currentState: SimulationState
}

internal class SimulationImpl(
    override val environment: EnvironmentImpl,
    override val kernelVariant: KernelVariantImpl,
) : Simulation {

    // TODO: Should this be a sub-scope of the circuit event scope instead?
    override val eventScope: EventScope = environment.eventScope.createSubScope()

    val history: SimulationHistory = SimulationHistory(kernelVariant)

    override fun simulateSteps(steps: Int) {
        TODO("Not yet implemented")
    }

    override fun stepBack(steps: Int): SimulationState {
        TODO("Not yet implemented")
    }

    override fun stepForward(steps: Int): SimulationState {
        TODO("Not yet implemented")
    }

    override fun configureInputs(block: SimulationInputConfiguration.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun configureInputs(values: Map<String, Array<LogicState>>) {
        TODO("Not yet implemented")
    }

    override var simulatedSteps: Int
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun state(step: Int): SimulationStateImpl {
        TODO("Not yet implemented")
    }

    override val latestState: SimulationState
        get() = TODO("Not yet implemented")

    override val currentState: SimulationState
        get() = TODO("Not yet implemented")
}
