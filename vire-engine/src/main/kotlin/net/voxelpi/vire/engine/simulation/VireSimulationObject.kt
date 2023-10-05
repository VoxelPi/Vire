package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.api.simulation.SimulationObject
import net.voxelpi.vire.engine.simulation.network.VireNetwork

abstract class VireSimulationObject : SimulationObject {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VireNetwork

        return uniqueId == other.uniqueId
    }

    override fun hashCode(): Int {
        return uniqueId.hashCode()
    }
}
