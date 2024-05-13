package net.voxelpi.vire.serialization.model

import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
internal data class ComponentData(
    val uniqueId: UUID,
    val kernelVariant: KernelVariantData,
    val ports: List<ComponentPortData>,
)
