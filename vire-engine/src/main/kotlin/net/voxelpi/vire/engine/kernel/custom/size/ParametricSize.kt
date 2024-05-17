package net.voxelpi.vire.engine.kernel.custom.size

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
public annotation class ParametricSize(
    val parameter: String,
)
