package net.voxelpi.vire.serialization.model

import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
public data class TerminalData(
    val uniqueId: UUID,
    val variable: String?,
)
