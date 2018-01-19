package eu.fbk.hlt.nlp.gcluster;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import eu.fbk.hlt.nlp.criteria.Abbreviation;
import eu.fbk.hlt.nlp.criteria.Acronym;
import eu.fbk.hlt.nlp.criteria.Entailment;
import eu.fbk.hlt.nlp.criteria.Equality;

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
	private Keyphrases kxs;
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
	public Comparator(AtomicBoolean interrupted, Keyphrases kxs, Graph graph) {

		this.interrupted = interrupted;
		this.kxs = kxs;
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
			int i = kxs.next();

			// there are no other keyphrases to compare
			if (i >= kxs.size())
				return;

			// compare the keyphrase i with the other keyphrases j in the list
			int j = i;
			while (true) {
				j++;
				if (j == kxs.size())
					j = 0;
				if (j == i)// or Equality.evaluate(kx_i, kx_j)
					break;

				Keyphrase kx_i = kxs.get(i);
				Keyphrase kx_j = kxs.get(j);
				// apply the Abbreviation criteria
				if (Abbreviation.evaluate(kx_i, kx_j)) {
					graph.add(kx_i.getId(), kx_j.getId(), Abbreviation.id);
				}
				// apply the Acronym criteria
				else if (Acronym.evaluate(kx_i, kx_j)) {
					graph.add(kx_i.getId(), kx_j.getId(), Acronym.id);
				}
				// apply the Entailment criteria
				else if (Entailment.evaluate(kx_i, kx_j)) {
					graph.add(kx_i.getId(), kx_j.getId(), Entailment.id);
				}

			}

		}

	}

}
