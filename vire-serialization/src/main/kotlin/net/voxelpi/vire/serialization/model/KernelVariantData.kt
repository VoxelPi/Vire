package net.voxelpi.vire.serialization.model

import com.squareup.moshi.JsonClass
import net.voxelpi.vire.engine.Identifier

@JsonClass(generateAdapter = true)
internal data class KernelVariantData(
    val id: Identifier,
    val parameters: Map<String, Any?>,
)
