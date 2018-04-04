package eu.fbk.hlt.nlp.cluster;

import java.util.concurrent.atomic.AtomicBoolean;



/**
 * This class represents a comparator that compares the keyphrases in input each
 * other and cluster them by building the graph of the edged keyphrases. The
 * comparison is computed by applying the implemented criteria, e.g.,
 * abbreviation, acronym. The class implements the interface Runnable to be
 * parallelized.
 * 
 * @author rzanoli
 *
 */
public class Comparator implements Runnable {

	// true to stop the thread
	private AtomicBoolean interrupted;
	// the list of keyphrases in input
	private Keyphrases keys;
	// the graph structure that will contain the edged keyphrases
	private Graph graph;

	/**
	 * The constructor
	 * 
	 * @param interrupted
	 *            true to stop the thread
	 * @param kxs
	 *            the list of keyphrases to cluster
	 * @param graph
	 *            the graph structure containing the created clusters of keyphrases
	 * 
	 */
	public Comparator(AtomicBoolean interrupted, Keyphrases keys, Graph graph) {

		this.interrupted = interrupted;
		this.keys = keys;
		this.graph = graph;

	}

	/**
	 * Start the comparison process; each keyphrase is compared wit the others by
	 * applying the given criteria; then the edged keyphrases are put in the graph.
	 */
	public void run() {

		while (!interrupted.get()) {

			// get the next keyphrase to compare with all the
			// other keyphrases
			int i = keys.next();

			// there are no other keyphrases to compare
			if (i >= keys.size())
				return;

			// compare the keyphrase i with the other keyphrases j in the list
			int j = i;
			while (true) {
				j++;
				if (j == keys.size())
					j = 0;
				if (j == i)// or Equality.evaluate(kx_i, kx_j)
					break;

				if (i < keys.getOffset(i) && j < keys.getOffset(i))
					continue;

				Keyphrase kx_i = keys.get(i);
				Keyphrase kx_j = keys.get(j);

				/*
				// apply the Equality criteria
				if (Equality.evaluate(kx_i, kx_j)) {
					// if (kxs.inDocument(kx_i, kx_j)) {
					graph.add(i, j, Equality.id);
					//System.out.println("Equality:" + kx_i.getText() + "\t" + kx_j.getText());
				}*/
				
				
				// 2 keyphrases and same language
				if (kx_i.getLanguage() == kx_j.getLanguage()) {
					// language: IT
					if (kx_i.getLanguage() == Language.IT) {
						
						// apply the Abbreviation criteria
						if (eu.fbk.hlt.nlp.criteria.Abbreviation.evaluate(kx_i, kx_j)) {
							// if (kxs.inDocument(kx_i, kx_j)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.Abbreviation.id);
							//System.out.println("Abbreviation:" + kx_i.getText() + "\t" + kx_j.getText());
						}
						/*
						// apply the Acronym criteria
						else if (eu.fbk.hlt.nlp.criteria.it.Acronym.evaluate(kx_i, kx_j)) {
							//if (keys.inDocument(kx_i, kx_j)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.it.Acronym.id);
							//System.out.println("Acronym:" + kx_i.getText() + "\t" + kx_j.getText());
							//}
						}*/
						// apply the Entailment criteria
						else if (eu.fbk.hlt.nlp.criteria.Entailment.evaluate(kx_i, kx_j)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.Entailment.id);
							//System.out.println("Entailment:" + kx_i.getText() + "\t" + kx_j.getText());
						}
						// apply the Modifier Swap criteria
						else if (eu.fbk.hlt.nlp.criteria.ModifierSwap.evaluate(kx_i, kx_j)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.ModifierSwap.id);
							//System.out.println("ModifierSwap:" + kx_i.getText() + "\t" + kx_j.getText());
						}
						// apply the Singular/Plural criteria
						else if (eu.fbk.hlt.nlp.criteria.it.SingularPlural.evaluate(kx_i, kx_j)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.it.SingularPlural.id);
							//System.out.println("SingularPlural:" + kx_i.getText() + "\t" + kx_j.getText());
						}
						// apply the Singular/Plural criteria
						else if (eu.fbk.hlt.nlp.criteria.it.PrepositionalVariant.evaluate(kx_i, kx_j)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.it.PrepositionalVariant.id);
							//System.out.println("PrepositionalVariant:" + kx_i.getText() + "\t" + kx_j.getText());
						}
						// apply the Synonym criteria
						else if (eu.fbk.hlt.nlp.criteria.it.Synonymy.evaluate(kx_i, kx_j, keys)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.it.Synonymy.id);
							//System.out.println("Synonym:" + kx_i.getText() + "\t" + kx_j.getText());
						}
						// apply the Synonym criteria
						else if (eu.fbk.hlt.nlp.criteria.it.Article.evaluate(kx_i, kx_j, keys)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.it.Article.id);
							//System.out.println("Article:" + kx_i.getText() + "\t" + kx_j.getText());
						}
					}
					// language: DE
					else if (kx_i.getLanguage() == Language.DE) {
						
						// apply the Abbreviation criteria
						if (eu.fbk.hlt.nlp.criteria.Abbreviation.evaluate(kx_i, kx_j)) {
							// if (kxs.inDocument(kx_i, kx_j)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.Abbreviation.id);
							//System.out.println("Abbreviation:" + kx_i.getText() + "\t" + kx_j.getText());
						}
						/*
						// apply the Acronym criteria
						else if (eu.fbk.hlt.nlp.criteria.it.Acronym.evaluate(kx_i, kx_j)) {
							//if (keys.inDocument(kx_i, kx_j)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.it.Acronym.id);
							//System.out.println("Acronym:" + kx_i.getText() + "\t" + kx_j.getText());
							//}
						}*/
						// apply the Entailment criteria
						else if (eu.fbk.hlt.nlp.criteria.Entailment.evaluate(kx_i, kx_j)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.Entailment.id);
							//System.out.println("Entailment:" + kx_i.getText() + "\t" + kx_j.getText());
						}
						// apply the Modifier Swap criteria
						else if (eu.fbk.hlt.nlp.criteria.ModifierSwap.evaluate(kx_i, kx_j)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.ModifierSwap.id);
							//System.out.println("ModifierSwap:" + kx_i.getText() + "\t" + kx_j.getText());
						}
						// apply the Singular/Plural criteria
						else if (eu.fbk.hlt.nlp.criteria.de.SingularPlural.evaluate(kx_i, kx_j)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.de.SingularPlural.id);
							//System.out.println("SingularPlural:" + kx_i.getText() + "\t" + kx_j.getText());
						}
						// apply the Singular/Plural criteria
						else if (eu.fbk.hlt.nlp.criteria.de.PrepositionalVariant.evaluate(kx_i, kx_j)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.de.PrepositionalVariant.id);
							//System.out.println("PrepositionalVariant:" + kx_i.getText() + "\t" + kx_j.getText());
						}
						// apply the Synonym criteria
						else if (eu.fbk.hlt.nlp.criteria.de.Article.evaluate(kx_i, kx_j, keys)) {
							graph.add(i, j, eu.fbk.hlt.nlp.criteria.de.Article.id);
							//System.out.println("Article:" + kx_i.getText() + "\t" + kx_j.getText());
						}
						
					}
					// language: EN
					else if (kx_i.getLanguage() == Language.EN) {
						
					}
					
				}
				else {// 2 keyphrases and different language
					if (eu.fbk.hlt.nlp.criteria.Translation.evaluate(kx_i, kx_j)) {
						graph.add(i, j, eu.fbk.hlt.nlp.criteria.Translation.id);
						//System.out.println("Article:" + kx_i.getText() + "\t" + kx_j.getText());
					}
				}
				
			}

		}

	}

}
