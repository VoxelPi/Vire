package net.voxelpi.vire.engine.kernel.generated.constraint

/**
 * Adds a range constraint to the annotated variable.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class FloatInterval(
    val min: Float,
    val max: Float,
)

/**
 * Adds a minimum constraint to the annotated variable.
 * @property min the minimum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class FloatMin(
    val min: Float,
)

/**
 * Adds a maximum constraint to the annotated variable.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class FloatMax(
    val max: Float,
)
