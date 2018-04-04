package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Keyphrases;

/*
 * 
 * Criteria: Synonyms
 * 
 * @author zanoli
 *
 */
public class AbstractSynonymy {

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
	 * @param keys
	 *            the keyphrases data structure containing the list of synonyms
	 * 
	 * @return if key2 can be derived from key1
	 */
	public static boolean evaluate(Keyphrase key1, Keyphrase key2, Keyphrases keys) {
		
		return false;
		
	}
	
}
