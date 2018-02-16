package eu.fbk.hlt.nlp.cluster;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/*
awk 'BEGIN {counter = 0; FS = "\t"}; {n = split($2, count, " "); for (i = 1; i <= n; i++) if (count[i] ~ /,3/) counter++;} END{printf("%s\n", counter);}' adjacencyList.txt | more
*/

/**
 * This class represents the entry point to the application of clustering
 * keyphrases (expressions which help understand and summarize the content of
 * documents) in text documents. It uses an algorithm based on graph
 * connectivity for Cluster analysis, by first representing the similarity among
 * keyphrases in a similarity graph, and afterwards finding all the connected
 * subgraphs (groups of keyphrases that are connected to one another, but that
 * have no connections to keyphrases outside the group) as clusters. The
 * algorithm does not make any prior assumptions on the number of the clusters.
 * 
 * Runner can create clusters starting from scratch or add new keyphrases to the
 * data collection without having to perform a full re-clustering.
 * 
 * @author rzanoli
 *
 */
public class Runner {

	// the logger
	private static final Logger LOGGER = Logger.getLogger(Runner.class.getName());

	// this variable is used to terminate the threads
	private static AtomicBoolean interrupted;
	// the list of running threads (Comparator) that compare the keyphrases in input
	private List<Thread> threads;
	private int numberOfThreads = 1;

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
	 * Runner entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// create Options object
		Options options = new Options();

		// add data set option
		Option dirInOption = new Option("i", "Input Path", true,
				"The absolue path of the directory containing the keyphrases to cluster.");
		dirInOption.setRequired(true);
		options.addOption(dirInOption);

		// add data set option
		Option dirOutOption = new Option("o", "Output Path", true,
				"The absolue path of the directory containing the produced clusters.");
		dirOutOption.setRequired(true);
		options.addOption(dirOutOption);

		// add data set option
		Option graphOutOption = new Option("g", "Graph Path", true,
				"The absolue path of the directory containing the adjacency list and the keyphrases index.");
		graphOutOption.setRequired(true);
		options.addOption(graphOutOption);

		// add data set option
		Option graphInOption = new Option("a", "Incremental Clustering", true,
				"The absolue path of the directory containing the adjacency list produced in a previous phase.");
		graphInOption.setRequired(false);
		options.addOption(graphInOption);

		// add data set option
		Option processesOption = new Option("p", "Processes", true, "The number of processes to perform clustering.");
		processesOption.setRequired(false);
		options.addOption(processesOption);

		// create the command line parser
		CommandLineParser parser = new BasicParser();

		// parse the command line arguments
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (Exception ex) {
			System.out.println(
					"Usage: java Runner -i dirIn -o dirOut -g graphOut [-a graphIn] [-p processes]");
			System.out.println("\nWhere:\n" + " dirIn: the directory of the keyphrases to cluster\n"
					+ " dirOut: the directory of the produced clusters in xml format\n"
					+ " graphOut: the directory of the produced adjacency list and keyphrases index\n"
					+ " graphIn: the directory of the adjacency list and the keyphrases index of a previuous clustering phase to perform incremental clustering\n"
					+ " processes: the number of processes to perform clustering (default 1)");
			System.exit(1);
		}

		// init the launcher
		Runner launcher = new Runner();

		// the directory containing the keyphrases produced by KD
		String dirIn = cmd.getOptionValue("Input Path");
		if (launcher.checkDirectoryExists(dirIn) == false) {
			System.err.println("The directory " + dirIn + " does not exist!");
			System.exit(1);
		}
		// the directory containing the produced clusters of keyphrases in xml format
		String dirOut = cmd.getOptionValue("Output Path");
		if (launcher.checkDirectoryExists(dirOut) == false) {
			System.err.println("The directory " + dirOut + " does not exist!");
			System.exit(1);
		}
		String graphDirectoryOut = cmd.getOptionValue("Graph Path");
		if (launcher.checkDirectoryExists(graphDirectoryOut) == false) {
			System.err.println("The directory " + graphDirectoryOut + " does not exist!");
			System.exit(1);
		}
		if (cmd.getOptionValue("Processes") != null)
			launcher.numberOfThreads = Integer.parseInt(cmd.getOptionValue("Processes"));

		// attach Shut Down Hook
		launcher.attachShutDownHook();

		long startTime = System.currentTimeMillis();

		Graph graph = null;
		Keyphrases keyphrases = null;
		String adjacencyListFileName = "adjacencyList.txt";
		String keyphrasesListFileName = "masterList.txt";

		String graphDirectoryIn = cmd.getOptionValue("Incremental Clustering");

		try {

			// incremental clustering
			if (graphDirectoryIn != null) {

				if (launcher.checkDirectoryExists(graphDirectoryIn) == false) {
					System.err.println("The directory " + graphDirectoryIn + " does not exist!");
					System.exit(1);
				}

				// load the adjacency list and the list of keyphrases analized in a previous
				// step
				File adjacencyList = new File(graphDirectoryIn + "/" + adjacencyListFileName);
				String masterList = graphDirectoryIn + "/" + keyphrasesListFileName;
				LOGGER.info("Loading clustered keyphrases...");
				keyphrases = new Keyphrases(masterList);
				LOGGER.info("Loading new keyphrases...");
				keyphrases.read(dirIn);

				// keyphrases.printInnerList();
				// System.exit(1);

				LOGGER.info("Initializing graph data structure...");
				graph = new Graph(adjacencyList);
				graph = new Graph(keyphrases.size(), graph);

			} else { // clustering from scratch

				LOGGER.info("Loading keyphrases...");
				// load the the keyphrases produced by KD
				keyphrases = new Keyphrases();
				keyphrases.read(dirIn);

				// keyphrases.printInnerList();
				// System.exit(1);
				// System.out.println(keyphrases.size());

				// System.out.println(keyphrases.totalSize());
				LOGGER.info("Initializing graph data structure...");
				// init the graph structure containing the disconnected graphs (clusters)
				graph = new Graph(keyphrases.size());
			}
			long endTime_1 = System.currentTimeMillis();

			// add the threads to compare the keyphrases in input and build the graph
			launcher.threads = new ArrayList<Thread>(launcher.numberOfThreads);
			for (int i = 0; i < launcher.numberOfThreads; i++) {
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
			//LOGGER.info("\n" + "============================");
			// and print the disconnected graphs (cluster) as single xml files
			printGraphs(graphs, keyphrases, dirOut);

			long endTime_3 = System.currentTimeMillis();

			// print some graph statistics
			String graphStatistic = Graph.getGraphStatistics(graphs);

			// prepare the report
			String report = "\nReport:" + new Date();
			report = report + "\n\n" + "System Info\n";
			report = report + "===========\n";
			report = report + "#thread: " + launcher.numberOfThreads + "\n";
			report = report + "#documents: " + (new File(dirIn)).listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().toLowerCase().endsWith(".iob");
				}
			}).length + "\n";
			report = report + "#keyphrases: " + keyphrases.totalSize() + " (unique:" + keyphrases.size() + ")\n";
			report = report + "Reading keyphrases: " + (endTime_1 - startTime) + " [ms]\n";
			report = report + "Bulding graphs: " + (endTime_2 - endTime_1) + " [ms]\n";
			report = report + "Writing graphs: " + (endTime_3 - endTime_2) + " [ms]\n";
			report = report + "Total elapsed time: " + (endTime_3 - startTime) + " [ms]\n";
			report = report + "\n" + "Graph statistics\n";
			report = report + "================\n";
			report = report + graphStatistic;
			report = report + "\n" + "Keyphrases statistics\n";
			report = report + "=====================\n";
			report = report + Keyphrases.getStatistics(keyphrases);

			// save the report
			launcher.saveReport(report, graphDirectoryOut + "/" + "report.txt");
			// System.out.println(report);
			// System.out.println(keyphrases.cursor);

			// saves the adjacency list and the list of keyphrases
			graph.printAdjacencyList(new File(graphDirectoryOut + "/adjacencyList.txt"));
			keyphrases.save(graphDirectoryOut + "/masterList.txt");

		} catch (Exception e) {
			e.printStackTrace();
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

	/**
	 * Print the disconnected graphs (clusters) into xml files
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

				Writer writer = new OutputStreamWriter(new FileOutputStream(dirOut + "/" + root + ".xml"), "UTF-8");
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
	public void saveReport(String report, String fileName) {

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

				System.err.println(ex.getMessage());

			}

		}

	}

	/**
	 * Check if the given directory exists
	 * 
	 * @param dirName
	 *            the directory
	 * 
	 * @return true if the directory exists; false otherwise.
	 */
	private boolean checkDirectoryExists(String dirName) {

		File file = new File(dirName);

		if (file.exists() && file.isDirectory())
			return true;

		return false;

	}

}
