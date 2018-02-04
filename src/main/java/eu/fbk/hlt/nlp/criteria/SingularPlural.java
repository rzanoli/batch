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
	 * Given a keyphrase kx1, can the keyphrase kx2 be derived from kx1?
	 * 
	 * @param kx1
	 *            the keyphrase kx1
	 * @param kx2
	 *            the keyphrase kx2
	 * @return if kx2 can be derived from kx1
	 */
	public static boolean evaluate(Keyphrase kx1, Keyphrase kx2) {

		if (kx1.length() != kx2.length()) {
			return false;
		}

		int formaCount = 0;
		int lemmaCount = 0;
		for (int i = 0; i < kx1.length(); i++) {
			if (kx1.get(i).equals(kx2.get(i)))
					formaCount++;
			else if (kx1.get(i).equalsLemma(kx2.get(i)))
					lemmaCount++;
			else
				return false;
		}
		
		return (lemmaCount != 0);

	}

}
