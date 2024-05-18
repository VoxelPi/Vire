package net.voxelpi.vire.engine.kernel.generated.constraint

/**
 * Adds a range constraint to the annotated variable.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class ByteInterval(
    val min: Byte,
    val max: Byte,
)

/**
 * Adds a minimum constraint to the annotated variable.
 * @property min the minimum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class ByteMin(
    val min: Byte,
)

/**
 * Adds a maximum constraint to the annotated variable.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
public annotation class ByteMax(
    val max: Byte,
)
