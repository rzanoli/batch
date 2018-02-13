package eu.fbk.hlt.nlp.cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class represents the list of keyphrases in input to cluster.
 * 
 * @author rzanoli
 *
 */
public class Keyphrases {

	// it is used to store a keyphrase with all the documents ids where it appears
	// it uses a LinkedHashMap to keep the keys in the order they were inserted
	private Map<Keyphrase, Set<String>> masterList;
	// the list of unique keyphrases
	private List<Keyphrase> innerList;

	private static Map<String, HashSet<Integer>> synonyms;

	// it is used by the Comparator class to move to the next keyphrase to compare
	public int cursor;
	// the total number of keyphrases including duplicates
	private int nKeyphrases;
	// the offset pointer to the first place in the list after the last keyphrase
	// it is used for incremental clustering so that
	// comparators can compare the new keyphrases with the
	// ones that were already analyzed in a previous run.
	private int offset = 0;

	/**
	 * The constructor
	 */
	public Keyphrases() throws Exception {

		this.masterList = new LinkedHashMap<Keyphrase, Set<String>>();
		this.innerList = new ArrayList<Keyphrase>();
		this.offset = 0;
		synonyms = new HashMap<String, HashSet<Integer>>();
		loadSynonyms();

	}

	/**
	 * The constructor that initializes the list of keyphrases with the keypharses
	 * saved in a previous run of the system and clustered by the clustering
	 * algorithm. It is used for implementing incremental clustering where the
	 * algorithm has to compare the new keyphrases to cluster with the ones that
	 * have already been compared.
	 * 
	 * First of all it loads the master list from file that contains the keypharses
	 * and the documents ids where they appear; then it moves the offset pointer to
	 * the last keyphrase in the list.
	 * 
	 */
	public Keyphrases(String keyphrasesfileName) throws Exception {

		this();
		load(keyphrasesfileName);
		this.offset = masterList.size();

	}

	/**
	 * Add a new keyphrase into the list of keyphrase in input to cluster
	 * 
	 * @param id
	 *            the document id where the keyphrase appears
	 * @param kx
	 *            the keyphrase
	 */
	public void add(String id, Keyphrase kx) {

		nKeyphrases++;

		if (!masterList.containsKey(kx)) {
			Set<String> ids = new TreeSet<String>();
			ids.add(id);
			masterList.put(kx, ids);
			// kx.setIkD(innerList.size());
			innerList.add(kx);
		} else {
			Set<String> ids = masterList.get(kx);
			ids.add(id);
			masterList.put(kx, ids);
		}

	}

	/**
	 * Get the documents ids where the given keyphrase appears
	 * 
	 * @param kx
	 *            the keyword
	 * 
	 * @return the documents ids where the keyphrase appears
	 */
	public String getIDs(Keyphrase kx) {

		StringBuilder result = new StringBuilder();
		for (String id : masterList.get(kx)) {
			result.append(id);
			result.append(" ");
		}
		return result.toString().trim();

	}

	/**
	 * Get the keyphrase at index i in the list
	 * 
	 * @param i
	 *            the keyphrase index
	 * 
	 * @return the keyword at index i
	 */
	public Keyphrase get(int i) {

		return innerList.get(i);

	}

	/**
	 * The size of the list containing the unique keyphrases
	 * 
	 * @return the size
	 */
	public int size() {

		return this.masterList.size();

	}

	/**
	 * The total number of keyphrases in input
	 * 
	 * @return the number of keyphrases
	 */
	public int totalSize() {

		return this.nKeyphrases;

	}

	/**
	 * Return the offset pointer to the first place in the list after the last
	 * keyphrase.
	 * 
	 * @return the offset
	 */
	public int getOffset(int i) {

		return this.offset;

	}

	/**
	 * Get an iterator to the list of the keyphrases
	 * 
	 * @return the iterator
	 */
	public Iterator<Keyphrase> iterator() {

		return this.masterList.keySet().iterator();

	}

	/**
	 * Increase the cursor to move to the next keyphrase
	 * 
	 * @return the index to the next keyphrase
	 */
	public synchronized int next() {

		if (cursor % 1000 == 0 && innerList.size() != 0)
			System.err.printf("\r%s %s", "keyphrases analyzed:", cursor * 100 / innerList.size() + "%");
		return cursor++;

	}

	/**
	 * Print the master list
	 * 
	 * @param fileName
	 *            the file that will contain the master list
	 * 
	 */
	public void save(String fileName) throws Exception {

		BufferedWriter bw = null;

		try {

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));

			Iterator<Keyphrase> kx_it = masterList.keySet().iterator();
			int counter = 0;
			while (kx_it.hasNext()) {
				Keyphrase kx = kx_it.next();
				bw.write(counter + "\t");
				Token[] tokens = kx.getTokens();
				for (int i = 0; i < tokens.length; i++) {
					Token token = tokens[i];
					String form = token.getForm();
					String lemma = token.getLemma();
					String PoS = token.getPoS();
					bw.write(form + "_#_" + PoS + "_#_" + lemma);
					if (i < tokens.length - 1)
						bw.write(" ");
				}
				bw.write("\t");
				Set<String> ids = masterList.get(kx);
				Iterator<String> id_it = ids.iterator();
				while (id_it.hasNext()) {
					bw.write(id_it.next());
					if (id_it.hasNext())
						bw.write(" ");
				}
				bw.write("\n");
				counter++;
			}

		} finally {

			if (bw != null)
				bw.close();

		}

	}

	/**
	 * Load the master list
	 * 
	 * @param fileName
	 *            the file that contains the master list
	 * 
	 */
	private void load(String fileName) throws Exception {

		BufferedReader br = null;

		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));

			String line;

			while ((line = br.readLine()) != null) {
				String[] splitLine = line.split("\t");
				String[] tokens = splitLine[1].split(" ");
				Keyphrase kx = new Keyphrase(tokens.length);
				for (int i = 0; i < tokens.length; i++) {
					String[] token_i = tokens[i].split("_#_");
					String form = token_i[0];
					String PoS = token_i[1];
					String lemma = token_i[2];
					Token token = new Token(form, PoS, lemma);
					kx.add(i, token);
				}
				String[] ids = splitLine[2].split(" ");
				for (int i = 0; i < ids.length; i++)
					add(ids[i], kx);
			}

		} finally {

			if (br != null)
				br.close();

		}

	}

	/**
	 * Load the master list
	 * 
	 * @param fileName
	 *            the file that contains the master list
	 * 
	 */

	private void loadSynonyms() throws Exception {

		BufferedReader br = null;

		try {

			br = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream("/italian_syn_list.txt"), "UTF-8"));

			String line;

			int lineNumber = 0;
			while ((line = br.readLine()) != null) {
				if (line.indexOf("_") != -1) {
					continue;
				}
				String[] splitLine = line.split("\t");
				for (int i = 0; i < splitLine.length; i++) {
					String word_i = splitLine[i];
					if (synonyms.containsKey(word_i)) {
						HashSet<Integer> synonyms_words = synonyms.get(word_i);
						synonyms_words.add(lineNumber);
					} else {
						HashSet<Integer> synonyms_words = new HashSet<Integer>();
						synonyms_words.add(lineNumber);
						synonyms.put(word_i, synonyms_words);
					}
				}
			}

		} finally {

			if (br != null)
				br.close();

		}

	}

	/**
	 * Say if 2 tokens are synonyms
	 * 
	 * @param token1
	 * @param token2
	 * 
	 * @return true if they are synonyms; false otherwise
	 */
	public static boolean synonyms(Token token1, Token token2) {

		if (synonyms.get(token1.getLemma()) == null || synonyms.get(token2.getLemma()) == null)
			return false;

		for (Integer item : synonyms.get(token1.getLemma())) {

			if (synonyms.get(token2.getLemma()).contains(item))

				return true;

		}

		return false;

	}

	/**
	 * Get some statistics about the keyphrases, e.g., number, length, ..
	 * 
	 * @param keyphrases
	 *            the keyphrases to get statistics from
	 * 
	 * @return the statistics
	 */
	public static String getStatistics(Keyphrases keyphrases) {

		StringBuilder result = new StringBuilder();

		result.append("#keyphrases:" + keyphrases.totalSize() + " (unique:" + keyphrases.size() + ")\n\n");

		Map<Integer, Integer> lengthDistribution = new TreeMap<Integer, Integer>(); // keywords length occurrences
		Map<Integer, Integer> documentsDistribution = new TreeMap<Integer, Integer>(); // documents keywords occurrences
		Iterator<Keyphrase> it = keyphrases.iterator();
		while (it.hasNext()) {

			Keyphrase kx = it.next();
			int kxLength = kx.length();
			if (lengthDistribution.containsKey(kxLength)) {
				int value = lengthDistribution.get(kxLength);
				value++;
				lengthDistribution.put(kxLength, value);
			} else {
				int value = 1;
				lengthDistribution.put(kxLength, value);
			}

			int nDocuments = keyphrases.getIDs(kx).split(" ").length;
			if (documentsDistribution.containsKey(nDocuments)) {
				int value = documentsDistribution.get(nDocuments);
				value++;
				documentsDistribution.put(nDocuments, value);
			} else {
				int value = 1;
				documentsDistribution.put(nDocuments, value);
			}

		}

		result.append("Length distribution (Length, #Occurrences):\n");
		Iterator<Integer> lengthDistributionIt = lengthDistribution.keySet().iterator();
		while (lengthDistributionIt.hasNext()) {
			int length = lengthDistributionIt.next();
			int frequency = lengthDistribution.get(length);
			result.append("\t" + length + "\t" + frequency + "\n");
		}

		result.append("\nDocuments distribution (#Documents, #Keyphrases):\n");
		Iterator<Integer> documentsDistributionIt = documentsDistribution.keySet().iterator();
		while (documentsDistributionIt.hasNext()) {
			int number = documentsDistributionIt.next();
			int frequency = documentsDistribution.get(number);
			result.append("\t" + number + "\t" + frequency + "\n");
		}

		return result.toString();

	}

	/**
	 * Load the keyphrases produced by KD
	 * 
	 * @param dirName
	 *            the directory containing the files produced by KD
	 * 
	 */
	public void read(String dirName) throws Exception {

		File dir = new File(dirName);
		File[] files = dir.listFiles();
		// sort the files to be consistent on the different platforms
		Arrays.sort(files, (f1, f2) -> f1.compareTo(f2));

		for (File file : files) {

			if (file.isFile()) {
				// System.out.println(file.getName());
				// if (file.getName().endsWith(".tsv")) {
				if (file.getName().endsWith(".iob")) {
					// Map<String, Keyphrase> kxs = readKDFiles(file);
					Map<String, Keyphrase> kxs = readIOBFiles(file);
					Iterator<String> it = kxs.keySet().iterator();
					while (it.hasNext()) {
						String kxID = it.next();
						Keyphrase kx = kxs.get(kxID);
						add(kxID, kx);
					}
				}
			}

		}

	}

	/**
	 * Read the file produced by KD and containing the keyphrases of the current
	 * document.
	 * 
	 * @param file
	 *            the file containing the keyphrases
	 * @return the index of the keyphrases in input with their ids
	 */
	@Deprecated
	private static Map<String, Keyphrase> readKDFiles(File file) throws Exception {

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
				String[] kxText = splitLine[1].split(" ");
				Keyphrase kx = new Keyphrase(kxText.length);
				for (int i = 0; i < kxText.length; i++)
					kx.add(i, new Token(kxText[i], null, null));
				result.put(kxID, kx);

			}
		} finally {
			if (br != null)
				br.close();
		}

		return result;

	}

	/**
	 * 
	 * 
	 * @param key1
	 * @param key2
	 * @return
	 */
	public boolean inDocument(Keyphrase key1, Keyphrase key2) {

		for (String item : masterList.get(key1)) {

			if (masterList.get(key2).contains(item))

				return true;

		}

		return false;

	}

	// }

	/**
	 * Read the file produced by TextPro and enriched with a new column containing
	 * the kephrases recognized by KD
	 * 
	 * @param file
	 *            the file containing the keyphrases
	 * @return the index of the keyphrases in input with their ids
	 */
	private static Map<String, Keyphrase> readIOBFiles(File file) throws Exception {

		Map<String, Keyphrase> result = new HashMap<String, Keyphrase>();

		BufferedReader br = null;
		String line = null;

		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

			List<Token> tokenBuffer = new ArrayList<Token>();

			int lineNumber = 0;
			while ((line = br.readLine()) != null) {

				String[] splitLine = line.split(" ");
				String kxString = "";
				if (splitLine.length == 4)
					kxString = splitLine[3];
				if (kxString.equals("") || kxString.equals("O")) { // tag: O OR End Of Line
					if (tokenBuffer.size() > 0) {
						Keyphrase keyphrase = new Keyphrase(tokenBuffer.size());
						boolean containsName = false;
						for (int i = 0; i < tokenBuffer.size(); i++) {
							keyphrase.add(i, tokenBuffer.get(i));
							if (tokenBuffer.get(i).getPoS().startsWith("S"))
								containsName = true;
						}
						if (containsName == true) {
							String kxID = file.getName() + "_" + lineNumber;
							result.put(kxID, keyphrase);
						}
						// System.out.println("====================================");
						tokenBuffer = new ArrayList<Token>();
					}
				} else if (kxString.indexOf("B-") != -1) {
					if (tokenBuffer.size() > 0) {
						Keyphrase keyphrase = new Keyphrase(tokenBuffer.size());
						boolean containsName = false;
						for (int i = 0; i < tokenBuffer.size(); i++) {
							keyphrase.add(i, tokenBuffer.get(i));
							if (tokenBuffer.get(i).getPoS().startsWith("S"))
								containsName = true;
						}
						if (containsName == true) {
							String kxID = file.getName() + "_" + lineNumber;
							result.put(kxID, keyphrase);
							// System.out.println("====================================");
							tokenBuffer = new ArrayList<Token>();
						}
					}
					String form = splitLine[0];
					String PoS = splitLine[1];
					String lemma = splitLine[2];
					Token token = new Token(form, PoS, lemma);
					tokenBuffer.add(token);
					// System.out.println("token buffer:" + tokenBuffer.size());
				} else { // tag: I
					String form = splitLine[0];
					String PoS = splitLine[1];
					String lemma = splitLine[2];
					Token token = new Token(form, PoS, lemma);
					tokenBuffer.add(token);
					// System.out.println("token buffer:" + tokenBuffer.size());
				}

				lineNumber++;

			}

		} finally {
			if (br != null)
				br.close();
		}

		return result;

	}

}
