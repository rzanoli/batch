package eu.fbk.hlt.nlp.criteria.it;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Language;
import eu.fbk.hlt.nlp.criteria.AbstractSingularPlural;

/*
* 
* Criteria: SingularPlural
* 
* Version: 1.2
* 
* Definition (2018-02-22):
* 
* check if two variants have the same lemma and one or more (at least one) is the plural of the other one (based on the comp-morpho analysis of TextPro)
* e.g. 
* 
* fondazione for fondazioni (and viceversa), mensa dell'università for mensa delle università, 
* sistema per la revisione for sistemi per le revisioni
* 
* NOT: 	
* amico for amica
* “della” for “del”
* “della” for “al”
*
* @author zanoli
*
*/
public class SingularPlural extends AbstractSingularPlural {

	// version
	public static final String version = "1.2";
	// language
	public static final Language language = Language.IT;
	
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
						key1.get(i).getPoS().substring(0,1).equals(key2.get(i).getPoS().substring(0,1)) && 
						(key1.get(i).getPoS().endsWith("S") && key2.get(i).getPoS().endsWith("P") ||
						key1.get(i).getPoS().endsWith("P")) && key2.get(i).getPoS().endsWith("S"))
				singularPluralCount++;
			else {
				return false;
			}
		
		}

		return (singularPluralCount != 0);

	}

}
