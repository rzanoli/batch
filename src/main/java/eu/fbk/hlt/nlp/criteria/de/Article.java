package eu.fbk.hlt.nlp.criteria.de;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Keyphrases;
import eu.fbk.hlt.nlp.cluster.Language;
import eu.fbk.hlt.nlp.criteria.AbstractArticle;

/*
* 
* Criteria:
* 
* Version:
* 
* Definition:
* 
* @author zanoli
*/
public class Article extends AbstractArticle {

	// version
	public static final String version = "0.1";
	// language
	public static final Language language = Language.DE;
	
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

		if (key1.length() != key2.length()) {
			return false;
		}

		int articlesCount = 0;
		for (int i = 0; i < key1.length(); i++) {

			if (key1.get(i).getForm().equals(key2.get(i).getForm())) {
			} else if (((key1.get(i).getPoS().equals("APPR") && key2.get(i).getPoS().equals("APPRART")) || 
					(key2.get(i).getPoS().equals("APPRART") && key1.get(i).getPoS().equals("APPR")))
					&& key1.get(i).getLemma().equals(key2.get(i).getLemma()))
				articlesCount++;
			else {
				return false;
			}

		}

		return (articlesCount == 1);

	}
	
}
