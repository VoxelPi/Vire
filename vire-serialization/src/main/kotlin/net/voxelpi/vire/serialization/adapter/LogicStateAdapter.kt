package net.voxelpi.vire.serialization.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.voxelpi.vire.engine.LogicState
import net.voxelpi.vire.engine.LogicValue
import java.lang.reflect.Type
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

@OptIn(ExperimentalStdlibApi::class)
internal object LogicStateAdapter : JsonSerializer<LogicState>, JsonDeserializer<LogicState> {

    override fun serialize(value: LogicState, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(value.channels)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LogicState {
        return LogicState(context.deserialize(json, typeOf<Array<LogicValue>>().javaType))
    }
}
