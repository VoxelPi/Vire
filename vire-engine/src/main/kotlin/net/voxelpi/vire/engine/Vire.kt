package net.voxelpi.vire.engine

import net.voxelpi.vire.engine.environment.Environment
import net.voxelpi.vire.engine.environment.EnvironmentImpl
import net.voxelpi.vire.engine.environment.library.Library

/**
 * The vire engine.
 */
public object Vire {

    /**
     * The version of the vire engine.
     */
    public val version: String
        get() = VireBuildParameters.VERSION

    /**
     * The exact version of the vire engine.
     */
    public val exactVersion: String
        get() = "${VireBuildParameters.VERSION}-${VireBuildParameters.GIT_COMMIT}"

    /**
     * Creates a new environment.
     */
    public fun createEnvironment(libraries: List<Library>): Environment {
        return createEnvironmentImpl(libraries)
    }

    internal fun createEnvironmentImpl(libraries: List<Library>): EnvironmentImpl {
        return EnvironmentImpl(libraries)
    }
}
