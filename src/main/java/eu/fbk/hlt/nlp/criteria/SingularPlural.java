package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;

/*
* 
* Criteria: SingularPlural
* 
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
*
*/
public class SingularPlural {

	// the criteria id
	public static final int id = 4;
	// the criteria description
	public static final String description = "SingularPlural";

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
