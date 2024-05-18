package net.voxelpi.vire.engine.util

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSuperclassOf

internal fun isInstanceOfType(value: Any?, type: KType): Boolean {
    // Handle null values: only return true if 'value' is nullable
    if (value == null) return type.isMarkedNullable

    return when (val classifier = type.classifier) {
        is KClass<*> -> classifier.isSuperclassOf(value::class)
        else -> classifier == value::class
    }
}

internal fun <K, V> Map<K, V>.partition(predicate: (Map.Entry<K, V>) -> Boolean): Pair<Map<K, V>, Map<K, V>> {
    val (matching, nonMatching) = entries.partition(predicate)
    return matching.associate { it.toPair() } to nonMatching.associate { it.toPair() }
}
