package net.voxelpi.vire.engine.util

internal object GraphUtil {

    internal fun <T> isConnectedGraph(nodes: Collection<T>, connections: Collection<Pair<T, T>>): Boolean {
        if (nodes.isEmpty()) {
            return true
        }

        val missingNodes = nodes.toMutableSet()
        val unusedConnections = connections.toMutableSet()

        var leafNodes = setOf(missingNodes.first())
        missingNodes.removeAll(leafNodes)

        while (leafNodes.isNotEmpty()) {
            // Find new leaf nodes.
            leafNodes = missingNodes.filter { node ->
                unusedConnections.any { (node1, node2) ->
                    (node1 in leafNodes && node == node2) || (node2 in leafNodes && node == node1)
                }
            }.toSet()
            missingNodes.removeAll(leafNodes)

            // Remove connections without use.
            unusedConnections.removeAll { (node1, node2) ->
                node1 !in missingNodes && node2 !in missingNodes
            }
        }

        return missingNodes.isEmpty()
    }
}
