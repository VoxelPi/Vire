package net.voxelpi.vire.engine.simulation

import net.voxelpi.event.EventScope
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.environment.EnvironmentImpl
import net.voxelpi.vire.engine.kernel.KernelInstance
import net.voxelpi.vire.engine.kernel.KernelInstanceImpl
import net.voxelpi.vire.engine.kernel.KernelState
import net.voxelpi.vire.engine.kernel.MutableKernelState
import net.voxelpi.vire.engine.kernel.MutableKernelStateImpl
import net.voxelpi.vire.engine.kernel.variable.storage.InputStateMap

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
    public val kernelInstance: KernelInstance

    /**
     * The event scope of the environment.
     */
    public val eventScope: EventScope

    public val state: KernelState

    /**
     * Simulates the given number over [steps].
     */
    public fun simulateSteps(steps: Int)

    public fun stepBack(steps: Int): KernelState

    public fun stepForward(steps: Int): KernelState

    public fun modifyInputs(lambda: SimulationInputConfiguration.() -> Unit)

    public fun modifyInputs(data: InputStateMap)

    /**
     * The number of steps that have been simulated.
     */
    public val simulatedSteps: Int

    /**
     * Returns the state of the simulation in the given [step].
     * @param step the step of which the state should be returned. Must be 1<=n<=[simulatedSteps].
     */
    public fun state(step: Int): KernelState
}

internal class SimulationImpl(
    override val environment: EnvironmentImpl,
    override val kernelInstance: KernelInstanceImpl,
) : Simulation {

    override val eventScope: EventScope = environment.eventScope.createSubScope()

    override var state: MutableKernelState = kernelInstance.initialKernelState()
        private set

    val states: MutableList<KernelState> = mutableListOf(state.copy())

    var currentStep: Int = 0
        private set

    override fun simulateSteps(steps: Int) {
        // Clear all stored steps after the current step.
        for (step in 0..<(simulatedSteps - currentStep)) {
            states.removeLast()
        }

        // Store state of new steps.
        for (step in 1..steps) {
            kernelInstance.kernel.updateKernel(state)
            states.add(state.copy())
            currentStep++
        }
    }

    override fun stepBack(steps: Int): KernelState {
        if (steps == 0) {
            return states[currentStep].copy()
        }
        require(steps > 0) { "Can only move a positive amount of steps back (is $steps)" }
        require(currentStep - steps > 0) { "Not enough history available." }

        currentStep -= steps
        state = states[currentStep].mutableCopy()
        return states[currentStep]
    }

    override fun stepForward(steps: Int): KernelState {
        if (steps == 0) {
            return states[currentStep].copy()
        }
        require(steps > 0) { "steps must be > 0 (is $steps)" }
        require(currentStep + steps <= simulatedSteps) { "Not enough history available." }

        currentStep += steps
        state = states[currentStep].mutableCopy()
        return states[currentStep]
    }

    override fun modifyInputs(lambda: SimulationInputConfiguration.() -> Unit) {
        val configuration = SimulationInputConfigurationImpl(state, (state as MutableKernelStateImpl).inputStateStorage)
        configuration.apply(lambda)
    }

    override fun modifyInputs(data: InputStateMap) {
        (state as MutableKernelStateImpl).inputStateStorage.update(data)
    }

    override val simulatedSteps: Int
        get() = states.size - 1

    override fun state(step: Int): KernelState {
        return states[step]
    }
}
