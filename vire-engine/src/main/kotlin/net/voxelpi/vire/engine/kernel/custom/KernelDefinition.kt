package net.voxelpi.vire.engine.kernel.custom

@Target(AnnotationTarget.CLASS)
public annotation class KernelDefinition(
    val namespace: String,
    val key: String,
)
