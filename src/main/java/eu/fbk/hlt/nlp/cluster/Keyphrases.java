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
import eu.fbk.hlt.nlp.babelnet.BabelnetWrapper;


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

	// MultiWordNet synonyms list
	private static Map<String, HashSet<String>> synonymsList_IT; //IT
	private static Map<String, HashSet<String>> synonymsList_EN; //EN

	// it is used by the Comparator class to move to the next keyphrase to compare
	public int cursor;
	// the total number of keyphrases including duplicates
	private int nKeyphrases;
	// the total number of keyphrases found in Babelnet
	private static int nITKeyphrasesFoundInBabelnet;
	private static int nITMonosemicKeyphrasesFoundInBabelnet;
	private static int nDEKeyphrasesFoundInBabelnet;
	private static int nDEMonosemicKeyphrasesFoundInBabelnet;
	private static int nENKeyphrasesFoundInBabelnet;
	private static int nENMonosemicKeyphrasesFoundInBabelnet;
	private static int nQueriesInBabelnet;
	// the offset pointer to the first place in the list after the last keyphrase
	// it is used for incremental clustering so that
	// comparators can compare the new keyphrases with the
	// ones that were already analyzed in a previous run.
	private int offset = 0;
	// The BabelNet class is used as the entry point to access all the content available 
	// in BabelNet. The class is implemented through the singleton pattern, where we restrict 
	// the instantiation of the BabelNet class to one object. You can obtain a reference to the 
	// only instance of the BabelNet class with the following line
	private BabelnetWrapper bn;
	
	/**
	 * The constructor
	 */
	public Keyphrases() throws Exception {

		this.masterList = new LinkedHashMap<Keyphrase, Set<String>>();
		this.innerList = new ArrayList<Keyphrase>();
		this.offset = 0;
		synonymsList_IT = loadSynonyms("/it_syn_list.txt"); // IT
		synonymsList_EN = loadSynonyms("/en_syn_list.txt"); // EN
		bn = new BabelnetWrapper();

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
	 * @param addBabelnetSynsets 
	 * 			if the keyphrase already contains the babelnet synsets this parameter is set to false;
	 * 			otherwise true; basically during the incremental clustering phase and when the tool is reading
	 * 			the clustered keyphrases that already contains the babelnet synsets the parameter is set to
	 * 			false. 
	 */
	private void add(String id, Keyphrase kx, boolean addBabelnetSynsets) {
		
		nKeyphrases++;
		
		//if (nKeyphrases % 10000 == 0)
			//System.out.println(new Date() + " " + nKeyphrases + "\t");

		if (!masterList.containsKey(kx)) {
			//System.out.println("nuova:" + nKeyphrases);
			Set<String> ids = new TreeSet<String>();
			ids.add(id);
			
			// add the babelnet synsets
			if (addBabelnetSynsets == true) {
				//System.out.println((new Date()).getTime());
				// add babelnet synsets
				List<Language> targetLanguages = new ArrayList<Language>();
				targetLanguages.add(Language.IT);
				targetLanguages.add(Language.DE);
				targetLanguages.add(Language.EN); 
				
				List<String> babelnetSynsets = this.getSynsets(kx.getText(), kx.getLanguage(), targetLanguages);
				
				nQueriesInBabelnet++;
				if (babelnetSynsets.size() > 0) {
					if (kx.getLanguage() == Language.IT) {
						nITKeyphrasesFoundInBabelnet++;
					}
					else if (kx.getLanguage() == Language.DE) {
						nDEKeyphrasesFoundInBabelnet++;
						//for (String synset : babelnetSynsets)
							//System.out.println(synset);
					}
					else if (kx.getLanguage() == Language.EN)
						nENKeyphrasesFoundInBabelnet++;
				}
				//else 
					//System.out.println("non trovato");
					
				for (String synset : babelnetSynsets) {
					kx.addbabelnetSynset(synset);

					//System.out.println(synset);
				}
					
				if (kx.getLanguage() == Language.IT && kx.isMonosemic()) {
					nITMonosemicKeyphrasesFoundInBabelnet++;
				}
				else if (kx.getLanguage() == Language.DE && kx.isMonosemic()) {
					nDEMonosemicKeyphrasesFoundInBabelnet++;
				}
				else if (kx.getLanguage() == Language.EN  && kx.isMonosemic()) {
					nENMonosemicKeyphrasesFoundInBabelnet++;
				}
				
				//System.out.println((new Date()).getTime());
			}
			
			masterList.put(kx, ids);
			// kx.setIkD(innerList.size());
			innerList.add(kx);
			//System.out.println("nuova");
		} else {
			Set<String> ids = masterList.get(kx);
			ids.add(id);
			//System.out.println("trovata:" + nKeyphrases);
			//System.out.println("vista");
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

			int counter = 0;
			Iterator<Keyphrase> kx_it = masterList.keySet().iterator();
			while (kx_it.hasNext()) {
				Keyphrase kx = kx_it.next();
				
				// counter language
				bw.write(counter + "\t" + kx.getLanguage() + "\t");
				
				// form pos lemma
				Token[] tokens = kx.getTokens();
				for (int i = 0; i < tokens.length; i++) {
					Token token = tokens[i];
					String form = token.getForm();
					String lemma = token.getLemma();
					String PoS = token.getPoS();
					String wordnetPoS = token.getWordNetPoS();
					bw.write(form + "_#_" + PoS + "_#_" + lemma + "_#_" + wordnetPoS);
					if (i < tokens.length - 1)
						bw.write(" ");
				}
				
				bw.write("\t");
				// babelnet synsets
				Iterator<String> synsetIt = kx.getbabelnetSynset().iterator();
				while(synsetIt.hasNext()) {
					String synset = synsetIt.next();
					bw.write(synset);
					if (synsetIt.hasNext())
						bw.write(" ");
				}
				
				// id
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
				
				String languageString = splitLine[1];
				// set the language of the keyphrases
				Language language = null;
				if (languageString.equals("IT"))
					language = Language.IT;
				else if (languageString.equals("DE"))
					language = Language.DE;
				else if (languageString.equals("EN"))
					language = Language.EN;
				
				// set form, pos and lemma
				String[] tokens = splitLine[2].split(" ");
				Keyphrase kx = new Keyphrase(tokens.length, language);
				for (int i = 0; i < tokens.length; i++) {
					String[] token_i = tokens[i].split("_#_");
					String form = token_i[0];
					String PoS = token_i[1];
					String lemma = token_i[2];
					String wordnetPoS = token_i[3];
					Token token = new Token(form, PoS, lemma, wordnetPoS);
					kx.add(i, token);
				}
				
				// set babelnet synsets
				String[] synsets = splitLine[3].split(" ");
				for (int i = 0; i < synsets.length; i++) {
					String synset = synsets[i];
					kx.addbabelnetSynset(synset);
				}
				
				// set id
				String[] ids = splitLine[4].split(" ");
				for (int i = 0; i < ids.length; i++)
					add(ids[i], kx, false);
			}

		} finally {

			if (br != null)
				br.close();

		}

	}

	
	/**
	 * Load synonyms extracted from MWN
	 * 
	 */

	private Map<String, HashSet<String>> loadSynonyms(String fileName) throws Exception {

		Map<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();
		
		BufferedReader br = null;

		try {
			
			br = new BufferedReader(
					new InputStreamReader(getClass().getResourceAsStream(fileName), "UTF-8"));

			String line;
            
			while ((line = br.readLine()) != null) {
				//System.err.println(line);
				// e.g., 
				// input: ('n#09896331',' 5 V cinque ',NULL,NULL);
				// output: n#09896331	5 V cinque
				String[] lineSplit = line.split(",");
				String synset = lineSplit[0].replace("(", "").replaceAll("'",  "");
				//System.err.println("pippo=================================");
				//String synsetId = synset.split("#")[1];
				String synsetPoS = synset.split("#")[0];
				
				String synonyms = lineSplit[1].replace("' ", "").replace(" '", "");
				String[] synonymsSplit = synonyms.split(" ");

				if (synonymsSplit.length == 1)
					continue;
				
				for (int j = 0; j < synonymsSplit.length; j++) {
					
					String root_i = synonymsSplit[j].toLowerCase();
					if (root_i.indexOf("_") != -1 || root_i.indexOf("\\") != -1)
						continue;
					
					for (int i = 0; i < synonymsSplit.length; i++) {
						String word_i = synonymsSplit[i].toLowerCase();
						
						if (word_i.indexOf("_") != -1 || word_i.indexOf("\\") != -1)
							continue;
						
						if (j == i)
							continue;
						
						if (result.containsKey(synsetPoS + "#" + root_i)) {
							HashSet<String> entries = result.get(synsetPoS + "#" + root_i);
							entries.add(synsetPoS + "#" + word_i);
						} else {
							HashSet<String> entries = new HashSet<String>();
							entries.add(synsetPoS + "#" + word_i);
							//System.out.println(synsetPoS + "#" + word_i + "\t" + synsetIdList);
							result.put(synsetPoS + "#" + root_i, entries);
							//System.out.println(synsetPoS + "#" + word_i + "\t" + synsetId);
						}
						
					}
				}
			}

		} finally {

			if (br != null)
				br.close();

		}
		
		return result;

	}
	

	/**
	 * A BabelSynset is a set of multilingual lexicalizations that are synonyms expressing a given 
	 * concept or named entity. For instance, the synset for car in the motorcar sense looks like this. 
	 * After creating the BabelNet object which we call bn , we can use its methods to retrieve one 
	 * or many BabelSynset objects. For instance, to retrieve all the synsets containing 
	 * car we can call the BabelNet#getSynsets method.
	 * 
	 * @param key
	 * @param sourceLanguage
	 * @param targetLanguage
	 * @return
	 */
	private List<String> getSynsets(String key, Language sourceLanguage, List<Language> targetLanguages) {
		
		List<String> result = null;
		
		//if (1 == 1)
			//return new ArrayList<String>();
		
		String source = sourceLanguage.toString();
		List<String> target = new ArrayList<String>();
		for (Language language : targetLanguages)
			target.add(language.toString());
		
		result = bn.getSynsets(key, source, target);
		
		return result;
		
	}
	

	
	/**
	 * Say if 2 tokens are synonyms
	 * 
	 * @param token1
	 * @param token2
	 * 
	 * @return true if they are synonyms; false otherwise
	 */
	public boolean synonyms_IT(Token token1, Token token2) {

		if (!synonymsList_IT.containsKey(token1.getWordNetPoSAndLemma()) || 
				!synonymsList_IT.containsKey(token2.getWordNetPoSAndLemma()))
			return false;
		
		return
			synonymsList_IT.get(token1.getWordNetPoSAndLemma()).contains(token2.getWordNetPoSAndLemma());

	}
	
	/**
	 * Say if 2 tokens are synonyms
	 * 
	 * @param token1
	 * @param token2
	 * 
	 * @return true if they are synonyms; false otherwise
	 */
	public boolean synonyms_EN(Token token1, Token token2) {

		if (!synonymsList_EN.containsKey(token1.getWordNetPoSAndLemma()) || 
				!synonymsList_EN.containsKey(token2.getWordNetPoSAndLemma()))
			return false;
		
		return
			synonymsList_EN.get(token1.getWordNetPoSAndLemma()).contains(token2.getWordNetPoSAndLemma());

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
		
		int nKeyphrasesIT = 0;
		int nKeyphrasesDE = 0;
		int nKeyphrasesEN = 0;
		
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
			
			Language language = kx.getLanguage();
			if (language == Language.IT)
				nKeyphrasesIT++;
			else if (language == Language.DE)
				nKeyphrasesDE++;
			else if (language == Language.EN)
				nKeyphrasesEN++;

		}
		
		result.append("\tIT:" + nKeyphrasesIT + "\tDE:" + nKeyphrasesDE + "\tEN:" + nKeyphrasesEN + "\n\n");
		result.append("Babelnet queries:" + nQueriesInBabelnet + "\t" + "Found IT:" + nITKeyphrasesFoundInBabelnet + "(" + nITMonosemicKeyphrasesFoundInBabelnet + ")" + "\tDE:" + nDEKeyphrasesFoundInBabelnet + "(" + nDEMonosemicKeyphrasesFoundInBabelnet + ")" + "\tEN:" + nENKeyphrasesFoundInBabelnet + "(" + nDEMonosemicKeyphrasesFoundInBabelnet + ")" + "\n\n");

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
	 * Load the keyphrases produced by KD; it reads all the iob files
	 * containing the keyphrases.
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

		//int counter = 0;
		for (File file : files) {
			//counter++;
			if (file.isFile()) {
				// System.out.println(file.getName());
				// if (file.getName().endsWith(".tsv")) {
				if (file.getName().endsWith(".iob")
						//&& file.getName().indexOf("DE") != -1
						) {
					// Map<String, Keyphrase> kxs = readKDFiles(file);
					Map<String, Keyphrase> kxs = readIOBFiles(file);
					Iterator<String> it = kxs.keySet().iterator();
					while (it.hasNext()) {
						String kxID = it.next();
						Keyphrase kx = kxs.get(kxID);
						add(kxID, kx, true);
					}
				}
			}

		}

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
	 * Read the file produced by TextPro/TreeTagger and enriched with a new column containing
	 * the kephrases recognized by KD; the information about the language of the document is shown in 
	 * the file name, i.e., IT, DE, EN. This information is used to set the keyphrase language.
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
			
			// set the language of the keyphrases
			Language language = null;
			if (file.getName().indexOf("IT") != -1)
				language = Language.IT;
			else if (file.getName().indexOf("DE") != -1)
				language = Language.DE;
			else if (file.getName().indexOf("EN") != -1)
				language = Language.EN;

			List<Token> tokenBuffer = new ArrayList<Token>();

			int lineNumber = 0;
			while ((line = br.readLine()) != null) {

				String[] splitLine = line.split(" ");
				String kxString = "";
				if (splitLine.length == 4)
					kxString = splitLine[3];
				if (kxString.equals("") || kxString.equals("O")) { // tag: O OR End Of Line
					if (tokenBuffer.size() > 0) {
						//System.out.println(language);
						Keyphrase keyphrase = new Keyphrase(tokenBuffer.size(), language);
						boolean containsName = false;
						for (int i = 0; i < tokenBuffer.size(); i++) {
							keyphrase.add(i, tokenBuffer.get(i));
							if (language == Language.IT && tokenBuffer.get(i).getPoS().startsWith("S") ||
									((language == Language.DE || language == Language.EN) && 
											tokenBuffer.get(i).getPoS().startsWith("N")))
							containsName = true;
						}
						
						/*
						if (keyphrase.getHead() == null) {
							System.out.println(keyphrase.getLanguage() + "\t" + keyphrase.getText());
							for (Token token: keyphrase.getTokens()) {
								System.out.println("\t" + token.getForm() + "\t" + token.getPoS() + "\t" +token.getLemma());
							}
							System.out.println("====================================");
						}
						//else
							//System.out.println("trovato:" + keyphrase.getLanguage() + "\t" + keyphrase.getText());
						*/
						
						if (containsName == true) {
							String kxID = file.getName() + "_" + lineNumber;
							//if (keyphrase.getHead() == null)
							//	System.out.println(keyphrase.getText());
							result.put(kxID, keyphrase);
						}
						//else {
						//	System.out.println(language + " non valida:" + keyphrase.getText());
						//	for (int i = 0; i < tokenBuffer.size(); i++) {
						//		System.out.println(tokenBuffer.get(i).getForm() + "\t" + tokenBuffer.get(i).getPoS()); 
						//	}
						//}
						// System.out.println("====================================");
						tokenBuffer = new ArrayList<Token>();
					}
				} else if (kxString.indexOf("B-") != -1) {
					if (tokenBuffer.size() > 0) {
						Keyphrase keyphrase = new Keyphrase(tokenBuffer.size(), language);
						//boolean containsName = false;
						for (int i = 0; i < tokenBuffer.size(); i++) {
							keyphrase.add(i, tokenBuffer.get(i));
							//if (language == Language.IT && tokenBuffer.get(i).getPoS().startsWith("S") ||
							//		((language == Language.DE || language == Language.EN) && tokenBuffer.get(i).getPoS().startsWith("N")))
							//	containsName = true;
						}
						//if (containsName == true) {
						String kxID = file.getName() + "_" + lineNumber;
						result.put(kxID, keyphrase);
						// System.out.println("====================================");
						tokenBuffer = new ArrayList<Token>();
						//else {
						//	System.out.println(language + " non valida:" + keyphrase.getText());
						//	for (int i = 0; i < tokenBuffer.size(); i++) {
						//		System.out.println(tokenBuffer.get(i).getForm() + "\t" + tokenBuffer.get(i).getPoS()); 
						//	}
					}
					String form = splitLine[0];
					String PoS = splitLine[1];
					String lemma = splitLine[2];
					if (lemma.equals("<unknown>")) // treetagger annotation for DE
						lemma = form;
					// set the WordNet PoS
					String wordnetPoS = getWordNetPoS(PoS, language);
					Token token = new Token(form, PoS, lemma, wordnetPoS);
					tokenBuffer.add(token);
					// System.out.println("token buffer:" + tokenBuffer.size());
				} else { // tag: I
					String form = splitLine[0]; 
					String PoS = splitLine[1];
					String lemma = splitLine[2];
					if (lemma.equals("<unknown>")) // treetagger annotation for DE
						lemma = form;
					// set the WordNet PoS
					String wordnetPoS = getWordNetPoS(PoS, language);
					Token token = new Token(form, PoS, lemma, wordnetPoS);
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
	
	/**
	 * Gets the WordNet PoS given a certain PoS produced by one of the used pipeline (e.g., TextPro)
	 * 
	 * @param PoS the PoS produced by the pipeline
	 * @param language the language of the pipeline
	 * @return the WordNet PoS
	 */
	public static String getWordNetPoS(String PoS, Language language) {
		
		String result = null;
		
		if (language == Language.IT) {
			if (PoS.startsWith("S") || PoS.startsWith("Y")) //- n (noun): SS,SP,SN,SPN,YA,YF
				result = "n";
			else if (PoS.startsWith("V")) //- v (verb): VI,VI+E,VIY,VIY+E,VF,VF+E,VFY,VFY+E,VSP,VSP+E,VSPY,VSPY+E,VPP,VPP+E,VPPY,VPPY+E,VG,VG+E,VGY,VGY+E,VM,VM+E,VMY,VMY+E
				result = "v";
			else if (PoS.startsWith("A") || PoS.startsWith("D")) // - a (adjective): AS,AP,AN,DS,DP,DN  
				result = "a" ;
			else if (PoS.startsWith("B")) // - r (adverb): B
				result = "r";
		}
		else if (language == Language.DE) {
		
		}
		else if (language == Language.EN) {
			// assign the wordnet PoS
			if (PoS.startsWith("N")) //- n (noun): NN0, NN1, NN2, NP0
				result = "n";
			else if (PoS.startsWith("V")) // - v (verb):
				result = "v";
			else if (PoS.startsWith("AJ")) // - a (adjective): 
				result = "a";
			else if (PoS.startsWith("AV")) // - r (adverb):
				result = "r";
		}
		
		return result;
		
	}

}
