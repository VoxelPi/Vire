package net.voxelpi.vire.api.circuit.statemachine.annotation

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class Output(
    val id: String,
)
