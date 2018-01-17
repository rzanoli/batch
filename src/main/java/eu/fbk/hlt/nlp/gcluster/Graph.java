package eu.fbk.hlt.nlp.gcluster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

//This program will print the breadth first traversal of a disconnected graph 
//from a given source vertex
public class Graph {

	private int v; // no of vertices in a graph
	private Vector<int[]>[] adj; // array of linked list for adjacency list representation
	//private Vector<Integer>[] adj;
	
	public Graph(int v) {

		this.v = v;
		adj = new Vector[v];
		for (int i = 0; i < v; i++) {
			adj[i] = new Vector<int[]>();
			//adj[i] = new Vector<Integer>();
		}

	}

	// method to add an edge into a graph
	public void add(int i, int j, int criteria) {
		int[] elementAndCriteria = new int[2];
		elementAndCriteria[0] = j;
		elementAndCriteria[1] = criteria;
		//adj[i].add(j);
		adj[i].add(elementAndCriteria);
	}

	// print BFS traversal from a given source vertex s
	public String BFSUtil(int s, boolean visited[]) {
		
		StringBuffer nodes = new StringBuffer();
		StringBuffer edges = new StringBuffer();
		
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
				nodes.append(s + " " + 1);
				root = false;
			}
			else
				nodes.append(s);
			nodes.append("\n");

			// create an iterator, to get all the adjacent vertices of dequeued
			// vertex s
			// if the adjacent vertex is not visited then mark it visited and
			// enqueue it
			//Iterator<Integer> i = adj[s].iterator();
			Iterator<int[]> i = adj[s].iterator();
			while (i.hasNext()) {
				//int n = i.next();
				int[] elementAndCriteria = i.next();
				//int criteria = 0;
				int n = elementAndCriteria[0];
				int criteria = elementAndCriteria[1];
				if (!visited[n]) {
					visited[n] = true;
					queue.add(n);
					//System.out.println(s + " " + n + " " + 0);
					edges.append(s);
					edges.append(" ");
					edges.append(n);
					edges.append(" ");
					edges.append(criteria);
					edges.append("\n");
				}
			} // end of inner while loop
			//System.out.println();
		} // end of outer while loop
		
		return nodes.toString() + edges.toString();
		
	}

	public String BFS(int s) {
		
		StringBuffer graphsString = new StringBuffer();
		
		// mark all the vertices as not visited
		boolean visited[] = new boolean[this.v];

		for (int i = 0; i < this.v; i++) {
			if (visited[i] == false) {
				String graph_i = BFSUtil(i, visited);
				//System.out.println(graph_i);
				graphsString.append(graph_i);
				graphsString.append("\n");
			}
		}
		
		//System.out.println(graphsString.toString());
		return graphsString.toString();

	}
	
	public void printAdjacencyList() {
		
		for (int i = 0 ; i< adj.length; i++) {
			System.out.print(i + "\t");
			//Vector<Integer> list = adj[i];
			Vector<int[]> list = adj[i];
			for (int j = 0; j < list.size(); j++)
				//System.out.print(list.get(j) + " ");
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
