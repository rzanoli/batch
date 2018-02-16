package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;

/*
* 
* Criteria: ModifierSwap
* 
* 
* Definition (2017-11-22):
* 
* Singular/Plural;
* check if two variants have the same lemma (based on the comp-morpho analysis of TextPro)
* e.g. fondazione for fondazioni (and viceversa), amico for amica (in the first version we include also masculine/feminine)
*
*
*/
public class SingularPlural {

	// the criteria id
	public static final int id = 4;
	// the criteria description
	public static final String description = "SingularPlural";

	/**
	 * Given a keyphrase key1, can the keyphrase key2 be derived from key1?
	 * 
	 * @param key1
	 *            the keyphrase key1
	 * @param key2
	 *            the keyphrase key2
	 * @return if key2 can be derived from key1
	 */
	public static boolean evaluate(Keyphrase key1, Keyphrase key2) {

		if (key1.length() != key2.length()) {
			return false;
		}

		int lemmaCount = 0;
		for (int i = 0; i < key1.length(); i++) {
			
			if (key1.get(i).equalsFormIgnoreCase(key2.get(i))) {
			}
			else if (key1.get(i).getLemma().equals(key2.get(i).getLemma()) &&
					((key1.get(i).getPoS().endsWith("P") && key2.get(i).getPoS().endsWith("S")) ||
					(key1.get(i).getPoS().endsWith("S") && key2.get(i).getPoS().endsWith("P")))) {
				lemmaCount++;
			} else {
				return false;
			}
		}

		return (lemmaCount != 0);

	}

}
