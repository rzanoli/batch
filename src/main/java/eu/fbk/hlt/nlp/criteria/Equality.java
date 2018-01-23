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
	 * Given a keyphrase kx1, can the keyphrase kx2 be derived from kx1?
	 * 
	 * @param kx1
	 *            the keyphrase kx1
	 * @param kx2
	 *            the keyphrase kx2
	 * @return if kx2 can be derived from kx1
	 */
	public static boolean evaluate(Keyphrase kx1, Keyphrase kx2) {

		if (kx1.length() != kx2.length())
			return false;

		for (int i = 0; i < kx1.length(); i++)
			if (!kx1.get(i).equals(kx2.get(i)))
				return false;

		return true;

	}

}
