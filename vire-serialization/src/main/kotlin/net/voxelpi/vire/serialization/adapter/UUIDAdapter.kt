package net.voxelpi.vire.serialization.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.UUID

internal object UUIDAdapter {

    @ToJson
    internal fun toJson(value: UUID): String {
        return value.toString()
    }

    @FromJson
    internal fun fromJson(value: String): UUID {
        return UUID.fromString(value)
    }
}
