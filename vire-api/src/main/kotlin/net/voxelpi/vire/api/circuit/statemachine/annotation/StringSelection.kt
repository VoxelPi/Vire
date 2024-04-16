package net.voxelpi.vire.api.circuit.statemachine.annotation

/**
 * The allowed values of a [String] property.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class StringSelection(
    vararg val values: String,
)
