package net.voxelpi.vire.api.simulation.statemachine.annotation

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class Output(
    val id: String,
)
