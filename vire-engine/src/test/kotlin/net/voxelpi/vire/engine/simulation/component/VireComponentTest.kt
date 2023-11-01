package net.voxelpi.vire.engine.simulation.component

import io.github.oshai.kotlinlogging.KotlinLogging
import net.voxelpi.vire.api.Identifier
import net.voxelpi.vire.api.simulation.component.StateMachine
import net.voxelpi.vire.api.simulation.component.StateMachineContext
import net.voxelpi.vire.api.simulation.component.StateMachineInput
import net.voxelpi.vire.api.simulation.component.StateMachineOutput
import net.voxelpi.vire.api.simulation.network.NetworkState
import net.voxelpi.vire.engine.simulation.VireSimulation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VireComponentTest {

    private lateinit var simulation: VireSimulation

    private val logger = KotlinLogging.logger {}

    @BeforeEach
    fun setUp() {
        simulation = VireSimulation(emptyList())
        logger.info { "Setup test simulation" }
    }

    @Test
    fun pullInput() {
        val inputVariable = StateMachineInput("input")

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
        val outputVariable = StateMachineOutput("output")

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
        val inputVariable = StateMachineInput("input")
        val outputVariable = StateMachineOutput("output")

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
        val inputVariable = StateMachineInput("input")
        val outputVariable = StateMachineOutput("output")

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
        val inputVariable = StateMachineInput("input")
        val outputVariable = StateMachineOutput("output")

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
}
