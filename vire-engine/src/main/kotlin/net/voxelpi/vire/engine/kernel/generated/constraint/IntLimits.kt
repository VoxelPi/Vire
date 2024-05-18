package net.voxelpi.vire.engine.kernel.generated.constraint

/**
 * Adds a range constraint to the annotated variable.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class IntInterval(
    val min: Int,
    val max: Int,
)

/**
 * Adds a minimum constraint to the annotated variable.
 * @property min the minimum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class IntMin(
    val min: Int,
)

/**
 * Adds a maximum constraint to the annotated variable.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class IntMax(
    val max: Int,
)
