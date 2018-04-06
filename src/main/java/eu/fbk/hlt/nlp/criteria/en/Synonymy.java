package eu.fbk.hlt.nlp.criteria.en;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Keyphrases;
import eu.fbk.hlt.nlp.cluster.Language;
import eu.fbk.hlt.nlp.criteria.AbstractSynonymy;

/*
* 
* Criteria:
* 
* Version: 0.1
* 
* Definition:
* 
* @author zanoli
* 
*/
public class Synonymy extends AbstractSynonymy {

	// version
	public static final String version = "0.1";
	// language
	public static final Language.VALUE language = Language.VALUE.EN;
	
	/**
	 * Given a keyphrase key1, can the keyphrase key2 be derived from key1?
	 * 
	 * @param key1
	 *            the keyphrase key1
	 * @param key2
	 *            the keyphrase key2
	 * @param keys
	 *  		  the keyphrases data structure containing the list of synonyms
	 * 
	 * @return if key2 can be derived from key1
	 */
	public static boolean evaluate(Keyphrase key1, Keyphrase key2, Keyphrases keys) {

		if (key1.length() != key2.length() || key1.length() < 2) {
			return false;
		}

		int synonymsCount = 0;
		for (int i = 0; i < key1.length(); i++) {
			if (key1.get(i).getForm().equals(key2.get(i).getForm())) {
			}
			else if (key1.get(i).getPoS().equals(key2.get(i).getPoS()) &&
					keys.synonyms_EN(key1.get(i), key2.get(i))) {
				synonymsCount++;
			}
			else
				return false;
		} 
		
		return (synonymsCount == 1);

	}

}
