package net.voxelpi.vire.serialization.model

import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
public data class ComponentPortData(
    val uniqueId: UUID,
    val variable: String?,
)
