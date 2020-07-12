import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;


/*
 * @author Isuru Abeysekara
 */
public class GraphLibrary2 {
	/*
	 * taken from GraphTraversal Class
	 */
	public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source, GraphTraversal gT) {
        //updates backtrack in the graphtraversal class which holds a map and each key points to its parent vertex
		//create a new graph where each node points in the direction of the node that gets it to the source
        return gT.BFSTree(g, source);
	}
	
	/**
	 * @param tree the graph with all updated vertices
	 * @param v	the target vertex
	 * @param gT the class to traverse the graph
	 * @param center the center of the universe
	 * @return returns the path from the vertex to the center
	 */
	public static <V,E> List<V> getPath(Graph<V, E> tree, V v, GraphTraversal gT, V center){
		Graph<V,E> graph = bfs(tree, v, gT);
		List<V> path = gT.findPath(v, center);
		return path;
	}
	
	/**
	 * @param graph - the graph with all vertices that is not sorted
	 * @param subgraph - the graph with only the vertices that are connected to the center
	 * @return returns a set of the missing vertices between the bfsed graph and the normal graph
	 */
	public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph){
		Set<V> missingVert = new HashSet<V>();
		Set<V> subgraphVertices = new HashSet<V>();
		
		//cycles through each vertex in subgraph to add to the set of subgraph vertices
		for(V vertex : subgraph.vertices())
			subgraphVertices.add(vertex);
		
		//cycles through all vertices and only adds it to the missing vertices set if it is not also shared
		for(V vertex : graph.vertices()) 
			if(!subgraphVertices.contains(vertex)) 
				missingVert.add(vertex);
		
		return missingVert;
    }
	
	/**
	 * 
	 * @param tree - graph with all vertices
	 * @param root - center of universe
	 * @param gT - object used to traverse the graph
	 * @return returns the average degrees it takes to get to the center from each vertex
	 */
	public static <V,E> double averageSeparation2(Graph<V,E> tree, V root, GraphTraversal gT) {
		int count = tree.numVertices()-1;
		int total = 0;
		total = avgRecurse(tree, root, 1);
	
		return (double)total/count;
	}
	
	private static <V,E> int avgRecurse(Graph<V,E> tree, V root, int path) {
		int total = 0;
		for(V vertex: tree.inNeighbors(root)) {
			total += path;
			total += avgRecurse(tree, vertex, path+1);
		}
		return total;
		
	}
}
