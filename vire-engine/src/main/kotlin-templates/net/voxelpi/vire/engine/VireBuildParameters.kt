package net.voxelpi.vire.engine

internal object VireBuildParameters {

    /**
     * The current version.
     */
    const val VERSION: String = "{{ version }}"

    /**
     * The current git commit.
     */
    const val GIT_COMMIT: String = "{{ git_commit }}"
}
