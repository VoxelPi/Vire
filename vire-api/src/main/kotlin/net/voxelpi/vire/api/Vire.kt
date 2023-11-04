package net.voxelpi.vire.api

interface Vire {

    /**
     * The name of the vire engine implementation.
     */
    val brand: String

    /**
     * The version of the vire engine.
     */
    val version: String

    /**
     * The exact version of the vire engine.
     */
    val longVersion: String
}
