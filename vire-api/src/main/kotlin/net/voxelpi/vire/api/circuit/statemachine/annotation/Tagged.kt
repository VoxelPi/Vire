package net.voxelpi.vire.api.circuit.statemachine.annotation

/**
 * Declares a tag that should be added to a state machine.
 */
@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class Tagged(vararg val tags: String)
