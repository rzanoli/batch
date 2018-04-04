package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;

/*
 * 
 * Criteria: SingularPlural
 * 
 * @author zanoli
 *
 */
public class AbstractSingularPlural {

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
	 *            
	 * @return if key2 can be derived from key1
	 */
	public static boolean evaluate(Keyphrase key1, Keyphrase key2) {
		
		return false;
		
	}
	
}
