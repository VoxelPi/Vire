package net.voxelpi.vire.engine.circuit

import net.voxelpi.vire.api.circuit.CircuitElement

abstract class VireCircuitElement : CircuitElement {

    abstract override val circuit: VireCircuit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VireCircuitElement

        return uniqueId == other.uniqueId
    }

    override fun hashCode(): Int {
        return uniqueId.hashCode()
    }
}
