package structures

import java.util.*
import kotlin.collections.ArrayDeque

interface Graph<E: Edge> {
    val edges: Array<MutableSet<E>>

    fun dfs(from: Int, to: Int): List<E> {
        val path = mutableListOf<E>()
        val visited = mutableSetOf(from)
        val next = Stack<E>()
        edges[from].forEach {
            visited.add(it.to)
            next.push(it)
        }

        while (next.isNotEmpty()) {
            next.pop().let { edge ->
                path.add(edge)
                if (edge.to == to) {
                    return finalPath(path)
                } else {
                    edges[edge.to]
                        .filter { !visited.contains(it.to) }
                        .forEach {
                            visited.add(it.to)
                            next.push(it)
                        }
                }
            }
        }
        return emptyList()
    }

    fun bfs(from: Int, to: Int): List<E> {
        val path = mutableListOf<E>()
        val visited = mutableSetOf(from)
        val next = ArrayDeque<E>()
        edges[from].forEach {
            visited.add(it.to)
            next.add(it)
        }

        while (next.isNotEmpty()) {
            next.removeFirst().let { edge ->
                path.add(edge)
                if (edge.to == to) {
                    return finalPath(path)
                } else {
                    edges[edge.to]
                        .filter { !visited.contains(it.to) }
                        .forEach {
                            visited.add(it.to)
                            next.add(it)
                        }
                }
            }
        }
        return emptyList()
    }

    private fun finalPath(path: List<E>): List<E> {
        if (path.size < 2) {
            return path
        }

        val finalPath = mutableListOf(path[path.size - 1])
        for (i in path.size - 2 downTo 0) {
            if (finalPath.last().from == path[i].to) {
                finalPath.add(path[i])
            }
        }
        return finalPath.reversed()
    }
}

interface Edge {
    val from: Int
    val to: Int
}

data class DirectedEdge(override val from: Int, override val to: Int) : Edge
data class WeightedEdge(override val from: Int, override val to: Int, val cap: Int) : Edge
typealias Path = Pair<List<Edge>, Int>

class UndirectedGraph(size: Int) : Graph<DirectedEdge> {
    override val edges: Array<MutableSet<DirectedEdge>> = Array(size) { mutableSetOf() }

    fun addEdge(from: Int, to: Int) {
        edges[from] += DirectedEdge(from, to)
        edges[to] += DirectedEdge(to, from)
    }
}

/**
 * Directed graph G(V, E), where a function cap(e): E -> N defined.
 * The starting node s is at index 0 and the target node t is at index |E| - 1.
 */
class WeightedDirectedGraph(size: Int) : Graph<WeightedEdge> {
    override val edges: Array<MutableSet<WeightedEdge>> = Array(size) { mutableSetOf() }

    fun addEdge(from: Int, to: Int, weight: Int) {
        edges[from] += WeightedEdge(from, to, weight)
    }

    private fun updateEdge(from: Int, to: Int, weight: Int) {
        edges[from].find { it.to == to }?.let {
            edges[from].remove(it)
        } ?: error("No edge $from -> $to exist")

        if (weight > 0) {
            addEdge(from, to, weight)
        }
    }

    /**
     * Ford-Fulkerson maximum flow algorithm.
     */
    fun maxFlowFF(): Pair<List<Path>, Int> = maxFlow { residualNetwork -> residualNetwork.dfs(0, edges.size - 1) }

    /**
     * Edmonds-Karp maximum flow algorithm.
     */
    fun maxFlowEK(): Pair<List<Path>, Int> = maxFlow { residualNetwork -> residualNetwork.bfs(0, edges.size - 1) }

    private fun maxFlow(algorithm: (WeightedDirectedGraph) -> List<WeightedEdge>): Pair<List<Path>, Int> {
        var flowValue = 0
        val paths = mutableListOf<Path>()
        val residualNetwork = WeightedDirectedGraph(edges.size)
        edges.forEachIndexed { i, edges -> edges.forEach { residualNetwork.addEdge(i, it.to, it.cap) } }
        var path = algorithm(residualNetwork)

        while (path.isNotEmpty()) {
            val pathFlowValue = path.minOf { it.cap }
            path.forEach { edge ->
                residualNetwork.updateEdge(edge.from, edge.to, edge.cap - pathFlowValue)
                if (residualNetwork.edges[edge.to].find { it.to == edge.from } == null) {
                    residualNetwork.addEdge(edge.to, edge.from, pathFlowValue)      // adding a back edge
                }
            }

            flowValue += pathFlowValue
            paths.add(path to pathFlowValue)
            path = algorithm(residualNetwork)
        }

        return paths.toList() to flowValue
    }
}