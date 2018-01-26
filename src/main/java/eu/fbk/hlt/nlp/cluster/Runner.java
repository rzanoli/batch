package eu.fbk.hlt.nlp.cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import eu.fbk.hlt.nlp.criteria.Abbreviation;
import eu.fbk.hlt.nlp.criteria.Acronym;
import eu.fbk.hlt.nlp.criteria.Entailment;

/**
 * This class represents the entry point to the application to cluster
 * keyphrases. It runs a certain number of processes (Comparator) to compare the
 * keyphrases in input each other and builds the disconnected graphs (clusters).
 * After that it saves the produced clusters into the disk.
 * 
 * @author rzanoli
 *
 */
public class Runner {

	// the logger
	private static final Logger LOGGER = Logger.getLogger(Runner.class.getName());

	// this variable is used to terminate the threads
	private static AtomicBoolean interrupted;
	// the list of running threads that compare the keyphrases in input
	private List<Thread> threads;
	private static int numberOfThreads = 7;

	/**
	 * The constructor
	 *
	 * @param configFileName
	 */
	public Runner() {

		try {

			interrupted = new AtomicBoolean(false);

		} catch (Exception ex) {
			// ex.printStackTrace();
			LOGGER.severe(ex.getMessage());
		}

	}

	/**
	 * This code to stop the threads when the main method was unexpectedly
	 * terminated.
	 */
	private void attachShutDownHook() {

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {

				LOGGER.info("Shutting down...");

				try {

					interrupted.set(true);

					if (threads != null) {
						for (int i = 0; i < threads.size(); i++) {
							Thread thread = threads.get(i);
							thread.join();
							// System.out.println("Monitor Follow/Unfollow
							// actions stopped.");
							LOGGER.info("Comparator i:" + i + " stopped.");
						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
					LOGGER.severe(e.getMessage());
				}

			}
		});

		LOGGER.info("Hook attached.");

	}

	public static void main(String[] args) {

		// the directory containing the keyphrases produced by KD
		String dirIn = args[0];
		// the directory containing the produced clusters of keyphrases
		String dirOut = args[1];
		// the directory containing the graph (adjacency list) and the list of keyphrases
		String graphDirectory = args[2];
		// true if incremental clustering; false otherwise
		boolean incrementalClastering = Boolean.parseBoolean(args[3]);
		
		// init the launcher
		Runner launcher = new Runner();
		
		try {

			// attach Shut Down Hook
			launcher.attachShutDownHook();

			long startTime = System.currentTimeMillis();
			
			Graph graph = null;
			Keyphrases keyphrases;
			
			// incremental clustering
			if (incrementalClastering == true) {
				File adjacencyList = new File(graphDirectory + "/Graph.txt");
				String masterList = graphDirectory + "/Keyphrases.txt";
				LOGGER.info("Loading keyphrases...");
				keyphrases = new Keyphrases(masterList);
				LOGGER.info("Loading new keyphrases...");
				keyphrases.read(dirIn);
				LOGGER.info("Initializing graph data structure...");
				graph = new Graph(adjacencyList);
				graph = new Graph(keyphrases.size(), graph);
			}
			else {
				LOGGER.info("Loading keyphrases...");
				// load the the keyphrases produced by KD
				keyphrases = new Keyphrases();
				keyphrases.read(dirIn);
				LOGGER.info("Initializing graph data structure...");
				// init the graph structure containing the disconnected graphs (clusters)
				graph = new Graph(keyphrases.size());
			}
			
			long endTime_1 = System.currentTimeMillis();

			// add the threads to compare the keyphrases in input and build the graph
			launcher.threads = new ArrayList<Thread>(numberOfThreads);
			for (int i = 0; i < numberOfThreads; i++) {
				// start the monitor for storing tweets written by the
				// account
				Thread thread = new Thread(new Comparator(interrupted, keyphrases, graph));
				launcher.threads.add(thread);
			}
			// start the threads
			for (int i = 0; i < launcher.threads.size(); i++) {
				Thread thread = launcher.threads.get(i);
				thread.start();
				LOGGER.info("Comparator i:" + i + " started.");
			}
			// let all threads finish execution before finishing main thread
			try {
				for (int i = 0; i < launcher.threads.size(); i++) {
					Thread thread = launcher.threads.get(i);
					thread.join();
				}
			} catch (InterruptedException e) {
				LOGGER.severe(e.getMessage());
			}
			long endTime_2 = System.currentTimeMillis();

			// print the graph
			// graph.printAdjacencyList();

			LOGGER.info("Printing the clusters...");
			// get the graph
			String graphs = graph.BFS(0);
			// LOGGER.info("\n" + graphs + "============================");
			// and print the disconnected graphs (cluster) as single xml files
			printGraphs(graphs, keyphrases, dirOut);
			long endTime_3 = System.currentTimeMillis();

			// print some graph statistics
			String graphStatistic = Graph.getGraphStatistics(graphs);

			// prepare the report
			String report = "\nReport:" + new Date();
			File file = new File(dirOut);
			report = report + "\n\n" + "System Info\n";
			report = report + "===========\n";
			report = report + "#thread: " + Runner.numberOfThreads + "\n";
			report = report + "#documents: " + (new File(dirIn)).listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().toLowerCase().endsWith(".tsv");
				}
			}).length + "\n";
			report = report + "#keyphrases: " + keyphrases.totalSize() + " (unique:" + keyphrases.size() + ")\n";
			report = report + "Reading keyphrases: " + (endTime_1 - startTime) + " [ms]\n";
			report = report + "Bulding garphs: " + (endTime_2 - endTime_1) + " [ms]\n";
			report = report + "Writing graphs: " + (endTime_3 - endTime_2) + " [ms]\n";
			report = report + "Total elapsed time: " + (endTime_3 - startTime) + " [ms]\n";
			report = report + "\n" + "Graph statistics\n";
			report = report + "================\n";
			report = report + graphStatistic;
			report = report + "\n" + "Keyphrases statistics\n";
			report = report + "=====================\n";
			report = report + Keyphrases.getStatistics(keyphrases);
			System.out.println(report);
			// System.out.println(keyphrases.cursor);

			graph.printAdjacencyList(new File(graphDirectory + "/adjacencyList.txt"));
			keyphrases.save(graphDirectory + "/masterList.txt");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Print the disconnected graphs (clusters) into files
	 * 
	 * @param graphs
	 *            the graph containing the disconnected graphs (clusters)
	 * @param keyphrases
	 *            the list of keyphrases in input
	 * @param dirOut
	 *            the directory to store output xml files
	 */
	public static void printGraphs(String graphs, Keyphrases keyphrases, String dirOut) throws Exception {

		StringBuilder out = new StringBuilder();
		int nNodes = 0;
		String[] splitGraphs = graphs.split("\n");
	    int root = -1;
		for (int i = 0; i <= splitGraphs.length; i++) {
			// System.out.println("======" + splitGraphs[i]);
			if (i == splitGraphs.length || splitGraphs[i].equals("")) {

				Writer writer = new OutputStreamWriter(new FileOutputStream(dirOut + "/" + root + ".xml"),
						"UTF-8");
				// System.out.println(dirOut + "/" + i + ".xml");
				BufferedWriter fout = new BufferedWriter(writer);
				fout.write("<KEC_graph id=\"" + root + "\"" + " node_count=\"" + nNodes + "\">\n");
				nNodes = 0;
				fout.write(out.toString().substring(0, out.toString().length() - 1) + "\n");
				fout.write("</KEC_graph>\n");
				out = new StringBuilder();
				fout.close();
				continue;

			}
			String[] splitLine = splitGraphs[i].split(" ");
			if (splitLine.length == 1) {
				nNodes++;
				int kxID = Integer.parseInt(splitLine[0]);
				Keyphrase kx = keyphrases.get(kxID);
				out.append(" <node id=\"" + kxID + "\" root=\"false\">\n");
				out.append("  <text>" + kx.getText() + "</text>\n");
				out.append("  <ids>" + keyphrases.getIDs(kx) + "</ids>\n");
				out.append(" </node>\n");
			} else if (splitLine.length == 2) {
				nNodes++;
				int kxID = Integer.parseInt(splitLine[0]);
				root = kxID;
				Keyphrase kx = keyphrases.get(kxID);
				out.append(" <node id=\"" + kxID + "\" root=\"true\">\n");
				out.append("  <text>" + kx.getText() + "</text>\n");
				out.append("  <ids>" + keyphrases.getIDs(kx) + "</ids>\n");
				out.append(" </node>\n");
			} else {
				int kxSourceID = Integer.parseInt(splitLine[0]);
				int kxTargetID = Integer.parseInt(splitLine[1]);
				String relationRole = splitLine[2];
				out.append(" <edge relation_role=\"" + relationRole + "\" source=\"" + kxSourceID + "\" " + "target=\""
						+ kxTargetID + "\"/>\n");
			}
		}

	}

	/**
	 * Save the report into disk
	 * 
	 * @param report
	 *            the report
	 * @param fileName
	 *            the file where write the report
	 */
	public void saveReport(String report, File fileName) {

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			bw.write(report);

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

	}

}
