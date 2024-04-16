package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.engine.circuit.MutableCircuitImpl
import net.voxelpi.vire.engine.environment.EnvironmentImpl

internal class SimulationImpl(
    override val circuit: MutableCircuitImpl,
) : Simulation {

    override val environment: EnvironmentImpl
        get() = circuit.environment
}
