package net.voxelpi.vire.engine.circuit.component

import net.voxelpi.event.on
import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.LogicState
import net.voxelpi.vire.api.circuit.event.component.ComponentCreateEvent
import net.voxelpi.vire.api.circuit.event.component.ComponentDestroyEvent
import net.voxelpi.vire.api.circuit.event.component.port.ComponentPortCreateEvent
import net.voxelpi.vire.api.circuit.event.component.port.ComponentPortDestroyEvent
import net.voxelpi.vire.api.circuit.statemachine.StateMachine
import net.voxelpi.vire.api.circuit.statemachine.input
import net.voxelpi.vire.api.circuit.statemachine.output
import net.voxelpi.vire.api.circuit.statemachine.parameter
import net.voxelpi.vire.engine.VireImplementation
import net.voxelpi.vire.engine.circuit.VireCircuit
import net.voxelpi.vire.engine.simulation.VireSimulation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VireComponentTest {

    private lateinit var simulation: VireSimulation
    private lateinit var circuit: VireCircuit

    @BeforeEach
    fun setUp() {
        simulation = VireImplementation.createSimulation(emptyList())
        circuit = simulation.circuit
    }

    @Test
    fun pullInput() {
        val inputVariable = input("input")

        val stateMachine = StateMachine.create(Identifier("vire-test", "buffer")) {
            declare(inputVariable)
        }

        val component = circuit.createComponent(stateMachine)
        val inputPort = component.createPort(inputVariable.variable())

        inputPort.network.state = LogicState.value(true)
        component.pullInputs()
        assertEquals(LogicState.value(true), component.stateMachineInstance[inputVariable])
    }

    @Test
    fun pushOutput() {
        val outputVariable = output("output")

        val stateMachine = StateMachine.create(Identifier("vire-test", "buffer")) {
            declare(outputVariable)

            update = { context ->
                context[outputVariable] = LogicState.value(true)
            }
        }

        val component = circuit.createComponent(stateMachine)
        val outputPort = component.createPort(outputVariable.variable())

        component.tick()
        component.pushOutputs()
        assertEquals(LogicState.value(true), outputPort.network.state)
    }

    @Test
    fun simpleComponentTick() {
        val inputVariable = input("input")
        val outputVariable = output("output")

        val stateMachine = StateMachine.create(Identifier("vire-test", "buffer")) {
            declare(inputVariable)
            declare(outputVariable)

            update = { context ->
                context[outputVariable] = context[inputVariable].booleanState()
            }
        }

        val component = circuit.createComponent(stateMachine)
        val inputPort = component.createPort(inputVariable.variable())
        val outputPort = component.createPort(outputVariable.variable())

        inputPort.network.state = LogicState.value(true)
        component.pullInputs()
        component.tick()
        component.pushOutputs()
        assertEquals(LogicState.value(true), outputPort.network.state)
    }

    @Test
    fun removeComponent() {
        val inputVariable = input("input")
        val outputVariable = output("output")

        val stateMachine = StateMachine.create(Identifier("vire-test", "not")) {
            declare(inputVariable)
            declare(outputVariable)

            update = { context ->
                context[outputVariable] = !context[inputVariable].booleanState()
            }
        }

        val component = circuit.createComponent(stateMachine)
        val inputPort = component.createPort(inputVariable.variable())
        val outputPort = component.createPort(outputVariable.variable())
        val node1 = inputPort.network.createNode(listOf(inputPort.node))
        val node2 = inputPort.network.createNode(listOf(node1))
        val node3 = inputPort.network.createNode(listOf(node2))
        circuit.createNetworkNodeConnection(node3, outputPort.node)

        component.remove()
        component.remove() // Try removing the already removed component.
    }

    @Test
    fun removeComponentPortTwice() {
        val inputVariable = input("input")
        val outputVariable = output("output")

        val stateMachine = StateMachine.create(Identifier("vire-test", "not")) {
            declare(inputVariable)
            declare(outputVariable)

            update = { context ->
                context[outputVariable] = !context[inputVariable].booleanState()
            }
        }

        val component = circuit.createComponent(stateMachine)
        val inputPort = component.createPort(inputVariable.variable())
        val outputPort = component.createPort(outputVariable.variable())
        circuit.createNetworkNodeConnection(inputPort.node, outputPort.node)

        inputPort.remove()
        inputPort.remove() // Try removing the already removed component port.
    }

    @Test
    fun stateMachineLifecycle() {
        var configureCounter = 0
        var tickCounter = 0

        val testParameter = parameter("test", 0, min = 0, max = 10)

        val stateMachine = StateMachine.create(Identifier("vire-test", "test")) {
            declare(testParameter)

            configure = { _ ->
                configureCounter++
            }

            update = { _ ->
                tickCounter++
            }
        }
        val component = circuit.createComponent(stateMachine)

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

        component.stateMachineInstance.configureParameters {
            this[testParameter] = 1
        }
        assertEquals(1, configureCounter) { "Setting a parameter didn't call configure()" }
        assertEquals(1, component.stateMachineInstance[testParameter]) { "Parameter value didn't change to new valid value" }
        configureCounter = 0

        component.stateMachineInstance.configureParameters {
            this[testParameter] = -3
        }
        assertEquals(0, configureCounter) { "Setting an invalid parameter called configure()" }
        assertEquals(1, component.stateMachineInstance[testParameter]) { "Parameter value did change to invalid value" }
        configureCounter = 0

        component.reset(parameters = true)
        assertEquals(1, configureCounter) { "Resetting the component didn't call configure()" }
        assertEquals(0, component.stateMachineInstance[testParameter]) { "Parameter value didn't reset" }
        configureCounter = 0
    }

    @Test
    fun lifecycleEvents() {
        var createCounter = 0
        var destroyCounter = 0

        simulation.eventScope.on<ComponentCreateEvent> {
            createCounter++
        }

        simulation.eventScope.on<ComponentDestroyEvent> {
            destroyCounter++
        }

        val component = circuit.createComponent(
            StateMachine.create(Identifier("vire-test", "unit")) {}
        )
        component.remove()

        assertEquals(1, createCounter)
        assertEquals(1, destroyCounter)
    }

    @Test
    fun componentPort() {
        var createCounter = 0
        var destroyCounter = 0

        simulation.eventScope.on<ComponentPortCreateEvent> {
            createCounter++
        }

        simulation.eventScope.on<ComponentPortDestroyEvent> {
            destroyCounter++
        }

        val component = circuit.createComponent(
            StateMachine.create(Identifier("vire-test", "unit")) {}
        )
        val port = component.createPort(null)
        port.remove()
        component.remove()

        assertEquals(1, createCounter)
        assertEquals(1, destroyCounter)
    }
}
