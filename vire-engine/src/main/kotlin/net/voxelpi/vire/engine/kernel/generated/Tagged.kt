package net.voxelpi.vire.engine.kernel.generated

/**
 * Declares tags that should be added to the kernel.
 */
@Target(AnnotationTarget.CLASS)
@Repeatable
public annotation class Tagged(vararg val tags: String)
