package net.voxelpi.vire.engine.simulation

import net.voxelpi.event.EventScope
import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.CircuitImpl
import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.environment.EnvironmentImpl

/**
 * A simulation of a logic circuit.
 */
public interface Simulation {

    /**
     * The environment of the simulation.
     */
    public val environment: Environment

    /**
     * The simulated circuit.
     */
    public val circuit: Circuit

    /**
     * The event scope of the environment.
     */
    public val eventScope: EventScope
}

internal class SimulationImpl(
    override val circuit: CircuitImpl,
) : Simulation {

    override val environment: EnvironmentImpl
        get() = circuit.environment

    override val eventScope: EventScope
        get() = environment.eventScope.createSubScope() // TODO: Should this be a sub-scope of the circuit event scope instead?
}
