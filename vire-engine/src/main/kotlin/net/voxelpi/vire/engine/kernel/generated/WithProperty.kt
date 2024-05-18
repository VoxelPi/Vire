package net.voxelpi.vire.engine.kernel.generated

/**
 * Declares a property that should be added to the kernel.
 */
@Target(AnnotationTarget.CLASS)
@Repeatable
public annotation class WithProperty(val key: String, val value: String)
