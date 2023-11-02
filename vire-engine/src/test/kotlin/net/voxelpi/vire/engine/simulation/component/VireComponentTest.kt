package net.voxelpi.vire.engine.simulation.component

import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.api.simulation.component.input
import net.voxelpi.vire.api.simulation.component.output
import net.voxelpi.vire.api.simulation.component.parameter
import net.voxelpi.vire.api.simulation.event.simulation.component.ComponentCreateEvent
import net.voxelpi.vire.api.simulation.event.simulation.component.ComponentDestroyEvent
import net.voxelpi.vire.api.simulation.event.simulation.component.port.ComponentPortCreateEvent
import net.voxelpi.vire.api.simulation.event.simulation.component.port.ComponentPortDestroyEvent
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.api.simulation.on
import net.voxelpi.vire.engine.simulation.VireSimulation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VireComponentTest {

    private lateinit var simulation: VireSimulation

    @BeforeEach
    fun setUp() {
        simulation = VireSimulation(emptyList())
    }

    @Test
    fun pullInput() {
        val inputVariable = input("input")

        val stateMachine = object : StateMachine(Identifier("vire-test", "buffer")) {
            init {
                declare(inputVariable)
            }

            override fun tick(context: StateMachineContext) {}
        }

        val component = simulation.createComponent(stateMachine)
        val inputPort = component.createPort(inputVariable.createView())

        inputPort.network.state = NetworkState.value(true)
        component.pullInputs()
        assertEquals(NetworkState.value(true), component.stateMachineContext[inputVariable])
    }

    @Test
    fun pushOutput() {
        val outputVariable = output("output")

        val stateMachine = object : StateMachine(Identifier("vire-test", "buffer")) {
            init {
                declare(outputVariable)
            }

            override fun tick(context: StateMachineContext) {
                context[outputVariable] = NetworkState.value(true)
            }
        }

        val component = simulation.createComponent(stateMachine)
        val outputPort = component.createPort(outputVariable.createView())

        component.tick()
        component.pushOutputs()
        assertEquals(NetworkState.value(true), outputPort.network.state)
    }

    @Test
    fun simpleComponentTick() {
        val inputVariable = input("input")
        val outputVariable = output("output")

        val stateMachine = object : StateMachine(Identifier("vire-test", "buffer")) {
            init {
                declare(inputVariable)
                declare(outputVariable)
            }

            override fun tick(context: StateMachineContext) {
                context[outputVariable] = context[inputVariable]
            }
        }

        val component = simulation.createComponent(stateMachine)
        val inputPort = component.createPort(inputVariable.createView())
        val outputPort = component.createPort(outputVariable.createView())

        inputPort.network.state = NetworkState.value(true)
        component.pullInputs()
        component.tick()
        component.pushOutputs()
        assertEquals(NetworkState.value(true), outputPort.network.state)
    }

    @Test
    fun removeComponent() {
        val inputVariable = input("input")
        val outputVariable = output("output")

        val stateMachine = object : StateMachine(Identifier("vire-test", "buffer")) {
            init {
                declare(inputVariable)
                declare(outputVariable)
            }

            override fun tick(context: StateMachineContext) {
                context[outputVariable] = !context[inputVariable]
            }
        }

        val component = simulation.createComponent(stateMachine)
        val inputPort = component.createPort(inputVariable.createView())
        val outputPort = component.createPort(outputVariable.createView())
        val node1 = inputPort.network.createNode(listOf(inputPort.node))
        val node2 = inputPort.network.createNode(listOf(node1))
        val node3 = inputPort.network.createNode(listOf(node2))
        simulation.createNetworkNodeConnection(node3, outputPort.node)

        component.remove()
        component.remove() // Try removing the already removed component.
    }

    @Test
    fun removeComponentPortTwice() {
        val inputVariable = input("input")
        val outputVariable = output("output")

        val stateMachine = object : StateMachine(Identifier("vire-test", "buffer")) {
            init {
                declare(inputVariable)
                declare(outputVariable)
            }

            override fun tick(context: StateMachineContext) {
                context[outputVariable] = !context[inputVariable]
            }
        }

        val component = simulation.createComponent(stateMachine)
        val inputPort = component.createPort(inputVariable.createView())
        val outputPort = component.createPort(outputVariable.createView())
        simulation.createNetworkNodeConnection(inputPort.node, outputPort.node)

        inputPort.remove()
        inputPort.remove() // Try removing the already removed component port.
    }

    @Test
    fun stateMachineLifecycle() {
        var configureCounter = 0
        var tickCounter = 0

        val testParameter = parameter("test", 0, min = 0, max = 10)

        val stateMachine = object : StateMachine(Identifier("vire-test", "test")) {

            init {
                declare(testParameter)
            }

            override fun configure(context: StateMachineContext) {
                configureCounter++
            }

            override fun tick(context: StateMachineContext) {
                tickCounter++
            }
        }
        val component = simulation.createComponent(stateMachine)

        assertEquals(1, configureCounter) { "The state machine was not initialized" }
        assertEquals(0, tickCounter) { "The state machine tick() function was called during the initialization" }
        configureCounter = 0

        assertEquals(0, tickCounter)
        for (step in 1..10) {
            simulation.simulateSteps(1)
            assertEquals(0, configureCounter) { "Stepping the simulation called tick()" }
            assertEquals(step, tickCounter) { "Stepping the simulation didn't call tick()" }
        }
        tickCounter = 0

        component.parameter(testParameter, 1)
        assertEquals(1, configureCounter) { "Setting a parameter didn't call configure()" }
        assertEquals(1, component.parameter(testParameter)) { "Parameter value didn't change to new valid value" }
        configureCounter = 0

        component.parameter(testParameter, -3)
        assertEquals(0, configureCounter) { "Setting an invalid parameter called configure()" }
        assertEquals(1, component.parameter(testParameter)) { "Parameter value did change to invalid value" }
        configureCounter = 0

        component.reset(parameters = true)
        assertEquals(1, configureCounter) { "Resetting the component didn't call configure()" }
        assertEquals(0, component.parameter(testParameter)) { "Parameter value didn't reset" }
        configureCounter = 0
    }

    @Test
    fun lifecycleEvents() {
        var createCounter = 0
        var destroyCounter = 0

        simulation.on<ComponentCreateEvent> {
            createCounter++
        }

        simulation.on<ComponentDestroyEvent> {
            destroyCounter++
        }

        val component = simulation.createComponent(
            object : StateMachine(Identifier("vire-test", "unit")) {
                override fun tick(context: StateMachineContext) {}
            }
        )
        component.remove()

        // Wait for events to finish.
        simulation.flushEvents()
        simulation.shutdown()

        assertEquals(1, createCounter)
        assertEquals(1, destroyCounter)
    }

    @Test
    fun componentPort() {
        var createCounter = 0
        var destroyCounter = 0

        simulation.on<ComponentPortCreateEvent> {
            createCounter++
        }

        simulation.on<ComponentPortDestroyEvent> {
            destroyCounter++
        }

        val component = simulation.createComponent(
            object : StateMachine(Identifier("vire-test", "unit")) {
                override fun tick(context: StateMachineContext) {}
            }
        )
        val port = component.createPort(null)
        port.remove()
        component.remove()

        // Wait for events to finish.
        simulation.shutdown()

        assertEquals(1, createCounter)
        assertEquals(1, destroyCounter)
    }
}
