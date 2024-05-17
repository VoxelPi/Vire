package net.voxelpi.vire.engine.kernel.custom.constraint

/**
 * Adds a range constraint to the annotated variable.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class DoubleInterval(
    val min: Double,
    val max: Double,
)

/**
 * Adds a minimum constraint to the annotated variable.
 * @property min the minimum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class DoubleMin(
    val min: Double,
)

/**
 * Adds a maximum constraint to the annotated variable.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class DoubleMax(
    val max: Double,
)
