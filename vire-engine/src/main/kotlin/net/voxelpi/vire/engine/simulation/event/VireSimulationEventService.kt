package net.voxelpi.vire.engine.simulation.event

import net.kyori.event.EventBus
import net.kyori.event.SimpleEventBus
import net.kyori.event.method.MethodHandleEventExecutorFactory
import net.kyori.event.method.MethodSubscriptionAdapter
import net.kyori.event.method.SimpleMethodSubscriptionAdapter
import net.voxelpi.vire.api.simulation.event.SimulationEventService
import net.voxelpi.vire.api.simulation.event.SimulationEvent
import net.voxelpi.vire.api.simulation.event.SimulationEventListener

class VireSimulationEventService : SimulationEventService {

    override val eventBus: EventBus<SimulationEvent> = SimpleEventBus(SimulationEvent::class.java)

    override val eventBusMethodAdapter: MethodSubscriptionAdapter<SimulationEventListener> = SimpleMethodSubscriptionAdapter(eventBus, MethodHandleEventExecutorFactory())
}
