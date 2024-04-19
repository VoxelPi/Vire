package net.voxelpi.vire.engine.circuit.component.port

import net.voxelpi.vire.engine.circuit.network.NetworkNode

public interface MutableComponentPort : ComponentPort

internal class MutableComponentPortImpl : MutableComponentPort {

    override val node: NetworkNode
        get() = TODO("Not yet implemented")
}
