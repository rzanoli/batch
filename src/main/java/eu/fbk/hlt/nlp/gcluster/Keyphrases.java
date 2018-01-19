package eu.fbk.hlt.nlp.gcluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the list of keyphrases in input to cluster.
 * 
 * @author rzanoli
 *
 */
public class Keyphrases {

	// it is used to store a keyphrase with all the documents ids where it appears
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

		masterList = new HashMap<Keyphrase, List<String>>();
		innerList = new ArrayList<Keyphrase>();

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
			kx.setID(innerList.size());
			masterList.put(kx, ids);
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

}
