package eu.fbk.hlt.nlp.criteria.de;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Language;
import eu.fbk.hlt.nlp.criteria.AbstractSingularPlural;

/*
* 
* Criteria: SingularPlural
* 
* Version: 0.1
* 
* Definition:
* 
* As the Tretagger’s PoS does not have the information about sing/plur, we check in 
* all the lemmas in the two variants have the same PoS and the same lemma; assumption: if 
* they were the same, one would have been removed as duplicate, so there must be a different 
* either in number or in gender
*
* @author zanoli
*
*/
public class SingularPlural extends AbstractSingularPlural {

	// version
	public static final String version = "0.1";
	// language
	public static final Language.VALUE language = Language.VALUE.DE;
	
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

		int singularPluralCount = 0;
		for (int i = 0; i < key1.length(); i++) {
			
			if (key1.get(i).getForm().equals(key2.get(i).getForm())) {
			}
			else if (key1.get(i).getLemma().equals(key2.get(i).getLemma()) &&
						key1.get(i).getPoS().equals(key2.get(i).getPoS()))
				singularPluralCount++;
			else {
				return false;
			}
		
		}

		return (singularPluralCount != 0);

	}

}
