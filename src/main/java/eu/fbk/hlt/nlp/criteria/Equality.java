package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.gcluster.Keyphrase;

//import org.easyrules.annotation.Condition;
//import org.easyrules.annotation.Rule;


/*
* kj and kl are equal (i.e. same tokens in the same order). 
* This is a special case, as occurrences of the same keyphrase are not considered as 
* different variants, rather we collapse them into a single keyphrase type.
*/
//@Rule(name = "10", description = "Equality")
public class Equality {
	
	public static final int id = 10;
	public static final  String description = "Equality";
	
	public static boolean evaluate(Keyphrase kx1, Keyphrase kx2) {
		
		if (kx1.length() != kx2.length())
			return false;
		
		for (int i = 0; i < kx1.length(); i++)
			if (!kx1.get(i).equals(kx2.get(i)))
				return false;
					
		return true;
		
	}

}
