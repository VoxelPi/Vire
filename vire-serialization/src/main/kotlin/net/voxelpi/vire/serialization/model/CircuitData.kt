package net.voxelpi.vire.serialization.model

import com.squareup.moshi.JsonClass
import net.voxelpi.vire.engine.Identifier

@JsonClass(generateAdapter = true)
internal data class CircuitData(
    val id: Identifier,
    val tags: Set<Identifier>,
    val properties: Map<Identifier, String>,
    val components: List<ComponentData>,
    val networks: List<NetworkData>,
    val terminals: List<TerminalData>,
    val inputs: List<IOVariableData>,
    val outputs: List<IOVariableData>,
)
