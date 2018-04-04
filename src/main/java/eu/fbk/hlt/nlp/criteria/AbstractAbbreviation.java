package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Language;

/*
* 
* Criteria: Abbreviation
* 
* Version: 1.2
* 
* Definition (2018-02-22):
* 
* We consider an abbreviation any token which finishes with a dot and we check if it 
* is a substring of some token not containing dots in another keyphrase; kj and ki must have 
* the same number of tokens in the same order, and the number of tokens must be more than 1, one token 
* in kj (and not more than one) can be the abbreviation of one token in ki
* 
* E.g.,
* Prof. Magnini for Professor Magnini, 
* B. Magnini for Bernardo Magnini
* 
* @author zanoli
*
*/
public abstract class AbstractAbbreviation {

	// the criteria id
	public static final int id = 2;
	// the criteria description
	public static final String description = "Abbreviation";
	// version
	public static final String version = "1.2"; // IT
	// language
	public static final Language language = Language.MULTILINGUAL;

	/**
	 * Given a keyphrase key1, can the keyphrase key2 be derived from key1?
	 * 
	 * @param key1
	 *            the keyphrase key1
	 * @param key2
	 *            the keyphrase key2
	 * @return if keyphrase2 can be derived from keyphrase1
	 */
	public static boolean evaluate(Keyphrase key1, Keyphrase key2) {

		if (key1.length() < 2)
			return false;

		if (key1.length() != key2.length())
			return false;
  
		if (key2.abbreviations() != 1 || 
				key1.abbreviations() > 0)
			return false;

		for (int i = 0; i < key2.length(); i++) {
			if (key2.get(i).isAbbreviation()) {
				if (!key2.get(i).isAbbreviationOf(key1.get(i))) {
					return false;
				}
			} else {
				if (!key2.get(i).getForm().equals(key1.get(i).getForm()))
					return false;
			}
		}

		return true;

	}

}
