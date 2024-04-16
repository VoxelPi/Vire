package net.voxelpi.vire.api.environment.event

import net.voxelpi.vire.api.environment.Environment

/**
 * An event that is bound to an environment.
 */
interface EnvironmentEvent {

    /**
     * The affected environment.
     */
    val environment: Environment
}
