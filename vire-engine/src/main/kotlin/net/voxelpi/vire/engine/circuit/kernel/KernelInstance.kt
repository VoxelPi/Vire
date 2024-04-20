package net.voxelpi.vire.engine.circuit.kernel

public interface KernelInstance {
    public val kernel: Kernel
}

internal interface KernelInstanceImpl : KernelInstance {

    override val kernel: KernelImpl
}
