package net.voxelpi.vire.engine.kernel.registered

import net.voxelpi.vire.engine.Identifier
import net.voxelpi.vire.engine.kernel.builder.KernelBuilder
import net.voxelpi.vire.engine.kernel.builder.KernelBuilderImpl

internal interface RegisteredKernelBuilder : KernelBuilder {

    val id: Identifier
}

internal class RegisteredKernelBuilderImpl(
    override val id: Identifier,
) : KernelBuilderImpl(), RegisteredKernelBuilder {

    override fun build(): RegisteredKernelImpl {
        finished = true
        return RegisteredKernelImpl(
            id,
            tags.toSet(),
            properties.toMap(),
            variables.toMap(),
            configurationAction,
            initializationAction,
            updateAction,
        )
    }
}
