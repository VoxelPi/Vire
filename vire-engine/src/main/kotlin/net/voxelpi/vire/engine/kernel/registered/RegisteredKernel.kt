package net.voxelpi.vire.engine.kernel.registered

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.WrappedKernel

/**
 * A kernel that is registered under a given id.
 */
public interface RegisteredKernel : WrappedKernel {

    /**
     * The id of under which the kernel is registered.
     */
    public val id: Identifier
}

internal open class RegisteredKernelImpl(
    override val id: Identifier,
    override val kernel: Kernel,
) : RegisteredKernel
