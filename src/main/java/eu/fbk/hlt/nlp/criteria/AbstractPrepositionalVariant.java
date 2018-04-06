package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;

/*
 * 
 * Criteria: PrepositionalVariant
 *
 * @author zanoli
 *
 */
public class AbstractPrepositionalVariant {
	
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
		
		return false;
		
	}
	
}