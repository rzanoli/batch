package eu.fbk.hlt.nlp.cluster;

import java.util.concurrent.atomic.AtomicBoolean;

import eu.fbk.hlt.nlp.criteria.Abbreviation;
import eu.fbk.hlt.nlp.criteria.Acronym;
import eu.fbk.hlt.nlp.criteria.Entailment;
import eu.fbk.hlt.nlp.criteria.ModifierSwap;
import eu.fbk.hlt.nlp.criteria.PrepositionalVariant;
import eu.fbk.hlt.nlp.criteria.SingularPlural;
import eu.fbk.hlt.nlp.criteria.Synonym;
import eu.fbk.hlt.nlp.criteria.Article;

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

				
				// apply the Abbreviation criteria
				if (Abbreviation.evaluate(kx_i, kx_j)) {
					// if (kxs.inDocument(kx_i, kx_j)) {
					graph.add(i, j, Abbreviation.id);
					//System.out.println("Abbreviation:" + kx_i.getText() + "\t" + kx_j.getText());
				}
				// apply the Acronym criteria
				else if (Acronym.evaluate(kx_i, kx_j)) {
					// if (kxs.inDocument(kx_i, kx_j)) {
					graph.add(i, j, Acronym.id);
					//System.out.println("Acronym:" + kx_i.getText() + "\t" + kx_j.getText());
					// }
				}
				// apply the Entailment criteria
				else if (Entailment.evaluate(kx_i, kx_j)) {
					graph.add(i, j, Entailment.id);
					//System.out.println("Entailment:" + kx_i.getText() + "\t" + kx_j.getText());
				}
				// apply the Modifier Swap criteria
				else if (ModifierSwap.evaluate(kx_i, kx_j)) {
					graph.add(i, j, ModifierSwap.id);
					//System.out.println("ModifierSwap:" + kx_i.getText() + "\t" + kx_j.getText());
				}
				// apply the Singular/Plural criteria
				else if (SingularPlural.evaluate(kx_i, kx_j)) {
					graph.add(i, j, SingularPlural.id);
					System.out.println("SingularPlural:" + kx_i.getText() + "\t" + kx_j.getText());
				}
				// apply the Singular/Plural criteria
				else if (PrepositionalVariant.evaluate(kx_i, kx_j)) {
					graph.add(i, j, PrepositionalVariant.id);
					//System.out.println("PrepositionalVariant:" + kx_i.getText() + "\t" + kx_j.getText());
				}
				// apply the Synonym criteria
				else if (Synonym.evaluate(kx_i, kx_j, keys)) {
					graph.add(i, j, Synonym.id);
					//System.out.println("Synonym:" + kx_i.getText() + "\t" + kx_j.getText());
				}
				// apply the Synonym criteria
				else if (Article.evaluate(kx_i, kx_j, keys)) {
					graph.add(i, j, Article.id);
					//System.out.println("Article:" + kx_i.getText() + "\t" + kx_j.getText());
				}
			}

		}

	}

}
