package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;

/*
* 
* Criteria: Entailment
* 
* 
* Definition (2017-11-22):

* Check if two variants have the same semantic head and one has just one token less
* 
* 
* E.g.,
* 
* Fondazione Kessler for Fondazione, Bernardo Magnini for Magnini
* ufficio italiano del Consorzio for ufficio italiano (not for the first version because it is two tokens less)
* Magnini for Bernardo Magnini (not  in Roberto’s first version because named entities follow the same rule as all other KPs)
*
*/
public class Entailment {

	// the criteria id
	public static final int id = 9;
	// the criteria description
	public static final String description = "Entailment";

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

		if (key1.length() != key2.length() + 1)
			return false;

		if (!key1.getHead().equals(key2.getHead()))
			return false;

		int i = 0;
		int j = 0;
		int nDifferences = 0;
		while (i < key1.length() && j < key2.length()) {
			if (key1.get(i).equalsFormIgnoreCase(key2.get(j))) {
				i++;
				j++;
			} else {
				nDifferences++;
				i++;
			}
		}
		if (i < key1.length() - 1 || j < key2.length() - 1 || nDifferences > 1)
			return false;

		return true;

	}

}
