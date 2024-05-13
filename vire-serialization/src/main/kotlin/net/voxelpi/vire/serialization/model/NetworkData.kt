package net.voxelpi.vire.serialization.model

import com.squareup.moshi.JsonClass
import net.voxelpi.vire.engine.LogicState
import java.util.UUID

@JsonClass(generateAdapter = true)
internal data class NetworkData(
    val uniqueId: UUID,
    val initialization: LogicState,
    val nodes: List<UUID>,
    val connections: List<Array<UUID>>,
)
