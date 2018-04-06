package eu.fbk.hlt.nlp.criteria.de;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Language;
import eu.fbk.hlt.nlp.criteria.AbstractPrepositionalVariant;

/*
* 
: Criteria: PrepositionalVariant
* 
* Version: 0.1
* 
* Definition:
* 
* kj and ki are variants if they have the same tokens except for one preposition or 
* article preposition contractions (defined as tokens to which TreeTagger assigns the PoS 
* whose first letters are AP); these must have same PoS AND different lemma.

* TreeTagger prepositions tagset:
* 
* APPR		Praposition; Zirkumposition	links	in [der Stadt], ohne [mich]
* APPRART	Praposition mit Artikel				im [Haus], zur [Sache]
* APPO		Postposition						[ihm] zufolge, [der Sache] wegen
* APZR 		Zirkumposition 						rechts [von jetzt] an
* 
* @author zanoli
*
*/
public class PrepositionalVariant extends AbstractPrepositionalVariant {

	// version
	public static final String version = "0.1 ";
	// language
	public static final Language.VALUE language = Language.VALUE.DE;
	
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
			else if (key1.get(i).getPoS().startsWith("AP") && 
					key2.get(i).getPoS().startsWith("AP") &&
					!key1.get(i).getLemma().equals(key2.get(i).getLemma()))
					prepositionalVariantCount++;
			else
				return false;
		}

		return (prepositionalVariantCount == 1);

	}

}