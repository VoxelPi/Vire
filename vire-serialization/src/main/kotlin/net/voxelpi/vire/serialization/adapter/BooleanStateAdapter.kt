package net.voxelpi.vire.serialization.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.voxelpi.vire.engine.BooleanState
import java.lang.reflect.Type

internal object BooleanStateAdapter : JsonSerializer<BooleanState>, JsonDeserializer<BooleanState> {

    override fun serialize(value: BooleanState, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(value.channels)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): BooleanState {
        return BooleanState(context.deserialize(json, BooleanArray::class.java))
    }
}
