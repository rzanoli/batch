package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;

/*
* 
* Criteria: PrepositionalVariant
* 
* Definition (2018-02-22):
* 
* kj and ki are variants if they have the same tokens except for one preposition or article preposition 
* contractions (defined as tokens to which TextPro assigns the PoS E ES or EP); these must have same PoS AND 
* different lemma (in all cases we use whole lemma - di has lemma di, whereas del has lemma di/det - ).
*
* E.g., 
* FBK di Trento for FBK a Trento (same PoS E, different lemmas di and a)
* “del” for “al”  (same PoS ES, different lemmas di/det and a/det)
* “dei” for “ai”  (same PoS EP, different lemmas di/i and a/i)
* NOT: 
* mensa dell’università for mensa delle università (different PoS: ES and EP) Criterion 4 sing/plur applies instead
* mensa dell’università for mensa alle università (different PoS: ES and EP)
* regole di gioco for regole del gioco (same lemma: di) - this is covered by criterion 12
*
*/
public class PrepositionalVariant {

	// the criteria id
	public static final int id = 1;
	// the criteria description
	public static final String description = "PrepositionalVariant";

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
			else if ( (key1.get(i).getPoS().equals("E") && 
					key2.get(i).getPoS().equals("E") &&
					!key1.get(i).getLemma().equals(key2.get(i).getLemma())) ||
					
					(((key1.get(i).getPoS().equals("ES") && 
					key2.get(i).getPoS().equals("ES")) ||
					(key1.get(i).getPoS().equals("EP") && 
							key2.get(i).getPoS().equals("EP"))) &&
					!key1.get(i).getLemma().equals(key2.get(i).getLemma())) )
					prepositionalVariantCount++;
			else
				return false;
		}

		return (prepositionalVariantCount == 1);

	}

}