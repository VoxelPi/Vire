package net.voxelpi.vire.engine.kernel.generated.constraint

/**
 * Adds a selection constraint to the annotated variable
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class StringSelection(
    vararg val values: String,
)
