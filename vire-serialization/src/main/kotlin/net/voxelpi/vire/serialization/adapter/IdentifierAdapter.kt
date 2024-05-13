package net.voxelpi.vire.serialization.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.voxelpi.vire.engine.Identifier
import java.lang.reflect.Type

internal object IdentifierAdapter : JsonSerializer<Identifier>, JsonDeserializer<Identifier> {

    override fun serialize(value: Identifier, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(value.toString())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Identifier {
        return Identifier.parse(json.asString)
    }
}
