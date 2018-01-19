package eu.fbk.hlt.nlp.gcluster;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/**
 * 
 * This class represents a graph. It uses an adjacency list to store both its
 * vertices and edges. The disconnected graphs (clusters) in the adjacency list
 * are printed by the breadth first traversal algorithm.
 * 
 * @author rzanoli
 *
 */
public class Graph {

	private int v; // no of vertices in a graph
	private Vector<int[]>[] adj; // array of linked list for adjacency list representation
	// private Vector<Integer>[] adj;

	/**
	 * The constructor
	 * 
	 * @param v
	 *            the number of vertices in the graph
	 */
	@SuppressWarnings("unchecked")
	public Graph(int v) {

		this.v = v;
		adj = new Vector[v];
		for (int i = 0; i < v; i++) {
			adj[i] = new Vector<int[]>();
			// adj[i] = new Vector<Integer>();
		}

	}

	/**
	 * Add an edge into a graph form vertex source to vertex target.
	 *
	 * @param i
	 *            source vertex
	 * @param j
	 *            target vertex
	 * @param edge
	 *            the edge label
	 */
	public void add(int i, int j, int edge) {

		int[] vertexAndEdge = new int[2];
		vertexAndEdge[0] = j;
		vertexAndEdge[1] = edge;
		// adj[i].add(j);
		adj[i].add(vertexAndEdge);

	}

	/**
	 * Print BFS traversal from a given source vertex s to print the disconnected
	 * graph (clusters) with the given vertex source. The produced disconnected
	 * graph is in a format like:
	 * 
	 * 1 1 2 3 1 2 0 2 3 1
	 * 
	 * where rows containing numbers in a number <= 2 are vertices while rows
	 * containing 3 numbers are edges between vertices.
	 * 
	 * As regards vertices, '1' after a vertex index (e.g., 1 1) means that it is
	 * the root of the graph. vertices that are not followed by '1' are other
	 * vertices of the graph.
	 * 
	 * Regarding edges the first two numbers (e.g., 1 2) are the vertices index
	 * connected by the edge and the additional number is the edge label.
	 * 
	 * @param s
	 *            the source vertex
	 * @param visited
	 *            the vertex that have already been visited
	 * 
	 * @return
	 */
	public String BFSUtil(int s, boolean visited[]) {

		// the vertices of the disconnected graph
		StringBuffer vertices = new StringBuffer();
		// and its edges
		StringBuffer edges = new StringBuffer();
		// if the current node is the root of the disconnected graph
		boolean root = true;

		// create a queue for BFS
		LinkedList<Integer> queue = new LinkedList<Integer>();
		// mark the current vertex as visited and enqueue it
		queue.add(s);
		visited[s] = true;
		while (queue.size() != 0) {
			// dequeue a vertex from queue and print it
			s = queue.pop();
			if (root == true) {
				vertices.append(s + " " + 1);
				root = false;
			} else
				vertices.append(s);
			vertices.append("\n");

			// create an iterator, to get all the adjacent vertices of dequeued
			// vertex s
			// if the adjacent vertex is not visited then mark it visited and
			// enqueue it
			// Iterator<Integer> i = adj[s].iterator();
			Iterator<int[]> i = adj[s].iterator();
			while (i.hasNext()) {
				// int n = i.next();
				int[] elementAndCriteria = i.next();
				// int criteria = 0;
				int n = elementAndCriteria[0];
				int criteria = elementAndCriteria[1];
				if (!visited[n]) {
					visited[n] = true;
					queue.add(n);
					edges.append(s);
					edges.append(" ");
					edges.append(n);
					edges.append(" ");
					edges.append(criteria);
					edges.append("\n");
				}
			} // end of inner while loop
				// System.out.println();
		} // end of outer while loop

		// return the vertices and the edges among them
		return vertices.toString() + edges.toString();

	}

	/**
	 * Print BFS traversal of the graph to print all the disconnected graphs
	 * separated each one by a space line.
	 * 
	 * @param s
	 *            the vertex to start printing the graphs.
	 * 
	 * @return the graph containing all the disconnected graph.
	 */
	public String BFS(int s) {

		StringBuffer graphsString = new StringBuffer();

		// mark all the vertices as not visited
		boolean visited[] = new boolean[this.v];

		for (int i = 0; i < this.v; i++) {
			if (visited[i] == false) {
				String graph_i = BFSUtil(i, visited);
				// System.out.println(graph_i);
				graphsString.append(graph_i);
				graphsString.append("\n");
			}
		}

		return graphsString.toString();

	}

	/**
	 * Print the adjacency list
	 * 
	 */
	public void printAdjacencyList() {

		for (int i = 0; i < adj.length; i++) {
			System.out.print(i + "\t");
			// Vector<Integer> list = adj[i];
			Vector<int[]> list = adj[i];
			for (int j = 0; j < list.size(); j++)
				// System.out.print(list.get(j) + " ");
				System.out.print(list.get(j)[0] + " ");
			System.out.println();
		}

	}

	public static void main(String[] args) {

		Graph g = new Graph(5);
		g.add(0, 1, 0);
		g.add(0, 2, 0);
		g.add(1, 0, 0);
		g.add(1, 2, 0);
		g.add(3, 3, 0);
		g.add(2, 4, 0);

		System.out.println("Following is Breadth First Traversal " + "(starting from vertex 0)");

		g.printAdjacencyList();
		System.out.println("========================");
		String graphsString = g.BFS(0);
		System.out.println(graphsString);
	}
}
