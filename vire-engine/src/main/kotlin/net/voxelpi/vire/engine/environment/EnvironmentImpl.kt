package net.voxelpi.vire.engine.environment

import net.voxelpi.vire.engine.circuit.Circuit
import net.voxelpi.vire.engine.circuit.MutableCircuitImpl

internal class EnvironmentImpl : Environment {

    override fun createCircuit(): Circuit {
        return MutableCircuitImpl(this)
    }
}
