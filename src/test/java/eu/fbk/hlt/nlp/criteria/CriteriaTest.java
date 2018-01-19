package eu.fbk.hlt.nlp.criteria;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.fbk.hlt.nlp.gcluster.Keyphrase;

public class CriteriaTest {

	@Test
	public void abbreviation1Test() {
		final String str2 = "b. magnini";
		final String str1 = "bernardo magnini";
		Keyphrase kx1 = new Keyphrase(str1);
		Keyphrase kx2 = new Keyphrase(str2);
		boolean result = Abbreviation.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

	@Test
	public void abbreviation2Test() {
		final String str2 = "b. magnini";
		final String str1 = "bernardo lorenzi";
		Keyphrase kx1 = new Keyphrase(str1);
		Keyphrase kx2 = new Keyphrase(str2);
		boolean result = Abbreviation.evaluate(kx1, kx2);
		final boolean expected = false;
		assertEquals(expected, result);
	}

	@Test
	public void abbreviation3Test() {
		final String str2 = "b. magnini e lello f.";
		final String str1 = "bernardo magnini e lello filippi";
		Keyphrase kx1 = new Keyphrase(str1);
		Keyphrase kx2 = new Keyphrase(str2);
		boolean result = Abbreviation.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

	@Test
	public void acronym1Test() {
		final String str2 = "fbk";
		final String str1 = "fondazione bruno kessler";
		Keyphrase kx1 = new Keyphrase(str1);
		Keyphrase kx2 = new Keyphrase(str2);
		boolean result = Acronym.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

	@Test
	public void acronym2Test() {
		final String str2 = "f.b.k.";
		final String str1 = "fondazione bruno kessler";
		Keyphrase kx1 = new Keyphrase(str1);
		Keyphrase kx2 = new Keyphrase(str2);
		boolean result = Acronym.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

	@Test
	public void acronym3Test() {
		final String str2 = "f";
		final String str1 = "fondazione";
		Keyphrase kx1 = new Keyphrase(str1);
		Keyphrase kx2 = new Keyphrase(str2);
		boolean result = Acronym.evaluate(kx1, kx2);
		final boolean expected = false;
		assertEquals(expected, result);
	}

	@Test
	public void entailment1Test() {
		final String str2 = "fondazione";
		final String str1 = "fondazione kessler";
		Keyphrase kx1 = new Keyphrase(str1);
		Keyphrase kx2 = new Keyphrase(str2);
		boolean result = Entailment.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

	@Test
	public void equality1Test() {
		final String str2 = "ufficio italiano del consorzio";
		final String str1 = "ufficio italiano del consorzio";
		Keyphrase kx1 = new Keyphrase(str1);
		Keyphrase kx2 = new Keyphrase(str2);
		boolean result = Equality.evaluate(kx1, kx2);
		final boolean expected = true;
		assertEquals(expected, result);
	}

}