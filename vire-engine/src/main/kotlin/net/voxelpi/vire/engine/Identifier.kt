package net.voxelpi.vire.engine

import java.util.regex.Pattern

/**
 * A resource identifier which consists of a namespace and a value.
 * The namespace may only contain lowercase alphanumeric characters, periods, underscores, and hyphens.
 * The value may only contain lowercase alphanumeric characters, periods, underscores, hyphens, and forward slashes.
 *
 * @property namespace the namespace.
 * @property value the value.
 */
public data class Identifier(val namespace: String, val value: String) {

    init {
        require(NAMESPACE_PATTERN.matcher(namespace).matches()) { "$namespace is not a valid identifier namespace" }
        require(VALUE_PATTERN.matcher(value).matches()) { "$value is not a valid identifier value" }
    }

    override fun toString(): String {
        return "$namespace:$value"
    }

    public companion object {
        private val NAMESPACE_PATTERN: Pattern = Pattern.compile("[a-z0-9._-]+")
        private val VALUE_PATTERN: Pattern = Pattern.compile("[a-z0-9/._-]+")

        /**
         * Parse an identifier from the given [identifier] string.
         */
        public fun parse(identifier: String): Identifier {
            val index = identifier.indexOf(':')
            require(index >= 0) { "$identifier is not a valid Identifier" }
            return Identifier(identifier.substring(0, index), identifier.substring(index + 1))
        }
    }
}
