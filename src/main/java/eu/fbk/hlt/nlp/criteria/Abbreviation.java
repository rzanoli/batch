package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.gcluster.Keyphrase;

//import org.easyrules.annotation.Rule;


/*
* 
* We consider an abbreviation any token which finishes with a dot and we check if it is a substring of some 
* token in another keyphrase; kj and ki must have the same number of tokens in the same order, one or more tokens 
* in kj can be the abbreviation of one or more tokens in ki
* E.g. Prof. for Professor, B. for Bernardo
* Uni for University (not for the first version because it has no dot)
*/
//@Rule(name = "2", description = "Abbreviation")
public class Abbreviation {
	
	public static final int id = 2;
	public static final  String description = "Abbreviation";
	
	public static boolean evaluate(Keyphrase kx1, Keyphrase kx2) {
		
		if (kx1.length() != kx2.length()) {
			return false;
		}
		
		boolean containsAbbreviations = false;
		for (int i = 0; i < kx2.length(); i++) {
			if (kx2.get(i).isAbbreviation()) {
				containsAbbreviations = true;
				break;
			}
		}
		if (containsAbbreviations == false) {
			return false;
		}
		
		for (int i = 0; i < kx2.length(); i++) {
			if (kx2.get(i).isAbbreviation()) {
				if (!kx2.get(i).isAbbreviationOf(kx1.get(i))) {
					return false;
				}
			}
			else {
				if(!kx2.get(i).equals(kx1.get(i))) {
					return false;
				}
			}
		}
					
		return true;
		
	}

}
