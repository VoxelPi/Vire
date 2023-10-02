package net.voxelpi.vire.api

import java.util.regex.Pattern

/**
 * A resource identifier which consists of a namespace and a value.
 * The namespaces may only contain lowercase alphanumeric characters, periods, underscores, and hyphens.
 * The value may only contain lowercase alphanumeric characters, periods, underscores, hyphens, and forward slashes.
 *
 * @property namespace the namespace.
 * @property value the value.
 */
data class Identifier(val namespace: String, val value: String) {

    init {
        require(NAMESPACE_PATTERN.matcher(namespace).matches()) { "$namespace is not a valid namespace name" }
        require(VALUE_PATTERN.matcher(value).matches()) { "$value is not a valid key name" }
    }

    override fun toString(): String {
        return "$namespace:$value"
    }

    companion object {
        val NAMESPACE_PATTERN = Pattern.compile("[a-z0-9._-]+")
        val VALUE_PATTERN = Pattern.compile("[a-z0-9/._-]+")

        fun parse(key: String): Identifier {
            val index = key.indexOf(':')
            require(index >= 0) { "$key is not a valid Key" }
            return Identifier(key.substring(0, index), key.substring(index + 1))
        }

        fun create(namespace: String, value: String): Identifier {
            return Identifier(namespace, value)
        }
    }
}
