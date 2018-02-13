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

		if (key1.length() != key2.length()) {
			return false;
		}

		if (key1.length() == 1)
			return false;

		if (key1.getHeadPosition() != key2.getHeadPosition()) {
			return false;
		}

		if (!key1.getHead().equals(key2.getHead())) {
			return false;
		}

		boolean differentPosition = false;
		boolean[] visited = new boolean[key1.length()];
		for (int i = 0; i < key1.length(); i++) {
			boolean found = false;
			for (int j = 0; j < key2.length(); j++) {
				if (visited[j] == true)
					continue;
				if (key1.get(i).equalsFormIgnoreCase(key2.get(j))) {
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
