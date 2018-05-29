package eu.fbk.hlt.nlp.criteria;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Language;
import eu.fbk.hlt.nlp.cluster.Token;
import eu.fbk.hlt.nlp.criteria.de.Article;
import eu.fbk.hlt.nlp.criteria.de.PrepositionalVariant;
import eu.fbk.hlt.nlp.criteria.de.SingularPlural;


public class CriteriaDETest {

	@Test
	public void prepositionalVariant1Test() {
		// final String str1 = "FBK von Trient";
		Token str2T1 = new Token("FBK", "NN", "<unknown>", "");
		Token str2T2 = new Token("von", "APPR", "von", "");
		Token str2T3 = new Token("Trient", "NE", "Trient", "n");
		Keyphrase kx2 = new Keyphrase(3, Language.DE);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		kx2.add(2, str2T3);
		// final String str2 = "FBK in Trient";
		Token str1T1 = new Token("FBK", "NN", "<unknown>", "");
		Token str1T2 = new Token("in", "APPR", "di", "");
		Token str1T3 = new Token("Trient", "NE", "Trient", "");
		Keyphrase kx1 = new Keyphrase(3, Language.DE);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		boolean result = PrepositionalVariant.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}
	
	@Test
	public void prepositionalVariant2Test() {
		// final String str1 = "Regeln von Spiel";
		Token str2T1 = new Token("Regeln", "NN", "Regel", "");
		Token str2T2 = new Token("von", "APPR", "von", "");
		Token str2T3 = new Token("Spiel", "NN", "Spiel", "n");
		Keyphrase kx2 = new Keyphrase(3, Language.DE);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		kx2.add(2, str2T3);
		// final String str2 = "Regeln vom Spiel";
		Token str1T1 = new Token("Regeln", "NN", "Regel", "");
		Token str1T2 = new Token("vom", "APPRART", "von", "");
		Token str1T3 = new Token("Spiel", "NN", "Spiel", "");
		Keyphrase kx1 = new Keyphrase(3, Language.DE);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		boolean result = PrepositionalVariant.evaluate(kx1, kx2);
		final boolean expected = false;
		assertEquals(expected, result);
	}
	
	@Test
	public void article1Test() {
		// final String str1 = "Regeln von Spiel";
		Token str2T1 = new Token("Regeln", "NN", "Regel", "");
		Token str2T2 = new Token("von", "APPR", "von", "");
		Token str2T3 = new Token("Spiel", "NN", "Spiel", "n");
		Keyphrase kx2 = new Keyphrase(3, Language.DE);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		kx2.add(2, str2T3);
		// final String str2 = "Regeln vom Spiel";
		Token str1T1 = new Token("Regeln", "NN", "Regel", "");
		Token str1T2 = new Token("vom", "APPRART", "von", "");
		Token str1T3 = new Token("Spiel", "NN", "Spiel", "");
		Keyphrase kx1 = new Keyphrase(3, Language.DE);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		boolean result = Article.evaluate(kx1, kx2);
		final boolean expected = false;
		assertEquals(expected, result);
	}
	
	@Test
	public void singularPlural1Test() {
		// final String str1 = "Mensa der Universität";
		Token str2T1 = new Token("Mensa", "NN", "Mensa", "");
		Token str2T2 = new Token("der", "ART", "die", "");
		Token str2T3 = new Token("Universität", "NN", "Universität", "");
		Keyphrase kx2 = new Keyphrase(3, Language.DE);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		kx2.add(2, str2T3);
		// final String str2 = "Mensa der Universitäten";
		Token str1T1 = new Token("Mensa", "NN", "FBK", "n");
		Token str1T2 = new Token("der", "ART", "die", "");
		Token str1T3 = new Token("Universitäten", "NN", "Universität", "");
		Keyphrase kx1 = new Keyphrase(3, Language.DE);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		boolean result = SingularPlural.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}
	
	@Test
	public void entailment1Test() {
		// final String str2 = "Büro für die Energiewende";
		Token str2T1 = new Token("Büro", "NN", "Büro", "");
		Token str2T2 = new Token("für", "APPR", "für", "");
		Token str2T3 = new Token("die", "ART", "die", "");
		Token str2T4 = new Token("Energiewende", "NN", "Energiewende", "");
		Keyphrase kx2 = new Keyphrase(4, Language.DE);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		kx2.add(2, str2T3);
		kx2.add(3, str2T4);
		// final String str1 = "Deutsches Büro für die Energiewende";
		Token str1T1 = new Token("Deutsches", "NN", "deutsche", "");
		Token str1T2 = new Token("Büro", "NN", "Büro", "");
		Token str1T3 = new Token("für", "APPR", "für", "");
		Token str1T4 = new Token("die", "ART", "die", "");
		Token str1T5 = new Token("Energiewende", "NN", "Energiewende", "");
		Keyphrase kx1 = new Keyphrase(5, Language.DE);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		kx1.add(3, str1T4);
		kx1.add(4, str1T5);
		boolean result = Entailment.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}
	
	//@Test
	public void modifierSwap1Test() {
		// final String str2 = "2017 französische Wahlen";
		Token str2T1 = new Token("2017", "CARD", "2017", "");
		Token str2T2 = new Token("französische", "ADJA", "französisch", "");
		Token str2T3 = new Token("Wahlen", "NN", "Wahl", "");
		Keyphrase kx2 = new Keyphrase(3, Language.DE);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		kx2.add(2, str2T3);
		// final String str1 = "französische Wahlen 2017";
		Token str1T1 = new Token("französische", "ADJA", "französisch", "");
		Token str1T2 = new Token("Wahlen", "NN", "Wahl", "");
		Token str1T3 = new Token("2017", "CARD", "2017", "");
		Keyphrase kx1 = new Keyphrase(3, Language.DE);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		boolean result = ModifierSwap.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

}