package net.voxelpi.vire.engine.circuit

import net.voxelpi.vire.engine.circuit.kernel.variable.Variable
import net.voxelpi.vire.engine.environment.EnvironmentImpl
import net.voxelpi.vire.engine.simulation.Simulation
import net.voxelpi.vire.engine.simulation.SimulationImpl

internal class MutableCircuitImpl(
    override val environment: EnvironmentImpl,
) : MutableCircuit {

    override val variables: Map<String, Variable>
        get() = TODO("Not yet implemented")

    override fun createSimulation(): Simulation {
        return SimulationImpl(this)
    }
}
