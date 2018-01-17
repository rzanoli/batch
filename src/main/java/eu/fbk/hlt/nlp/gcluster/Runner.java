package eu.fbk.hlt.nlp.gcluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class Runner {

	// the logger
	private static final Logger LOGGER = Logger.getLogger(Runner.class.getName());

	// this variable is used to terminate the threads
	private static AtomicBoolean interrupted;
	// private static Thread mRetweetThread = null;
	private List<Thread> threads;
	private static int numberOfThreads = 8;

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

				System.out.println("Shutting down...");

				try {

					interrupted.set(true);

					if (threads != null) {
						for (int i = 0; i < threads.size(); i++) {
							Thread thread = threads.get(i);
							thread.join();
							// System.out.println("Monitor Follow/Unfollow
							// actions stopped.");
							System.out.println("Combinator i:" + i + " stopped.");
						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
					LOGGER.severe(e.getMessage());
				}

				// System.out.println("done.");
				LOGGER.info("done.");

			}
		});

		LOGGER.info("Hook attached.");

	}

	public static void main(String[] args) {

		String dirIn = args[0];
		String dirOut = args[1];

		Runner runner = new Runner();
		runner.attachShutDownHook();

		Keyphrases keyphrases = runner.readKeypharses(dirIn);

		Graph graph = new Graph(keyphrases.size());

		// graph.printAdjacencyList();

		// add the threads
		runner.threads = new ArrayList<Thread>(numberOfThreads);
		for (int i = 0; i < numberOfThreads; i++) {
			// start the monitor for storing tweets written by the
			// account
			Thread thread = new Thread(new Comparator(i, interrupted, keyphrases, graph));
			runner.threads.add(thread);
		}
		// start the threads
		for (int i = 0; i < runner.threads.size(); i++) {
			Thread thread = runner.threads.get(i);
			thread.start();
		}

		try {
			// let all threads finish execution before finishing main thread
			for (int i = 0; i < runner.threads.size(); i++) {
				Thread thread = runner.threads.get(i);
				thread.join();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}

		// graph.printAdjacencyList();
		String graphs = graph.BFS(0);
		//LOGGER.info("\n" + graphs + "============================");
		

		runner.printGraphs(graphs, keyphrases, dirOut);
		
		runner.getStatistics(graphs);
		
	}
	
	private void getStatistics(String graphs) {
		
		String result = "";
		String[] splitGraphs = graphs.split("\n");
		int nNodes = 0;
		int nGraphs = 0;
		int nRoots = 0;
		Map<Integer,Integer> nodeDistribution = new TreeMap<Integer,Integer>();
		for (int i = 0; i < splitGraphs.length; i++) {
			String[] splitLine = splitGraphs[i].split(" ");
			if (splitGraphs[i].equals("")) {
				nGraphs++;
				if(nodeDistribution.containsKey(nNodes)) {
					int freq = nodeDistribution.get(nNodes);
					freq++;
					nodeDistribution.put(nNodes, freq);
				}
				else
					nodeDistribution.put(nNodes, 1);
				nNodes = 0;
			}  
			else if (splitLine.length == 2) {
				nNodes++;
				nRoots++;
			}
			else if (splitLine.length <= 1) {
				nNodes++;
			}
		}
		
		LOGGER.info("nRoots:" + nRoots + "\t" + "nGraphs:" + nGraphs);
		Iterator<Integer> it = nodeDistribution.keySet().iterator();
		StringBuffer stat = new StringBuffer();
		while(it.hasNext()) {
			int key = it.next();
			int value = nodeDistribution.get(key);
			stat.append(key + "\t" + value + "\n");
		}
		LOGGER.info("\n" + stat.toString());
		
	}

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

	private Keyphrases readKeypharses(String dirName) {

		Keyphrases keyphrases = new Keyphrases();

		File dir = new File(dirName);
		File[] files = dir.listFiles();

		//int counter = 0;
		
		for (File file : files) {

			if (file.isFile()) {
				if (file.getName().endsWith(".tsv")) {
					System.out.println(file.getName());
					Map<String, Keyphrase> kxs = readFile(file);
					Iterator<String> it = kxs.keySet().iterator();
					while (it.hasNext()) {
						String kxID = it.next();
						Keyphrase kx = kxs.get(kxID);
						keyphrases.add(kxID, kx);
					}
				}
				//counter++;
				//if (counter > 40)
					//break;
			}

		}

		LOGGER.info("Keyphrases found:" + keyphrases.nKephrases());
		LOGGER.info("Unique keyphrases found:" + keyphrases.size());

		return keyphrases;

	}

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
