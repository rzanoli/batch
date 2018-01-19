package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.gcluster.Keyphrase;
import eu.fbk.hlt.nlp.gcluster.Token;

/**
 * 
 * Criteria: Acronym
 * 
 * 
 * Definition (2017-11-22):
 * 
 * kj and ki are variants if kj consists of one token of n>1 letters and ki
 * consists of n tokens; the initials of the n tokens of ki are the letters
 * composing the single token of kj in the same order.
 * 
 * 
 * E.g.,:
 * 
 * FBK and Fondazione Bruno Kessler 
 * S.p.A. and Societa' per Azioni 
 * S.p.a. and Societa' per azioni 
 * spa and “Societa' per azioni” (new, for Roberto's first version)
 *
 * @author zanoli
 *
 */
public class Acronym {

	// the criteria id
	public static final int id = 3;
	// the criteria description
	public static final String description = "Acronym";

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

		if (kx2.length() > 1 || kx2.get(0).characters() != kx1.length()
				|| kx2.get(0).startWith() != kx1.get(0).startWith() || kx2.get(0).characters() < 2)
			return false;

		Token kx2Token0 = kx2.get(0);
		int i = 0, j = 0;
		do {
			if (kx2Token0.charAt(i) == '.') {
				i++;
				continue;
			} else if (kx2Token0.charAt(i) != kx1.get(j).startWith())
				return false;
			i++;
			j++;
		} while (i < kx2Token0.length());

		return true;

	}

}
