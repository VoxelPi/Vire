package net.voxelpi.vire.serialization.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.voxelpi.vire.engine.LogicValue
import java.lang.reflect.Type

internal object LogicValueAdapter : JsonSerializer<LogicValue>, JsonDeserializer<LogicValue> {

    override fun serialize(value: LogicValue, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return when (value) {
            LogicValue.NONE -> JsonPrimitive("none")
            LogicValue.TRUE -> JsonPrimitive(true)
            LogicValue.FALSE -> JsonPrimitive(false)
            LogicValue.INVALID -> JsonPrimitive("invalid")
        }
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LogicValue {
        val primitive = json.asJsonPrimitive
        if (primitive.isBoolean) {
            return if (primitive.asBoolean) LogicValue.TRUE else LogicValue.FALSE
        }
        return when (primitive.asString) {
            "none" -> LogicValue.NONE
            "invalid" -> LogicValue.INVALID
            else -> throw IllegalArgumentException("Unknown logic value \"$primitive\"")
        }
    }
}
