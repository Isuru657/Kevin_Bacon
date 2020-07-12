import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/*
 * @author Isuru Abeysekara
 */
public class GraphGame {
	private Map<Integer, String> movieMap;
	private Map<Integer, String> actorMap;
	private Map<Integer, List<Integer>> a2M; //actorID to movieID
	private Map<Integer, List<Integer>> m2A; //movieID to actorID
	//private Graph<String, Set<String>> graph;
	private GraphTraversal<String, Set<String>> graphTrav;

	
	public GraphGame() {
		try { 
			movieMap = readID("inputs/movies.txt");
			actorMap = readID("inputs/actors.txt");
			
			a2M = readA2M("inputs/movie-actors.txt");
			m2A = readM2A("inputs/movie-actors.txt");

		} catch (Exception e) {
			System.out.println(e);
		}
		//graph = new AdjacencyMapGraph<String, Set<String>>();
		graphTrav = new GraphTraversal<String, Set<String>>();
	}
	
	
	public static HashMap<Integer, String> readID(String pathName) throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(pathName));
		String line, str;
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		while ((line = reader.readLine()) != null) {	//set the current line to the next line of the file
			String[] words = line.split("\\|");			//create an array the lines id in index 0 and the string in index 1
			if(!map.containsKey(Integer.parseInt(words[0]))) { //make sure the map doesnt already have the key
				map.put(Integer.parseInt(words[0]), words[1]);
			}
		}
		return map;
	}

	/**
	 * Constructs the map of movies that poitns to a list of each actor in that movie
	 * 
	 * @param pathName - passes in a pathName for the reader to use
	 * @return returns the completed map of each movie with a list of actors in those movies
	 * @throws Exception if the file is not found
	 */
	public static HashMap<Integer, List<Integer>> readM2A(String pathName) throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(pathName));
		String line, str;
		HashMap<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		while ((line = reader.readLine()) != null) {	//set the current line to the next line of the file
			String[] words = line.split("\\|");
			//2 run time situations - either the movie has already been added to the map, or it hasnt
			if(!map.containsKey(Integer.parseInt(words[0]))) {	//if it hasnt already been added - a new key and value will be created
				map.put(Integer.parseInt(words[0]), new ArrayList<Integer>());
				map.get(Integer.parseInt(words[0])).add(Integer.parseInt(words[1]));
			} else {	//if its already in the map, simply add the next actor being read into the List
				map.get(Integer.parseInt(words[0])).add(Integer.parseInt(words[1]));
			}
		}
		return map;
	}

	/**
	 * constructs a map wherein the keys are each actor, and the value is a list of movies they have been in
	 * implements the same functionality as readM2A(), except the actors point to movies
	 * @param pathName - file location of the file being read
	 * @return returns a map of actors that point to the movies they have been in
	 * @throws Exception
	 */
	public static HashMap<Integer, List<Integer>> readA2M(String pathName) throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(pathName));
		String line, str;
		HashMap<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		while ((line = reader.readLine()) != null) {	//set the current line to the next line of the file
			String[] words = line.split("\\|");
			//2 run time situations - either the actor has already been added to the map, or it hasnt
			if(!map.containsKey(Integer.parseInt(words[1]))) {	//if it hasnt already been added - a new key and value will be created
				map.put(Integer.parseInt(words[1]), new ArrayList<Integer>());
				map.get(Integer.parseInt(words[1])).add(Integer.parseInt(words[0]));
			} else {	//if its already in the map, simply add the next actor being read into the List
				map.get(Integer.parseInt(words[1])).add(Integer.parseInt(words[0]));
			}
		}
		return map;
	}
	
	
	/**
	 * Constructs the updated adjacency map graph
	 * @return returns a map where each vertex in the map points to a map of other vertices its connected with, where
	 * the value of each key in the second map points to a set of common movies both actors are in, represented through a graph.
	 */
	public Graph<String, Set<String>> createGraph() {
		Graph<String, Set<String>> graph = new AdjacencyMapGraph<String, Set<String>>();

		//create graph with vertices as actors and edges as sets of movies
		//add actors to graph
		for(Integer i : actorMap.keySet()) {
			graph.insertVertex(actorMap.get(i));
		}
		//map of a map - each key pints to another map that points to a set of strings which are the movies
		Map<Integer, Map<Integer, Set<String>>> listOfActors = new HashMap<Integer, Map<Integer, Set<String>>>();
		//iterate through a2m; get each element in the value list (move list) and go through that movie and insert directed
		for(Integer integer : actorMap.keySet()) {//for each vertex - each integer points to the string actor
			List<Integer> actorsMovieIDs = a2M.get(integer);//list of movies the vertex actor was in

			//get each movie and check if the actors in those movies are the actors in any of the other movies in actorsMovieIDS
			for(Integer i : actorsMovieIDs) {//iterating through movie 
				//i is each movie in the list of movies that an actor is in
				for(Integer j: m2A.get(i)) //now going through list of actors in each movie
				{
					//j is the actor ID of each actor in the list of actors for each movie
					if(!listOfActors.containsKey(integer)){
						if(integer != j){	//make sure integer doesnt point to itself
							Set<String> sharedMovies = new HashSet<String>();
							//need a new map which each key in ListofActors will point to
							Map<Integer, Set<String>> tempMap = new HashMap<Integer, Set<String>>();
							sharedMovies.add(movieMap.get(i));
							tempMap.put(j, sharedMovies);
							listOfActors.put(integer, tempMap);
						}
					}
					else{	//same functionality as the if statements before, except that you just add the new movie to the set
						if(integer != j){ //make sure integer doesnt point to itself
							if(!listOfActors.get(integer).containsKey(j)){
								Set<String> sharedMovies = new HashSet<String>();
								sharedMovies.add(movieMap.get(i));
								listOfActors.get(integer).put(j, sharedMovies);
							}
							else
								listOfActors.get(integer).get(j).add(movieMap.get(i));
						}
					}
				}

			}
		}
		//update the graph for each actor-actor pair through inserting a directed relationship in the graph
		for(Integer i: listOfActors.keySet()) 
			for(Integer j: listOfActors.get(i).keySet()) 
				if(!actorMap.get(i).equals(actorMap.get(j))) 
					graph.insertDirected(actorMap.get(i), actorMap.get(j), listOfActors.get(i).get(j));
				
			
		
		return graph;
	}

	/**
	 * Handler for game functionality
	 * @param start
	 * @param graph
	 */
	public void gameMethod(String start, Graph<String, Set<String>> graph) {
		//change the center of the acting universe to a valid actor --> u 
		// find the shortest path to an actor from the current center of the universe --> p
		// find the number of actors who have a path (connected by some number of steps) to the current center --> f
		// find the average path length over all actors who are connected by some path to the current center --> avg sep
		System.out.println("Commands:\n" + 
				"c: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" + 
				"d: list actors sorted by degree, with degree between low and high\n" + 
				"i: list actors with infinite separation from the current center\n" + 
				"p <name>: find path from <name> to current center of the universe\n" + 
				"s: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" + 
				"f: find the number of actors who have a path to the current center\n" +
				"u <name>: make <name> the center of the universe\n" + 
				"a: find the average separation in the universe\n" +
				"q: quit game");
		Scanner in = new Scanner(System.in); 
		String answer = "";
		String center = start;
		System.out.println(center + " is now the center of the acting universe, connected to " + (GraphLibrary2.bfs(graph, center, graphTrav).numVertices()-1) + " actors with average separation " + GraphLibrary2.averageSeparation2(GraphLibrary2.bfs(graph, center, graphTrav), center, graphTrav)); //finish
		while(!answer.equals("q")) { //keep cycling until the user asks to quit
			System.out.print(center + " game >");
			answer = in.next();
			
			if(answer.equals("p")) {	//find the path between the name inputed and the center of the universe
				
				answer = in.nextLine();
				if(answer.length()>1) answer = answer.substring(1);
				while(!graph.hasVertex(answer)){//checks whether input is valid (is it in the universe?)
					System.out.println("Invalid actor name! Enter different actor name: ");
					answer = in.nextLine();
					//function will loop until a valid vertex is inputed
				}
				
				//bfs the graph to remove the nodes not connected to the center - key so not every vertex is in the graph
				Graph<String, Set<String>> tree = GraphLibrary2.bfs(graph, center, graphTrav);
				if(tree.hasVertex(answer)) {
					List<String> path = GraphLibrary2.getPath(tree, answer, graphTrav, center); //sets the path
					if(answer.equals(center)) System.out.println("You have entered the center of the universe! Path is " +center);
					if((path==null) || (path.size() == 0)) System.out.println("No path exists");
					for(int i = 0; i < path.size()-1; i++) //prints out the movie path
						System.out.println(path.get(i) + " was in " + tree.getLabel(path.get(i), path.get(i+1)) + " with " + path.get(i+1));
					
				} else 
					System.out.println(answer + " is not connected to center");
				
			}
			else if(answer.equals("a")) //gets the average separation of the universe
			{
				double separation = GraphLibrary2.averageSeparation2(GraphLibrary2.bfs(graph, center, graphTrav), center, graphTrav);
				System.out.println(center +" has an average separation of " + separation);
			}
			//change center of the acting universe
			else if(answer.equals("u")) {
				answer = in.nextLine();
				if(answer.length()>1) answer = answer.substring(1);
				
				while(!graph.hasVertex(answer)) {//checks whether input is valid
					System.out.println("Invalid actor name! Enter different actor name: ");
					answer = in.nextLine();
					//function will loop until a valid vertex is inputed
				}				
				center = answer;
				//recreate universe and output all necessary information
				System.out.println(center + " is now the center of the acting universe, connected to " + (GraphLibrary2.bfs(graph, center, graphTrav).numVertices()-1) + " actors with average separation " + GraphLibrary2.averageSeparation2(GraphLibrary2.bfs(graph, center, graphTrav), center, graphTrav)); //finish
			}
			//list actors with infinite separation from the current center
			else if(answer.equals("i")) {
				System.out.println(GraphLibrary2.missingVertices(graph, GraphLibrary2.bfs(graph, center, graphTrav)));
			} 
			//find the number of actors who have a path (connected by some number of steps) to the current center
			else if(answer.equals("f")){
				System.out.println("The number of actors who have a connection with " + center + " is " + (GraphLibrary2.bfs(graph, center, graphTrav).numVertices()-1));
			}

			else if(answer.equals("c")) { //lists the top/bottom (+/- sign) actors with avg separation when they are the center of the universe
				System.out.println("Enter the number of actors");
				int centerNum = in.nextInt();
				
				Graph<String, Set<String>> tree = GraphLibrary2.bfs(graph, center, graphTrav);
				while(centerNum > tree.numVertices()) {//checks whether input is valid
					System.out.println("Enter smaller number of actors");
					centerNum = in.nextInt();
					//function will loop until a valid vertex is inputed
				}
				
				//need to create a map where each key is each vertex in the graph and each key points to the average separation when that key is the center of the universe
				Map<String, Double> separations = new HashMap<String, Double>();
 				for(String s: tree.vertices()) //this is a map that has each vertex that points to its avg separation
					separations.put(s, GraphLibrary2.averageSeparation2(GraphLibrary2.bfs(graph, s, graphTrav), s, graphTrav));
				List<String> keyList = new ArrayList<String>();
				keyList.addAll(separations.keySet()); //this is now a list of all the keys
				
				
				//compare the average separations between each key and sort the list
				Comparator<String> d = (String d1, String d2) -> (int)((separations.get(d1)-separations.get(d2))*100000); //so that the difference is not negligible
				keyList.sort(d);
				
				//handles both situations - whether the user wants the top separations or the bottom separations
				if(centerNum> 0) //top centerNum of the universe with greatest average separation
				{
					System.out.print("[");
					for(int i = 0; i < centerNum; i++) {
						//separate into if else to prevent adding a comma at the last element
						if(i!= centerNum-1)System.out.print(keyList.get(i)+ ": " + separations.get(keyList.get(i)) + ", ");
						else System.out.print(keyList.get(i) + " " + separations.get(keyList.get(i)));
					}
					System.out.println("]");
				}
				if(centerNum<0) //bottom centerNum of the universe with the least separation
				{	
					System.out.print("[");
					for(int i = keyList.size()-1; i >= keyList.size()+centerNum; i--) {
						//separate into if else to prevent adding a comma at the last element
						if(i!= keyList.size()+centerNum) System.out.print(keyList.get(i)+ ": " + separations.get(keyList.get(i)) + ", ");
						else System.out.print(keyList.get(i) + " " + separations.get(keyList.get(i)));
					}
					System.out.println("]");
				}
			}
			
			else if(answer.equals("d")){ //will output the actors between the degree interval inputed by the user
				
				System.out.print("Enter high degree: "); //high bound
				int degreeHigh = in.nextInt();
				System.out.print("Enter low degree: "); //low bound
				int degreeLow = in.nextInt();
				
				List<String> vertices = new ArrayList<String>();
				Graph<String, Set<String>> tree = GraphLibrary2.bfs(graph, center, graphTrav);
				for(String v: tree.vertices()) vertices.add(v);//adds each vertex to list of vertices (nodes not connected removed)
				
				//compare the average separations between each key and sort the list
				Comparator<String> c = (String v1, String v2) -> tree.inDegree(v2) - tree.inDegree(v1);
				vertices.sort(c);
				
				//boundary case - if the high bound is lower than the low bound
				if(degreeHigh<degreeLow)
					System.out.println("Invalid degree entries");
				else {
					//print out vertices from low degree to high degree
					System.out.print("High to low degree: [");
					for(int i = 0; i < vertices.size(); i++) 
						if((graph.inDegree(vertices.get(i)) >= degreeLow) && (graph.inDegree(vertices.get(i)) <= degreeHigh)) {
							//separate into if else to prevent adding a comma at the last element
							if(i != vertices.size()-1)System.out.print(vertices.get(i) + ": " + graph.inDegree(vertices.get(i)) + ", ");
							else System.out.print(vertices.get(i) + ": " + graph.inDegree(vertices.get(i)));
						}
					System.out.println("]");
				}
			}
			
			else if(answer.equals("s")) { //print the actors that have degree separations between the bound
				//map to store vertex and corresponding degree to center
				Map<String, Integer> degree = new HashMap<String, Integer>(); 
				Graph<String, Set<String>> tree = GraphLibrary2.bfs(graph, center, graphTrav);
				for(String v: tree.vertices()) //add each vertex here
					degree.put(v, GraphLibrary2.getPath(tree, v, graphTrav, center).size()-1);
				
				//comparator class created - comparing strings through the degrees of connection to the center of the universe
				class DegreeComparator implements Comparator<String>{
					public int compare(String s1, String s2) {
						if(degree.get(s1) > degree.get(s2)) return 1;
						else if(degree.get(s1) == degree.get(s2)) return 0;
						else return -1;
					}
				}
				List<String> keyList = new ArrayList<String>();
				keyList.addAll(degree.keySet());
				keyList.sort(new DegreeComparator());
				
				
				System.out.print("Enter high separation limit: ");	//high bound
				int sepHigh = in.nextInt();
				System.out.print("Enter low separation limit: ");	//low bound
				int sepLow = in.nextInt();
				
				//asks user to reenter limits if the high is lower than the low
				while(!(sepLow <= sepHigh)) {
					System.out.print("Enter valid high separation limit: ");
					sepHigh = in.nextInt();
					System.out.print("Enter valid low separation limit: ");
					sepLow = in.nextInt();
				}
				
				//assume the degree exists in the tree
				boolean contains = false;
				//go through each separation degree and check if there is an actor who has it
				for(int i = sepLow; i<= sepHigh; i++)
					if(degree.containsValue(i))
						contains = true;
				//if someone doesnt - output that, otherwise there is no need to deal with boundary case
				if(!contains) System.out.println("There is no actor within " + sepLow + " and " + sepHigh + " degrees");
				else {
					System.out.print("Low to high separation: [");
					for(int i = 0; i<keyList.size();i++) {
						int deg = degree.get(keyList.get(i));
						if((deg >= sepLow) && (deg <= sepHigh)) {
							//separate into if else to prevent adding a comma at the last element
							if(i != keyList.size()-1) System.out.print(keyList.get(i) + ": " + deg+ ", ");
							else  System.out.print(keyList.get(i) + ": " + deg);
						}
					}
					System.out.println("]");
				}
			}
			else //quit!!!!
				if(!answer.equals("q")) System.out.println("Not valid input");
		}
		System.out.println("The game has ended!!!");
	}
	public static void main(String[] args) {
		GraphGame gg = new GraphGame();
		Graph graph = gg.createGraph();
		gg.gameMethod("Kevin Bacon", graph);
		
		
	}
}

