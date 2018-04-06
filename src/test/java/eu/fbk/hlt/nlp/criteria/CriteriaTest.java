package eu.fbk.hlt.nlp.criteria;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.fbk.hlt.nlp.cluster.Keyphrase;
import eu.fbk.hlt.nlp.cluster.Language;
import eu.fbk.hlt.nlp.cluster.Token;
import eu.fbk.hlt.nlp.criteria.Abbreviation;
import eu.fbk.hlt.nlp.criteria.Acronym;
import eu.fbk.hlt.nlp.criteria.Entailment;
import eu.fbk.hlt.nlp.criteria.ModifierSwap;
import eu.fbk.hlt.nlp.criteria.it.SingularPlural;

public class CriteriaTest {

	@Test
	public void abbreviation1Test() {
		// final String str2 = "B. Magnini";
		Token str2T1 = new Token("B.", "SPN", "B.", "n");
		Token str2T2 = new Token("Magnini", "SPN", "Magnini", "n");
		Keyphrase kx2 = new Keyphrase(2, Language.VALUE.IT);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		// final String str1 = "bernardo magnini";
		Token str1T1 = new Token("Bernardo", "SPN", "Bernardo", "n");
		Token str1T2 = new Token("Magnini", "SPN", "Magnini", "n");
		Keyphrase kx1 = new Keyphrase(2, Language.VALUE.IT);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		boolean result = Abbreviation.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

	@Test
	public void abbreviation2Test() {
		// final String str2 = "B. Magnini";
		Token str2T1 = new Token("b.", "SPN", "b.", "n");
		Token str2T2 = new Token("Magnini", "SPN", "Magnini", "n");
		Keyphrase kx2 = new Keyphrase(2, Language.VALUE.IT);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		// final String str1 = "bernardo magnini";
		Token str1T1 = new Token("Bernardo", "SPN", "Bernardo", "n");
		Token str1T2 = new Token("Magnini", "SPN", "Magnini", "n");
		Keyphrase kx1 = new Keyphrase(2, Language.VALUE.IT);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		boolean result = Abbreviation.evaluate(kx2, kx1);
		final boolean expected = false;
		assertEquals(expected, result);
	}

	@Test
	public void abbreviation3Test() {
		// final String str2 = "b. magnini e lello f.";
		Token str2T1 = new Token("b.", "SPN", "b.", "n");
		Token str2T2 = new Token("magnini", "SPN", "magnini", "n");
		Token str2T3 = new Token("e", "C", "e", "");
		Token str2T4 = new Token("lello", "SPN", "lello", "n");
		Token str2T5 = new Token("f.", "SPN", "f.", "n");
		Keyphrase kx2 = new Keyphrase(5, Language.VALUE.IT);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		kx2.add(2, str2T3);
		kx2.add(3, str2T4);
		kx2.add(4, str2T5);
		// final String str1 = "bernardo magnini e lello filippi";
		Token str1T1 = new Token("bernardo", "SPN", "bernardo", "n");
		Token str1T2 = new Token("magnini", "SPN", "magnini", "n");
		Token str1T3 = new Token("e", "C", "e", "");
		Token str1T4 = new Token("lello", "SPN", "lello", "n");
		Token str1T5 = new Token("filippi", "SPN", "filippi", "n");
		Keyphrase kx1 = new Keyphrase(5, Language.VALUE.IT);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		kx1.add(3, str1T4);
		kx1.add(4, str1T5);
		// final String str1 =
		boolean result = Abbreviation.evaluate(kx1, kx2);
		final boolean expected = false;
		assertEquals(expected, result);
	}

	@Test
	public void abbreviation4Test() {
		// final String str2 = "Shots.it";
		Token str2T1 = new Token("S.", "SPN", "S.", "n");
		Keyphrase kx2 = new Keyphrase(1, Language.VALUE.IT);
		kx2.add(0, str2T1);
		// final String str1 = "Silverman";
		Token str1T1 = new Token("Silverman", "SPN", "Silverman", "n");
		Keyphrase kx1 = new Keyphrase(1, Language.VALUE.IT);
		kx1.add(0, str1T1);
		boolean result = Abbreviation.evaluate(kx1, kx2);
		final boolean expected = false;
		assertEquals(expected, result);
	}

	@Test
	public void acronym1Test() {
		// final String str2 = "fbk";
		Token str2T1 = new Token("FBK", "SPN", "FBK", "n");
		Keyphrase kx2 = new Keyphrase(1, Language.VALUE.IT);
		kx2.add(0, str2T1);
		// final String str1 = "fondazione bruno kessler";
		Token str1T1 = new Token("Fondazione", "SPN", "Fondazione", "n");
		Token str1T2 = new Token("Bruno", "SPN", "Bruno", "n");
		Token str1T3 = new Token("Kessler", "SPN", "Kessler", "n");
		Keyphrase kx1 = new Keyphrase(3, Language.VALUE.IT);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		boolean result = Acronym.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

	@Test
	public void acronym2Test() {
		// final String str2 = "f.b.k.";
		Token str2T1 = new Token("f.b.k.", "SPN", "f.b.k.", "n");
		Keyphrase kx2 = new Keyphrase(1, Language.VALUE.IT);
		kx2.add(0, str2T1);
		// final String str1 = "fondazione bruno kessler";
		Token str1T1 = new Token("fondazione", "SPN", "fondazione", "n");
		Token str1T2 = new Token("bruno", "SPN", "bruno", "n");
		Token str1T3 = new Token("kessler", "C", "kessler", "n");
		Keyphrase kx1 = new Keyphrase(3, Language.VALUE.IT);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		boolean result = Acronym.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

	@Test
	public void acronym3Test() {
		// final String str2 = "f";
		Token str2T1 = new Token("f", "SPN", "f", "n");
		Keyphrase kx2 = new Keyphrase(1, Language.VALUE.IT);
		kx2.add(0, str2T1);
		// final String str1 = "fondazione";
		Token str1T1 = new Token("fondazione", "SPN", "fondazione", "n");
		Keyphrase kx1 = new Keyphrase(1, Language.VALUE.IT);
		kx1.add(0, str1T1);
		boolean result = Acronym.evaluate(kx1, kx2);
		final boolean expected = false;
		assertEquals(expected, result);
	}

	@Test
	public void acronym4Test() {
		// final String str2 = "fem";
		Token str2T1 = new Token("FEM", "SPN", "FEM", "n");
		Keyphrase kx2 = new Keyphrase(1, Language.VALUE.IT);
		kx2.add(0, str2T1);
		// final String str1 = "fondazione edmund mach";
		Token str1T1 = new Token("Fondazione", "SPN", "Fondazione", "n");
		Token str1T2 = new Token("Edmund", "SPN", "Edmund", "n");
		Token str1T3 = new Token("Mach", "C", "Mach", "n");
		Keyphrase kx1 = new Keyphrase(3, Language.VALUE.IT);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		boolean result = Acronym.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

	@Test
	public void entailment1Test() {
		// final String str2 = "fondazione";
		Token str2T1 = new Token("fondazione", "SPN", "fondazione", "n");
		Keyphrase kx2 = new Keyphrase(1, Language.VALUE.IT);
		kx2.add(0, str2T1);
		// final String str1 = "fondazione kessler";
		Token str1T1 = new Token("fondazione", "SPN", "fondazione", "n");
		Token str1T2 = new Token("kessler", "SPN", "kessler", "n");
		Keyphrase kx1 = new Keyphrase(2, Language.VALUE.IT);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		boolean result = Entailment.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

	/*
	@Test
	public void equality1Test() {
		// final String str2 = "ufficio italiano del consorzio";
		Token str2T1 = new Token("ufficio", "SS", "ufficio");
		Token str2T2 = new Token("italiano", "AS", "italiano");
		Token str2T3 = new Token("del", "ES", "del");
		Token str2T4 = new Token("consorzio", "SS", "consorzio");
		Keyphrase kx2 = new Keyphrase(4);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		kx2.add(2, str2T3);
		kx2.add(3, str2T4);
		// final String str1 = "ufficio italiano del consorzio";
		Token str1T1 = new Token("ufficio", "SS", "ufficio");
		Token str1T2 = new Token("italiano", "AS", "italiano");
		Token str1T3 = new Token("del", "ES", "del");
		Token str1T4 = new Token("consorzio", "SS", "consorzio");
		Keyphrase kx1 = new Keyphrase(4);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		kx1.add(3, str1T4);
		boolean result = Capitalization.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}*/

	@Test
	public void modifierSwap1Test() {
		// final String str2 = "elezioni 2017 francesi";
		Token str2T1 = new Token("elezioni", "SS", "elezione", "n");
		Token str2T2 = new Token("2017", "N", "2017", "");
		Token str2T3 = new Token("francesi", "ES", "francese", "");
		Keyphrase kx2 = new Keyphrase(3, Language.VALUE.IT);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		kx2.add(2, str2T3);
		// final String str1 = "elezioni francesi 2017";
		Token str1T1 = new Token("elezioni", "SS", "elezione", "n");
		Token str1T2 = new Token("francesi", "ES", "francese", "");
		Token str1T3 = new Token("2017", "N", "2017", "");
		Keyphrase kx1 = new Keyphrase(3, Language.VALUE.IT);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		boolean result = ModifierSwap.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

	@Test
	public void modifierSwap2Test() {
		// final String str2 = "elezioni francesi 2017";
		Token str2T1 = new Token("elezioni", "SS", "elezione", "n");
		Token str2T2 = new Token("francesi", "ES", "francese", "");
		Token str2T3 = new Token("2017", "N", "2017", "");
		Keyphrase kx2 = new Keyphrase(3, Language.VALUE.IT);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		kx2.add(2, str2T3);
		// final String str1 = "elezioni 2017 francesi";
		Token str1T1 = new Token("elezioni", "SS", "elezione", "n");
		Token str1T2 = new Token("2017", "N", "2017", "");
		Token str1T3 = new Token("francesi", "ES", "francese", "");
		Keyphrase kx1 = new Keyphrase(3, Language.VALUE.IT);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		kx1.add(2, str1T3);
		boolean result = ModifierSwap.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

	@Test
	public void singularPlural1Test() {
		// final String str2 = "elezione francese";
		Token str2T1 = new Token("elezione", "SS", "elezione", "n");
		Token str2T2 = new Token("francese", "ES", "francese", "");
		Keyphrase kx2 = new Keyphrase(2, Language.VALUE.IT);
		kx2.add(0, str2T1);
		kx2.add(1, str2T2);
		// final String str1 = "elezioni francesi";
		Token str1T1 = new Token("elezioni", "SP", "elezione", "n");
		Token str1T2 = new Token("francesi", "EP", "francese", "");
		Keyphrase kx1 = new Keyphrase(2, Language.VALUE.IT);
		kx1.add(0, str1T1);
		kx1.add(1, str1T2);
		boolean result = SingularPlural.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

}