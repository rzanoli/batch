package eu.fbk.hlt.nlp.criteria.en;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Language;
import eu.fbk.hlt.nlp.criteria.AbstractPrepositionalVariant;

/*
* 
* Criteria: PrepositionalVariant
* 
* Version: 0.1
* 
* Definition:
* 
* Note:
* 
* TextPro tagset:
* PRF	 The preposition of	of
* PRP	 Preposition (except for of)	about, at, in, on, on behalf of, with
* 
* @author zanoli
*
*/
public class PrepositionalVariant extends AbstractPrepositionalVariant {

	// version
	public static final String version = "0.1 ";
	// language
	public static final Language language = Language.EN;
	
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
		
		int prepositionalVariantCount = 0;
		for (int i = 0; i < key1.length(); i++) {
			if (key1.get(i).getForm().equals(key2.get(i).getForm())) {
			}
			else if (key1.get(i).getPoS().startsWith("PR") && 
					key2.get(i).getPoS().startsWith("PR") &&
					!key1.get(i).getLemma().equals(key2.get(i).getLemma())) 
					prepositionalVariantCount++;
			else
				return false;
		}

		return (prepositionalVariantCount == 1);

	}

}