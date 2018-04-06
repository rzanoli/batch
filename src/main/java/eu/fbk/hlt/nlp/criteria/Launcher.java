package eu.fbk.hlt.nlp.criteria;

import java.util.Scanner;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Keyphrases;
import eu.fbk.hlt.nlp.cluster.Language;
import eu.fbk.hlt.nlp.cluster.Token;
import eu.fbk.hlt.nlp.criteria.Abbreviation;
import eu.fbk.hlt.nlp.criteria.Acronym;
import eu.fbk.hlt.nlp.criteria.Entailment;
import eu.fbk.hlt.nlp.criteria.ModifierSwap;
import eu.fbk.hlt.nlp.criteria.it.PrepositionalVariant;
import eu.fbk.hlt.nlp.criteria.it.SingularPlural;
import eu.fbk.hlt.nlp.criteria.it.Synonymy;

/**
 * This class can be used to test the implemented criteria from the command
 * line.
 * 
 * @author rzanoli
 *
 */
public class Launcher {

	private static Launcher instance;

	/**
	 * Create a rules engine
	 */
	private Launcher() {

	}

	/**
	 * Get an instance of the Launcher
	 * 
	 * @return a Launcher instance
	 * 
	 */
	public static Launcher getInstance() {
		if (instance == null) {
			instance = new Launcher();
		}

		return instance;
	}

	/**
	 * Entrance point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("easycriteria v0.2");
		System.out.println("Since: November 2017");
		System.out.println("Major changes of this release compared to v0.1:");
		System.out.println("-acronym: at least 2 characters");
		System.out.println(
				"-apply criteria: given keyphrase1 and keyphrase2, can keyphrase2 be derived from keyphrase1?");
		System.out.println("Decription: an interactive shell to compare 2 keyphrases");
		System.out.println("Implemented rules: 2,3,4,7,9,10,11,12");
		System.out.println("Usage: keyphrase1#keyphrase2 (e.g., >Fondazione|SPN|fondazione Bruno|SPN|Bruno Kessler|SPN|kessler#FBK|SPN|FBK)");

		Scanner in = null; // per la lettura dalla tastiera
		
		try {
		
			// load synonyms used by one of the criteria
			@SuppressWarnings("unused")
			Keyphrases keyphrases = new Keyphrases();
			
			// crea l’oggetto che rappresenta la tastiera
			in = new Scanner(System.in);
	
			System.out.print(">");
	
			while (in.hasNextLine()) {
	
				String line = in.nextLine();
	
				if (line.split("#").length != 2) {
					if (line.split("#").length == 1 && line.split("#")[0].equals("exit")) {
						System.out.println("Bye bye!");
						System.exit(0);
					}
					System.out.println("Error: wrong number of parameters!");
				} else {
	
					String str1 = line.split("#")[0];
					String str2 = line.split("#")[1];
					
					String[] str1Tokens = str1.split(" ");
					String[] str2Tokens = str2.split(" ");
	
					Keyphrase key1 = new Keyphrase(str1Tokens.length, Language.VALUE.IT);
					for (int i = 0; i < str1Tokens.length; i++) {
						String form = str1Tokens[i].split("\\|")[0];
						String PoS = str1Tokens[i].split("\\|")[1];
						String lemma = str1Tokens[i].split("\\|")[2];
						String wordnetPoS = Language.GET_WORDNET_POS(PoS, Language.VALUE.IT);
						Token token = new Token(form, PoS, lemma, wordnetPoS);
						key1.add(i, token);
					}
					
					Keyphrase key2 = new Keyphrase(str2Tokens.length, Language.VALUE.IT);
					for (int i = 0; i < str2Tokens.length; i++) {
						String form = str2Tokens[i].split("\\|")[0];
						String PoS = str2Tokens[i].split("\\|")[1];
						String lemma = str2Tokens[i].split("\\|")[2];
						String wordnetPoS = Language.GET_WORDNET_POS(PoS, Language.VALUE.IT);
						Token token = new Token(form, PoS, lemma, wordnetPoS);
						key2.add(i, token);
					}
					
					System.out.println("  " + key1.getText() + " --> " + key2.getText() + " ?");
	
					// Start time
					final long startTime = System.currentTimeMillis();
	
					// Fire rules
					// System.out.println("Can '" + str2 + "' be derived from '" + str1 + "'?");
					if (Abbreviation.evaluate(key1, key2))
						System.out.println("  YES, by rule:" + Abbreviation.id + " [" + Abbreviation.description + "]");
					else if (Acronym.evaluate(key1, key2))
						System.out.println("  YES, by rule:" + Acronym.id + " [" + Acronym.description + "]");
					else if (Entailment.evaluate(key1, key2))
						System.out.println("  YES, by rule:" + Entailment.id + " [" + Entailment.description + "]");
					else if (ModifierSwap.evaluate(key1, key2))
						System.out.println("  YES, by rule:" + ModifierSwap.id + " [" + ModifierSwap.description + "]");
					else if (SingularPlural.evaluate(key1, key2))
						System.out.println("  YES, by rule:" + SingularPlural.id + " [" + SingularPlural.description + "]");
					else if (Synonymy.evaluate(key1, key2, keyphrases))
						System.out.println("  YES, by rule:" + Synonymy.id + " [" + Synonymy.description + "]");
					else if (PrepositionalVariant.evaluate(key1, key2))
						System.out.println("  YES, by rule:" + PrepositionalVariant.id + " [" + PrepositionalVariant.description + "]");
					
					else
						System.out.println("  NO, it is not possible!");
	
					// End time
					final long endTime = System.currentTimeMillis();
	
					System.out.println("  Total execution time: " + (endTime - startTime) + "[ms]\n");
	
				}
	
				System.out.print(">");
	
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null)
				in.close();
		}

	}

}
