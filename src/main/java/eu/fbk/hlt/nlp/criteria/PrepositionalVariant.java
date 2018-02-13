package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;

/*
* 
* Criteria: PrepositionalVariant
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
public class PrepositionalVariant {

	// the criteria id
	public static final int id = 12;
	// the criteria description
	public static final String description = "PrepositionalVariant";

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

		if (key1.length() != key2.length())
			return false;
		
		if (key1.prepositions() == 0 ||
				key2.prepositions() == 0)
			return false;
		
		int prepositionsCount = 0;
		for (int i = 0; i < key1.length(); i++) {
			if (key1.get(i).equalsFormIgnoreCase(key2.get(i))) {
			}
			else if (key1.get(i).getPoS().startsWith("E") && 
					key2.get(i).getPoS().startsWith("E"))
				prepositionsCount++;
			else
				return false;
		}

		return (prepositionsCount != 0);

	}

}