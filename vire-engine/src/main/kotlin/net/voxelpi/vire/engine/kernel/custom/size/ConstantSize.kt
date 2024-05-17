package net.voxelpi.vire.engine.kernel.custom.size

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
public annotation class ConstantSize(
    val size: Int,
)
