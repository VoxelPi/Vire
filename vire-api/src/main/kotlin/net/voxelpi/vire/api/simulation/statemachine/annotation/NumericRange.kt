package net.voxelpi.vire.api.simulation.statemachine.annotation

/**
 * The allowed range of a byte property.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class ByteLimits(
    val min: Byte = Byte.MIN_VALUE,
    val max: Byte = Byte.MAX_VALUE,
)

/**
 * The allowed range of an unsigned byte property.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class UByteLimits(
    val min: UByte = UByte.MIN_VALUE,
    val max: UByte = UByte.MAX_VALUE,
)

/**
 * The allowed range of a short property.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class ShortLimits(
    val min: Short = Short.MIN_VALUE,
    val max: Short = Short.MAX_VALUE,
)

/**
 * The allowed range of an unsigned short property.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class UShortLimits(
    val min: UShort = UShort.MIN_VALUE,
    val max: UShort = UShort.MAX_VALUE,
)

/**
 * The allowed range of an int property.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class IntLimits(
    val min: Int = Int.MIN_VALUE,
    val max: Int = Int.MAX_VALUE,
)

/**
 * The allowed range of an unsigned int property.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class UIntLimits(
    val min: UInt = UInt.MIN_VALUE,
    val max: UInt = UInt.MAX_VALUE,
)

/**
 * The allowed range of a long property.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class LongLimits(
    val min: Long = Long.MIN_VALUE,
    val max: Long = Long.MAX_VALUE,
)

/**
 * The allowed range of an unsigned long property.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class ULongLimits(
    val min: ULong = ULong.MIN_VALUE,
    val max: ULong = ULong.MAX_VALUE,
)

/**
 * The allowed range of a float property.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class FloatLimits(
    val min: Float = Float.NEGATIVE_INFINITY,
    val max: Float = Float.POSITIVE_INFINITY,
)

/**
 * The allowed range of a double property.
 * @property min the minimum allowed value.
 * @property max the maximum allowed value.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class DoubleLimits(
    val min: Double = Double.NEGATIVE_INFINITY,
    val max: Double = Double.POSITIVE_INFINITY,
)
