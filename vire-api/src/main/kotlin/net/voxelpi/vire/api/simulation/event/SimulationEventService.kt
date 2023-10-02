package net.voxelpi.vire.api.simulation.event

import net.kyori.event.EventBus
import net.kyori.event.method.MethodSubscriptionAdapter

interface SimulationEventService {

    val eventBus: EventBus<SimulationEvent>

    val eventBusMethodAdapter: MethodSubscriptionAdapter<SimulationEventListener>
}
