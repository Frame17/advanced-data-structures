import structures.*

const val FF = "Ford-Fulkerson"
const val EK = "Edmonds-Karp"

fun main() {
    val undirectedGraph = UndirectedGraph(5)
    undirectedGraph.addEdge(0, 1)
    undirectedGraph.addEdge(0, 2)
    undirectedGraph.addEdge(0, 3)
    undirectedGraph.addEdge(1, 2)
    undirectedGraph.addEdge(2, 4)
    printPath(undirectedGraph.bfs(0, 4))
    println()
    printPath(undirectedGraph.dfs(0, 4))
    println()

    var graph = WeightedDirectedGraph(5)
    graph.addEdge(0, 1, 4)
    graph.addEdge(0, 2, 5)
    graph.addEdge(0, 3, 2)
    graph.addEdge(2, 1, 6)
    graph.addEdge(3, 2, 3)
    graph.addEdge(1, 4, 7)
    graph.addEdge(2, 4, 3)

    maxFlow(FF, graph) { it.maxFlowFF() }
    maxFlow(EK, graph) { it.maxFlowEK() }

    graph = WeightedDirectedGraph(6)
    graph.addEdge(0, 1, 10)
    graph.addEdge(0, 2, 14)
    graph.addEdge(0, 3, 7)
    graph.addEdge(1, 3, 11)
    graph.addEdge(2, 3, 9)
    graph.addEdge(1, 5, 8)
    graph.addEdge(3, 5, 10)
    graph.addEdge(3, 4, 3)
    graph.addEdge(2, 4, 7)
    graph.addEdge(4, 5, 14)

    maxFlow(FF, graph) { it.maxFlowFF() }
    maxFlow(EK, graph) { it.maxFlowEK() }
}

private fun maxFlow(
    name: String,
    graph: WeightedDirectedGraph,
    algorithm: (WeightedDirectedGraph) -> Pair<List<Path>, Int>
) {
    println("Running $name algorithm")
    val (paths, flowValue) = algorithm(graph)
    paths.forEach { (path, fv) ->
        printPath(path)
        println(" : $fv")
    }
    println("Flow value: $flowValue")
}

private fun printPath(path: List<Edge>) =
    path.forEachIndexed { i, edge ->
        if (i != path.size - 1) {
            print("${edge.from} -> ")
        } else {
            print("${edge.from} -> ${edge.to}")
        }
    }