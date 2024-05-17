package net.voxelpi.vire.engine.kernel.custom.declaration

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
public annotation class ParameterDeclaration(
    val name: String = "",
)
