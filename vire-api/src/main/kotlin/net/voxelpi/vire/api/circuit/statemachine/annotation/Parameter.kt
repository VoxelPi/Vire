package net.voxelpi.vire.api.circuit.statemachine.annotation

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class Parameter(
    val id: String,
)
