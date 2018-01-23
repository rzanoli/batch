package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;


/*
* 
* Criteria: Entailment
* 
* 
* Definition (2017-11-22):

* Check if two variants have the same semantic head and one has just one token less
* 
* 
* E.g.,
* 
* Fondazione Kessler for Fondazione, Bernardo Magnini for Magnini
* ufficio italiano del Consorzio for ufficio italiano (not for the first version because it is two tokens less)
* Magnini for Bernardo Magnini (not  in Roberto’s first version because named entities follow the same rule as all other KPs)
*
*/
public class Entailment {
	
	// the criteria id
	public static final int id = 9;
	// the criteria description
	public static final  String description = "Entailment";
	
	/**
	 * Given a keyphrase kx1, can the keyphrase kx2 be derived from kx1?
	 * 
	 * @param kx1
	 *            the keyphrase kx1
	 * @param kx2
	 *            the keyphrase kx2
	 * @return if kx2 can be derived from kx1
	 */
	public static boolean evaluate(Keyphrase kx1, Keyphrase kx2) {
		
		if (kx1.length() != kx2.length() + 1)
			return false;
		
		if (!kx1.get(0).equals(kx2.get(0)))
				return false;
		
		int i =0;
		int j = 0;
		int nDifferences = 0;
		while(i < kx1.length() && j < kx2.length()) {
			if (kx1.get(i).equals(kx2.get(j))) {
				i++;
				j++;
			}
			else {
				nDifferences++;
				i++;
			}		
		}
		if (i < kx1.length() -1 ||  j < kx2.length() -1 || nDifferences > 1)
			return false;
		
		return true;
		
	}

}
