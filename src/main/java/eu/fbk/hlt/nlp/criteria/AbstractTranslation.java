package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Language;

/*
 * 
 * Criteria: Translation
 *
 * @author zanoli
 *
 */
public abstract class AbstractTranslation {

	// the criteria id
	public static final int id = 20;
	// the criteria description
	public static final String description = "Translation";
	// version
	public static final String version = "1.2"; // IT
	// language
	public static final Language language = Language.MULTILINGUAL;
	
	/**
	 * Given a keyphrase key1, can the keyphrase key2 be derived from key1?
	 * 
	 * @param key1
	 *            the keyphrase key1
	 * @param key2
	 *            the keyphrase key2
	 *            
	 * @return if keyphrase2 can be derived from keyphrase1
	 */
	public static boolean evaluate(Keyphrase key1, Keyphrase key2) {
		
		for (String synset : key1.getbabelnetSynset()) {

			if (key2.getbabelnetSynset().contains(synset))

				return true;

		}

		return false;

	}

}
