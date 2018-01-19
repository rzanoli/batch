package eu.fbk.hlt.nlp.gcluster;

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
 * keyphrases in input each other and build the disconnected graphs (clusters).
 * After that it saves the produced clusters into the disk.
 * 
 * @author rzanoli
 *
 */
public class Launcher {

	// the logger
	private static final Logger LOGGER = Logger.getLogger(Launcher.class.getName());

	// this variable is used to terminate the threads
	private static AtomicBoolean interrupted;
	// the list of running threads that compare the keyphrases in input
	private List<Thread> threads;
	private static int numberOfThreads = 8;

	/**
	 * The constructor
	 *
	 * @param configFileName
	 */
	public Launcher() {

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
		// init the launcher
		Launcher launcher = new Launcher();
		// attach Shut Down Hook
		launcher.attachShutDownHook();

		long startTime = System.currentTimeMillis();
		LOGGER.info("Loading keyphrases...");
		// load the the keyphrases produced by KD
		Keyphrases keyphrases = launcher.readKeypharses(dirIn);
		long endTime_1 = System.currentTimeMillis();

		LOGGER.info("Initializing graph data structure...");
		// init the graph structure containing the disconnected graphs (clusters)
		Graph graph = new Graph(keyphrases.size());

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
		launcher.printGraphs(graphs, keyphrases, dirOut);
		long endTime_3 = System.currentTimeMillis();

		// print some statistics
		String graphStatistic = launcher.getGraphStatistics(graphs);

		String report = "\nReport:" + new Date();
		File file = new File(dirOut);
		report = report + "\n\n" + "System Info\n";
		report = report + "===========\n";
		report = report + "#thread: " + Launcher.numberOfThreads + "\n";
		report = report + "#documents: " + (new File(dirIn)).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".tsv");
			}
		}).length + "\n";
		report = report + "#keyphrases: " + keyphrases.nKephrases() + " (unique:" + keyphrases.size() + ")\n";
		report = report + "Reading keyphrases: " + (endTime_1 - startTime) + " [ms]\n";
		report = report + "Bulding garphs: " + (endTime_2 - endTime_1) + " [ms]\n";
		report = report + "Writing graphs: " + (endTime_3 - endTime_2) + " [ms]\n";
		report = report + "Total elapsed time: " + (endTime_3 - startTime) + " [ms]\n";
		report = report + "\n" + "Graph statistics\n";
		report = report + "================\n";
		report = report + graphStatistic;
		System.out.println(report);
		// System.out.println(keyphrases.cursor);

	}

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

	/**
	 * Get some graph statistics
	 * 
	 * e
	 */
	private String getGraphStatistics(String graphs) {

		StringBuffer result = new StringBuffer();

		String[] splitGraphs = graphs.split("\n");
		int nNodes = 0;
		int nTotNodes = 0;
		int nGraphs = 0;
		int nRoots = 0;
		int abbreviation = 0;
		int entailment = 0;
		int acronym = 0;
		Map<Integer, Integer> nodeDistribution = new TreeMap<Integer, Integer>();
		for (int i = 0; i < splitGraphs.length; i++) {
			System.out.println(splitGraphs[i]);
			String[] splitLine = splitGraphs[i].split(" ");
			if (splitGraphs[i].equals("")) {
				nGraphs++;
				if (nodeDistribution.containsKey(nNodes)) {
					int freq = nodeDistribution.get(nNodes);
					freq++;
					nodeDistribution.put(nNodes, freq);
				} else
					nodeDistribution.put(nNodes, 1);
				nNodes = 0;
			} else if (splitLine.length == 2) {
				nNodes++;
				nTotNodes++;
				nRoots++;
			} else if (splitLine.length <= 1) {
				nNodes++;
				nTotNodes++;
			} else {
				if (Integer.parseInt(splitLine[2]) == Abbreviation.id)
					abbreviation++;
				else if (Integer.parseInt(splitLine[2]) == Acronym.id)
					acronym++;
				else if (Integer.parseInt(splitLine[2]) == Entailment.id)
					entailment++;
			}
		}

		result.append("#Graphs (clusters) produced: " + nRoots + "\n");
		result.append("#Vertices: " + nTotNodes + "\n");
		result.append("#Edges: " + (abbreviation + acronym + entailment) + " (abbreviation:" + abbreviation + " "
				+ "acronym:" + acronym + " entailment:" + entailment + ")" + "\n");

		result.append("\n\tDistribution (#graphs with #Vertices):\n");
		Iterator<Integer> it = nodeDistribution.keySet().iterator();
		while (it.hasNext()) {
			int key = it.next();
			int value = nodeDistribution.get(key);
			result.append("\t" + value + "\t" + key + "\n");
		}

		return result.toString();

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
	private void printGraphs(String graphs, Keyphrases keyphrases, String dirOut) {

		StringBuffer out = new StringBuffer();
		int nNodes = 0;
		String[] splitGraphs = graphs.split("\n");
		int graphCounter = 0;
		for (int i = 0; i < splitGraphs.length; i++) {
			if (splitGraphs[i].equals("")) {
				try {
					Writer writer = new OutputStreamWriter(new FileOutputStream(dirOut + "/" + graphCounter + ".xml"),
							"UTF-8");
					// System.out.println(dirOut + "/" + i + ".xml");
					BufferedWriter fout = new BufferedWriter(writer);
					fout.write("<KEC_graph id=\"" + graphCounter + "\"" + " node_count=\"" + nNodes + "\">\n");
					nNodes = 0;
					fout.write(out.toString().substring(0, out.toString().length() - 1) + "\n");
					fout.write("</KEC_graph>\n");
					out = new StringBuffer();
					fout.close();
					graphCounter++;
					continue;
				} catch (IOException e) {
					LOGGER.severe(e.getMessage());
				}
			}
			String[] splitLine = splitGraphs[i].split(" ");
			if (splitLine.length == 1) {
				nNodes++;
				Keyphrase kx = keyphrases.get(Integer.parseInt(splitLine[0]));
				out.append(" <node id=\"" + kx.getId() + "\" root=\"false\">\n");
				out.append("  <text>" + kx.getText() + "</text>\n");
				out.append("  <ids>" + keyphrases.getIDs(kx) + "</ids>\n");
				out.append(" </node>\n");
			} else if (splitLine.length == 2) {
				nNodes++;
				Keyphrase kx = keyphrases.get(Integer.parseInt(splitLine[0]));
				out.append(" <node id=\"" + kx.getId() + "\" root=\"true\">\n");
				out.append("  <text>" + kx.getText() + "</text>\n");
				out.append("  <ids>" + keyphrases.getIDs(kx) + "</ids>\n");
				out.append(" </node>\n");
			} else {
				Keyphrase kxSource = keyphrases.get(Integer.parseInt(splitLine[0]));
				Keyphrase kxTarget = keyphrases.get(Integer.parseInt(splitLine[1]));
				String relationRole = splitLine[2];
				out.append(" <edge relation_role=\"" + relationRole + "\" source=\"" + kxSource.getId() + "\" "
						+ "target=\"" + kxTarget.getId() + "\"/>\n");
			}
		}

	}

	/**
	 * Read the keyphrases produced by KD
	 * 
	 * @param dirName
	 *            the directory containing the files produced by KD
	 * @return the list of keyphrases
	 */
	private Keyphrases readKeypharses(String dirName) {

		Keyphrases keyphrases = new Keyphrases();

		File dir = new File(dirName);
		File[] files = dir.listFiles();

		for (File file : files) {

			if (file.isFile()) {
				if (file.getName().endsWith(".tsv")) {
					Map<String, Keyphrase> kxs = readFile(file);
					Iterator<String> it = kxs.keySet().iterator();
					while (it.hasNext()) {
						String kxID = it.next();
						Keyphrase kx = kxs.get(kxID);
						keyphrases.add(kxID, kx);
					}
				}
			}

		}

		System.out.println();

		return keyphrases;

	}

	/**
	 * Read the file produced by KD and containing the keyphrases of the current
	 * document.
	 * 
	 * @param file
	 *            the file containing the keyphrases
	 * @return the index of the keyword in input with their ids
	 */
	private Map<String, Keyphrase> readFile(File file) {

		Map<String, Keyphrase> result = new HashMap<String, Keyphrase>();

		BufferedReader br = null;
		String line = null;

		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

			int lineNumber = 0;
			while ((line = br.readLine()) != null) {

				// System.out.println(sCurrentLine);
				lineNumber++;
				if (lineNumber == 1)
					continue;

				String[] splitLine = line.split("\t");
				String kxID = file.getName() + "_" + Integer.parseInt(splitLine[0]);
				String kxText = splitLine[1];
				Keyphrase kx = new Keyphrase(kxText);
				result.put(kxID, kx);

			}
		} catch (IOException e) {
			// e.printStackTrace();
			LOGGER.severe(e.getMessage());
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				// ex.printStackTrace();
				LOGGER.severe(ex.getMessage());
			}
		}

		return result;

	}

}
