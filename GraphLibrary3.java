import java.util.*;

/*
 * @author Sanjana Goli and Rohith Mandavilli
 */
//Our constructed test case
public class GraphLibrary3{
	public static void main(String[] args) {
		GraphGame gg = new GraphGame();
		Graph<String, Set<String>> graph = new AdjacencyMapGraph<String, Set<String>>();
		graph.insertVertex("A");
		graph.insertVertex("B");
		graph.insertVertex("C");
		graph.insertVertex("D");
		graph.insertVertex("E");
		graph.insertVertex("F");
		graph.insertVertex("G");
		graph.insertVertex("H");
		graph.insertVertex("I");
		graph.insertVertex("J");
		graph.insertVertex("K");
		graph.insertVertex("L");
		graph.insertVertex("Y");
		graph.insertVertex("Z");
		graph.insertVertex("N");
		graph.insertVertex("M");
		graph.insertUndirected("F", "B", null);
		graph.insertUndirected("B", "G", null);
		graph.insertUndirected("N", "B", null);
		graph.insertUndirected("D", "B", null);
		graph.insertUndirected("A", "B", null);
		graph.insertUndirected("D", "M", null);
		graph.insertUndirected("A", "C", null);
		graph.insertUndirected("C", "H", null);
		graph.insertUndirected("A", "D", null);
		graph.insertUndirected("A", "I", null);
		graph.insertUndirected("L", "Z", null);
		graph.insertUndirected("L", "D", null);
		graph.insertUndirected("Z", "Y", null);
		graph.insertUndirected("E", "K", null);
		graph.insertUndirected("E", "J", null);
		gg.gameMethod("H", graph);

	}
}