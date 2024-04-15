package net.voxelpi.vire.api.circuit.statemachine.annotation

@Target(AnnotationTarget.CLASS)
annotation class StateMachineMeta(
    val namespace: String,
    val id: String,
)
