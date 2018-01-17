package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.gcluster.Keyphrase;

//import org.easyrules.annotation.Condition;
//import org.easyrules.annotation.Rule;


/*
* 
* Entailment
* check if two variants have the same semantic head and one has just one token less
* e.g. Fondazione Kessler for Fondazione, Bernardo Magnini for Magnini
* ufficio italiano del Consorzio for ufficio italiano (not for the first version because it is two tokens less)
*
*/
//@Rule(name = "9", description = "Entailment")
public class Entailment {
	
	public static final int id = 9;
	public static final  String description = "Entailment";
	
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
