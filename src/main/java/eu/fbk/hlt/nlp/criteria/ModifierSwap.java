package eu.fbk.hlt.nlp.criteria;

import eu.fbk.hlt.nlp.cluster.Keyphrase;

/*
* 
* Criteria: ModifierSwap
* 
* 
* Definition (2017-11-22):
* 
* Modifier swap; 
* check if all tokens are the same in different order and the head is the same (no permutation of the head)
* e.g. elezioni francesi 2017 for elezioni 2017 francesi
*
*/
public class ModifierSwap {

	// the criteria id
	public static final int id = 7;
	// the criteria description
	public static final String description = "ModifierSwap";

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

		if (kx1.length() != kx2.length()) {
			return false;
		}
		
		if (kx1.length() == 1) //OR kx2.length() == 1; it means at least 2 tokens
			return false;
		
		if (kx1.getHeadPosition() != kx2.getHeadPosition()) {
			return false;
		}
		
		if (!kx1.getHead().equals(kx2.getHead())) {
			return false;
		}

		boolean differentPosition = false;
		boolean[] visited = new boolean[kx1.length()];
		for (int i = 0; i < kx1.length(); i++) {
			boolean found = false;
			for (int j = 0; j < kx2.length(); j++) {
				if (visited[j] == true)
					continue;
				if (kx1.get(i).equals(kx2.get(j))) {
						visited[j] = true;
						found = true;
						if (i != j)
							differentPosition = true;
						break;
				}
			}
			if (found == false)
				return false;
		}
		
		if (differentPosition == false)
			return false;

		return true;

	}

}
