package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.gcluster.Keyphrase;
import eu.fbk.hlt.nlp.gcluster.Token;

/**
 * kj and ki are variant if one of the tokens in kj consisting of n letters can
 * be the acronym of n tokens in ki (i.e. the initials of the n tokens of ki are
 * the same letters of the acronym token in kj in the same order) and kj and ki
 * have the same number of other tokens in the same order. EXAMPLES: “FBK” and
 * “Fondazione Bruno Kessler” “S.p.A.” and “Societa’ per Azioni” “S.p.a.” and
 * “Societa’ per azioni” “FBK in Povo” and “Fondazione Bruno Kessler in Povo”
 *
 *
 * @author zanoli
 *
 */

//@Rule(name = "3", description = "Acronym")
public class Acronym {

	public static final int id = 3;
	public static final  String description = "Acronym";
	
	public static boolean evaluate(Keyphrase kx1, Keyphrase kx2) {

		if (kx2.length() > 1 || 
				kx2.get(0).characters() != kx1.length() ||
				kx2.get(0).startWith() != kx1.get(0).startWith() ||
				kx2.get(0).characters() < 2)
			return false;

		Token kx2Token0 = kx2.get(0);
		int i = 0, j = 0;
		do {
			if (kx2Token0.charAt(i) == '.') {
				i++;
				continue;
			}
			else if (kx2Token0.charAt(i) != kx1.get(j).startWith())
				return false;
			
			i++; j++;
		} while(i < kx2Token0.length());
		
		return true;

	}

}
