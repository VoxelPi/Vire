package net.voxelpi.vire.api.simulation.statemachine.annotation

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class Input(
    val id: String,
)
