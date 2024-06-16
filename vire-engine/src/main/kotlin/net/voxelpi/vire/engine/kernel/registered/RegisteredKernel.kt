package net.voxelpi.vire.engine.kernel.registered

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.Kernel
import net.voxelpi.vire.engine.kernel.KernelProvider

/**
 * A kernel that is registered under a given id.
 */
public interface RegisteredKernel : KernelProvider {

    /**
     * The id of under which the kernel is registered.
     */
    public val id: Identifier

    /**
     * The kernel which is registered.
     */
    public override val kernel: Kernel
}
