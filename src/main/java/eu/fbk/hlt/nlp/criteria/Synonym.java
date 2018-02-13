package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Keyphrases;

/*
* 
* Criteria: Synonyms
* 
* 
* Definition ():
* 
*
*
* 
* E.g., 
* 
*/
public class Synonym {

	// the criteria id
	public static final int id = 11;
	// the criteria description
	public static final String description = "Synonym";

	/**
	 * Given a keyphrase key1, can the keyphrase key2 be derived from key1?
	 * 
	 * @param key1
	 *            the keyphrase key1
	 * @param key2
	 *            the keyphrase key2
	 * 
	 * @return if key2 can be derived from key1
	 */
	public static boolean evaluate(Keyphrase key1, Keyphrase key2) {

		if (key1.length() != key2.length()) {
			return false;
		}

		int synonymsCount = 0;
		for (int i = 0; i < key1.length(); i++) {
			if (key1.get(i).equalsFormIgnoreCase(key2.get(i)) || 
					key1.get(i).equalsLemma(key2.get(i))) {}
			else if (Keyphrases.synonyms(key1.get(i), key2.get(i)))
				synonymsCount++;
			else
				return false;
		}

		return (synonymsCount != 0);

	}

}
