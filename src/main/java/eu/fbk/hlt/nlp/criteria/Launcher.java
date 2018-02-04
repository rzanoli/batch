package eu.fbk.hlt.nlp.criteria;

import java.util.Scanner;

import eu.fbk.hlt.nlp.cluster.Keyphrase;

/**
 * This class can be used to test the implemented criteria from
 * the command line.
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
	/*
	public static void main(String[] args) {
		
		/*

		System.out.println("easycriteria v0.2");
		System.out.println("Since: November 2017");
		System.out.println("Major changes of this release compared to v0.1:");
		System.out.println("-acronym: at least 2 characters");
		System.out.println("-apply criteria: given keyphrase1 and keyphrase2, can keyphrase2 be derived from keyphrase1?");
		System.out.println("Decription: an interactive shell to compare 2 keyphrases");
		System.out.println("Implemented rules: 2,3,9,10");
		System.out.println("Usage: keyphrase1|keyphrase2 (e.g., >fondazione bruno kessler|fbk)");

		
		// Init the launcher
		Launcher launcher = Launcher.getInstance();

		String[] str1List = { "FBK in Povo", "FBK", "Fondazione Bruno Kessler in Povo" };

		
		if (1 == 2) {

			// Start time
			final long startTime = System.currentTimeMillis();

			int index = 0;
			for (int i = 0; i <= 1000000; i++) {

				if (index >= 2)
					index = 0;
				else
					index++;
				String str1 = str1List[index];

				String str2 = "Fondazione Bruno Kessler in Povo";
				Keyphrase kx1 = new Keyphrase(str1);
				Keyphrase kx2 = new Keyphrase(str2);

				Abbreviation.evaluate(kx1, kx2);
				Acronym.evaluate(kx1, kx2);
				Entailment.evaluate(kx1, kx2);
				Equality.evaluate(kx1, kx2);
				
			}

			final long endTime = System.currentTimeMillis();

			System.out.println("Total execution time: " + (endTime - startTime) + "[ms]\n");

		}

		else {

			Scanner in; // per la lettura dalla tastiera
			// crea l’oggetto che rappresenta la tastiera 
			in = new Scanner(System.in);

			System.out.print(">");

			while (in.hasNextLine()) {

				String line = in.nextLine();

				if (line.split("\\|").length != 2) {
					if (line.split("\\|").length == 1 && line.split("\\|")[0].equals("exit")) {
						System.out.println("Bye bye!");
						System.exit(0);
					}
					System.out.println("Error: wrong number of parameters!");
				} else {

					String str1 = line.split("\\|")[0];
					String str2 = line.split("\\|")[1];

					Keyphrase kx1 = new Keyphrase(str1);
					Keyphrase kx2 = new Keyphrase(str2);

					System.out.println("  " + str1 + " --> " + str2 + " ?");

					
					// Start time
					final long startTime = System.currentTimeMillis();

					// Fire rules
					//System.out.println("Can '" + str2 + "' be derived from '" + str1 + "'?");
					if (Abbreviation.evaluate(kx1, kx2))
						System.out.println("  YES, by rule:" + Abbreviation.id + " [" + Abbreviation.description + "]");
					else if(Acronym.evaluate(kx1, kx2))
							System.out.println("  YES, by rule:" + Acronym.id + " [" + Acronym.description + "]");
					else if(Entailment.evaluate(kx1, kx2))
						System.out.println("  YES, by rule:" + Entailment.id + " [" + Entailment.description + "]");
					else if(Equality.evaluate(kx1, kx2))
						System.out.println("  YES, by rule:" + Equality.id + " [" + Equality.description + "]");
					else
						System.out.println("  NO, it is not possible!");

					// End time
					final long endTime = System.currentTimeMillis();

					System.out.println("  Total execution time: " + (endTime - startTime) + "[ms]\n");

				}

				System.out.print(">");

			}

		}
		
	} */

}
