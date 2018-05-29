package eu.fbk.hlt.nlp.criteria.en;

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
* NOTE: 	
* 
* NN1	 Singular common noun	pencil, goose, time, revelation
* NN2	 Plural common noun	pencils, geese, times, revelations
*
* @author zanoli
*
*/
public class SingularPlural extends AbstractSingularPlural {

	// version
	public static final String version = "0.1";
	// language
	public static final Language language = Language.EN;
	
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
						((key1.get(i).getPoS().equals("NN1") && key2.get(i).getPoS().equals("NN2")) ||
						(key1.get(i).getPoS().equals("NN2") && key2.get(i).getPoS().equals("NN1"))))
				singularPluralCount++;
			else {
				return false;
			}
		
		}

		return (singularPluralCount != 0);

	}

}
