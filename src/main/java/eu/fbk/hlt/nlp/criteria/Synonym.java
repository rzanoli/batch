package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Keyphrases;

/*
* 
* Criteria: Synonyms
* 
* 
* Definition (2018-02-22):
* 
* kj and ki are variants if they consist of more than one token and one of the tokens in kj is a 
* synonym of ki in MultiWordNet and the have the same PoS; the check is performed at the level of 
* lemma (independently of number and gender)
* 
*/
public class Synonym {

	// the criteria id
	public static final int id = 11;
	// the criteria description
	public static final String description = "Synonym";

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
	public static boolean evaluate(Keyphrase key1, Keyphrase key2, Keyphrases keys) {

		if (key1.length() != key2.length() || key1.length() < 2) {
			return false;
		}

		int synonymsCount = 0;
		for (int i = 0; i < key1.length(); i++) {
			if (key1.get(i).getForm().equals(key2.get(i).getForm())) {
			}
			else if (key1.get(i).getPoS().equals(key2.get(i).getPoS()) &&
					keys.synonyms(key1.get(i), key2.get(i))) {
				synonymsCount++;
			}
			else
				return false;
		} 
		
		return (synonymsCount == 1);

	}

}
