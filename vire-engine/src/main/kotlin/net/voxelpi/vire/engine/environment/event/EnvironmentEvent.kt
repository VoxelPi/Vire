package net.voxelpi.vire.engine.environment.event

import net.voxelpi.vire.engine.environment.Environment

/**
 * An event that is bound to an environment.
 */
public interface EnvironmentEvent {

    /**
     * The affected environment.
     */
    public val environment: Environment
}
