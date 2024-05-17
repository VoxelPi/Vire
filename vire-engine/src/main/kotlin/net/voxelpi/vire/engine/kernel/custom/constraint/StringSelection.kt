package net.voxelpi.vire.engine.kernel.custom.constraint

/**
 * Adds a selection constraint to the annotated variable
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class StringSelection(
    vararg val values: String,
)
