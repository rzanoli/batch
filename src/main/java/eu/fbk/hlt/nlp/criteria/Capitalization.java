package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;

/*
 * 
 * Criteria: Equality
 * 
 * Definition (2017-11-22):
 * 
 * kj and kl are equal (i.e. same tokens in the same order). 
 * This is a special case, as occurrences of the same keyphrase are not considered as 
 * different variants, rather we collapse them into a single keyphrase type.
*/
public class Equality {

	// the criteria id
	public static final int id = 10;
	// the criteria description
	public static final String description = "Equality";

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

		for (int i = 0; i < key1.length(); i++) {

			if (!key1.get(i).equalsFormIgnoreCase(key2.get(i)))
				return false;

		}

		return true;

	}

}
