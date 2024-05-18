package net.voxelpi.vire.engine.kernel.generated.constraint

/**
 * Adds a range constraint to the annotated variable.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class ULongInterval(
    val min: ULong,
    val max: ULong,
)

/**
 * Adds a minimum constraint to the annotated variable.
 * @property min the minimum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class ULongMin(
    val min: ULong,
)

/**
 * Adds a maximum constraint to the annotated variable.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class ULongMax(
    val max: ULong,
)
