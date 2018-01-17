package eu.fbk.hlt.nlp.gcluster;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import eu.fbk.hlt.nlp.criteria.Abbreviation;
import eu.fbk.hlt.nlp.criteria.Acronym;
import eu.fbk.hlt.nlp.criteria.Entailment;
import eu.fbk.hlt.nlp.criteria.Equality;

public class Comparator implements Runnable {

	private AtomicBoolean interrupted;
	private Keyphrases kxs;
	private Graph graph;
	private int id;
	
	//private static final Logger LOGGER = Logger.getLogger(Comparator.class.getName());
	
	/**
	 * The constructor
	 * 
	 * @param configFileName
	 *            the cfg file
	 * @param accountTable
	 *            the table containing the accounts to monitor and yhat is in memory
	 * @param interrupted
	 *            'true' to stop the thread
	 * 
	 */
	public Comparator(int id, AtomicBoolean interrupted, Keyphrases kxs, Graph graph) {

		this.id = id;
		this.interrupted = interrupted;
		this.kxs = kxs;
		this.graph = graph;

	}

	/*
	public void run() {

		while (!interrupted.get()) {

			Keyphrase[] keyphrase_i_j = kxs.next();
			if (keyphrase_i_j == null)
				return;
			
			Keyphrase kx_i = keyphrase_i_j[0];
			Keyphrase kx_j = keyphrase_i_j[1];
			
			//System.out.println("compare:" + id + "\t" + kx_i.getId() + " " + kx_j.getId());
			
			if (Abbreviation.evaluate(kx_i, kx_j))
				graph.add(kx_i.getId(), kx_j.getId());
			else if(Acronym.evaluate(kx_i, kx_j))
				graph.add(kx_i.getId(), kx_j.getId());
			else if(Entailment.evaluate(kx_i, kx_j))
				graph.add(kx_i.getId(), kx_j.getId());
			
		}

	}*/
	
	public void run() {

		while (!interrupted.get()) {

			int i = kxs.increaseI();
			
			//System.out.println("====" + i);
			
			if (i >= kxs.size())
				return;
			
			int j = i;
			while (true) {
				
				j++;
				
				if (j == kxs.size())
					j = 0;
				
				if (j == i)
					break;
				
				Keyphrase kx_i = kxs.get(i);
				Keyphrase kx_j = kxs.get(j);
				
				//System.out.println("compare:" + id + "\t" + kx_i.getId() + " " + kx_j.getId());
				
				if (Abbreviation.evaluate(kx_i, kx_j))
					graph.add(kx_i.getId(), kx_j.getId(), Abbreviation.id);
				else if(Acronym.evaluate(kx_i, kx_j))
					graph.add(kx_i.getId(), kx_j.getId(), Acronym.id);
				else if(Entailment.evaluate(kx_i, kx_j))
					graph.add(kx_i.getId(), kx_j.getId(), Entailment.id);
			
			}
			
		}

	}
	

}
