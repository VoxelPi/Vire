package net.voxelpi.vire.serialization.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class IOVariableData(
    val name: String,
    val size: Int?,
)
