package net.voxelpi.vire.engine.circuit.terminal

import net.voxelpi.vire.engine.circuit.CircuitElement
import net.voxelpi.vire.engine.circuit.network.NetworkNodeHolder

/**
 * Terminal allow circuits to exchange data with the outside.
 */
public interface Terminal : CircuitElement, NetworkNodeHolder
