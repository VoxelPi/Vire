package net.voxelpi.vire.serialization.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import net.voxelpi.vire.engine.Identifier

internal object IdentifierAdapter {

    @ToJson
    internal fun toJson(value: Identifier): String {
        return value.toString()
    }

    @FromJson
    internal fun fromJson(value: String): Identifier {
        return Identifier.parse(value)
    }
}
