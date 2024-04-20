package net.voxelpi.vire.engine.circuit.network

/**
 * The holder of a network node.
 */
public interface NetworkNodeHolder {

    /**
     * The network node of the holder.
     */
    public val node: NetworkNode

    /**
     * The network of the node.
     */
    public val network: Network
        get() = node.network
}

internal interface NetworkNodeHolderImpl : NetworkNodeHolder {

    override val node: NetworkNodeImpl
}
