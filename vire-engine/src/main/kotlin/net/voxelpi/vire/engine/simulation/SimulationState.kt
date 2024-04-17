package net.voxelpi.vire.engine.simulation

import net.voxelpi.vire.engine.LogicState
import java.util.UUID

public interface SimulationState

internal class SimulationStateImpl {

    private val networkStates: MutableMap<UUID, LogicState> = mutableMapOf()
}
