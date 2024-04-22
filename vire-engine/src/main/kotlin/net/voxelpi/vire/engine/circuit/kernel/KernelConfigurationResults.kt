package net.voxelpi.vire.engine.circuit.kernel

internal data class KernelConfigurationResults(
    val kernelConfiguration: KernelConfiguration,
    val ioVectorSizes: MutableMap<String, Int>,
)
