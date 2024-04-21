package net.voxelpi.vire.engine.circuit.network

/**
 * The holder of a network node.
 */
internal interface NetworkNodeHolder {

    /**
     * The network node of the holder.
     */
    val networkNode: NetworkNodeImpl

    /**
     * The network of the node.
     */
    val network: NetworkImpl
        get() = networkNode.network
}
