package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;

/*
* 
* Criteria: Abbreviation
* 
* 
* Definition (2017-11-22):
* 
* We consider an abbreviation any token which finishes with a dot and we check if it is a substring of some 
* token in another keyphrase; kj and ki must have the same number of tokens in the same order, and the number of token must be more than 1, 
* one token in kj can be the abbreviation of one token in ki
*
* 
* E.g., 
* Prof. for Professor, B. for Bernardo
* Uni for University (not for the first version because it has no dot)
*/
public class Abbreviation {

	// the criteria id
	public static final int id = 2;
	// the criteria description
	public static final String description = "Abbreviation";

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

		if (kx1.length() < 2) 
			return false;
		
		if (kx1.length() != kx2.length())
			return false;

		if(kx2.containsAbbreviation() == false)
			return false;

		for (int i = 0; i < kx2.length(); i++) {
			if (kx2.get(i).isAbbreviation()) {
				if (!kx2.get(i).isAbbreviationOf(kx1.get(i))) {
					return false;
				}
			} else {
				if (!kx2.get(i).equals(kx1.get(i))) {
					return false;
				}
			}
		}

		return true;

	}

}
