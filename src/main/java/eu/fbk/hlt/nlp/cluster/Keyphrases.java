package eu.fbk.hlt.nlp.cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This class represents the list of keyphrases in input to cluster.
 * 
 * @author rzanoli
 *
 */
public class Keyphrases {

	// it is used to store a keyphrase with all the documents ids where it appears
	// it uses a LinkedHashMap to keep the keys in the order they were inserted
	private Map<Keyphrase, List<String>> masterList;
	// the list of unique keyphrases
	private List<Keyphrase> innerList;
	// it is used by the Comparator class to move to the next keyphrase to compare
	public int cursor;
	// the number of keyphrases in input
	private int nKeyphrases;

	/**
	 * The constructor
	 */
	public Keyphrases() {

		masterList = new LinkedHashMap<Keyphrase, List<String>>();
		innerList = new ArrayList<Keyphrase>();

	}
	
	/**
	 * The constructor
	 */
	public Keyphrases(File fileName) throws Exception {

		loadMasterList(fileName);

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
			List<String> ids = new ArrayList<String>();
			ids.add(id);
			masterList.put(kx, ids);
			//kx.setIkD(innerList.size());
			innerList.add(kx);
		} else {
			List<String> ids = masterList.get(kx);
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

		StringBuffer result = new StringBuffer();
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

		return this.innerList.size();

	}

	/**
	 * The total number of keyphrases in input
	 * 
	 * @return the number of keyphrases
	 */
	public int nKephrases() {

		return this.nKeyphrases;

	}

	/**
	 * Increase the cursor to move to the next keyphrase
	 * 
	 * @return the index to the next keyphrase
	 */
	public synchronized int next() {

		if (cursor % 1000 == 0)
			System.out.printf("\r%s %s", "keyphrases analized:", cursor * 100 / innerList.size() + "%");
		return cursor++;

	}
	
	/**
	 * Print the master list
	 * 
	 */
	public void saveMasterList(File fileName) throws Exception {

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
		
			Iterator<Keyphrase> kx_it = masterList.keySet().iterator();
			while(kx_it.hasNext()) {
				Keyphrase kx = kx_it.next();
				String kxText = kx.getText();
				bw.write(kxText + "\t");
				List<String> ids = masterList.get(kx);
				Iterator<String> id_it = ids.iterator();
				while(id_it.hasNext())
					bw.write(id_it.next() + " ");
				bw.write("\n");
			}
		
		} finally {

			if (bw != null)
				bw.close();

			if (fw != null)
				fw.close();

		}

	}
	
	
	/**
	 * Print the inner list
	 * 
	 */
	private String printInnerList() {

		StringBuilder result = new StringBuilder();
		
		Iterator<Keyphrase> kx_it = innerList.iterator();
		while(kx_it.hasNext()) {
		    Keyphrase kx = kx_it.next();
		    String kxText = kx.getText();
			result.append(kxText + "\n");
		}
		
		return result.toString();

	}
	
	/**
	 * Print the master list
	 * 
	 */
	private void loadMasterList(File fileName) throws Exception {

		masterList = new HashMap<Keyphrase, List<String>>();
		
		BufferedReader br = null;
		FileReader fr = null;
		
		try {

			fr = new FileReader(fileName);
			br = new BufferedReader(fr);

			String line;

			while ((line = br.readLine()) != null) {
				String[] splitLine = line.split("\t");
				String kxText = splitLine[0];
				Keyphrase kx = new Keyphrase(kxText);
				String[] ids = splitLine[1].split(" ");
				for (int i = 0; i < ids.length; i++)
					add(ids[i], kx);
			}

		} finally {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();
			
		}

	}

}
