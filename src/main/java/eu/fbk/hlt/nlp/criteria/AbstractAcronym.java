package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Language;
import eu.fbk.hlt.nlp.cluster.Token;


/**
 * 
 * Criteria: Acronym
 * 
 * Version: 1.2
 * 
 * Definition (2017-11-22):
 * 
 * We consider as acronym: any token with two or more capitalized letters,
 * either with dots or without dots (e.g. FBK, S.p.A.) any token which has two
 * or more dots e.g. (S.r.l.)
 * 
 * kj and ki are variants if kj consists of one token of n>1 letters and ki
 * consists of n tokens; the initials of the n tokens of ki are the letters
 * composing the single token of kj in the same order.
 * 
 *
 * E.g.,:
 * 
 * FBK and Fondazione Bruno Kessler S.p.A. and Societa' per Azioni S.p.a. and
 * Societa' per azioni
 *
 * @author zanoli
 *
 */
public class AbstractAcronym {

	// the criteria id
	public static final int id = 3;
	// the criteria description
	public static final String description = "Acronym";
	// version
	public static final String version = "1.2"; // IT
	// language
	public static final Language.VALUE language = Language.VALUE.MULTILINGUAL;

	/**
	 * Given a keyphrase key1, can the keyphrase kx2 be derived from key1?
	 * 
	 * @param key1
	 *            the keyphrase key1
	 * @param kx2
	 *            the keyphrase kx2
	 * 
	 * @return if kx2 can be derived from kx1
	 */
	public static boolean evaluate(Keyphrase key1, Keyphrase key2) {

		if (key2.length() > 1 || 
				key2.get(0).isAcronym() == false || 
				key2.get(0).characters() != key1.length() || 
				key2.get(0).startWith() != key1.get(0).startWith() || 
				key2.get(0).characters() < 2)
			return false;

		Token kx2Token0 = key2.get(0);
		int i = 0, j = 0;
		do {
			if (kx2Token0.charAt(i) == '.') {
				i++;
				continue;
			} else if (kx2Token0.charAt(i) != key1.get(j).startWith())
				return false;

			i++;
			j++;
		} while (i < kx2Token0.length());

		return true;

	}

}
