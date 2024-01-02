package net.voxelpi.vire.api.simulation.statemachine.annotation

@Target(AnnotationTarget.CLASS)
annotation class StateMachineMeta(
    val namespace: String,
    val id: String,
)
