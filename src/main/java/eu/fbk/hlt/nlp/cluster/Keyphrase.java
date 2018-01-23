package eu.fbk.hlt.nlp.cluster;

import java.util.Arrays;

import eu.fbk.hlt.nlp.criteria.Equality;

/**
 * KeyPhrases are expressions contained in a document which help understand and
 * summarize the content of that document. We assume the following properties
 * for a keyphrase k to be valid for a certain document d:
 * 
 * -k is a continuous sequence of tokens in d (e.g. United States of America is
 * a keyphrase composed of four tokens)
 * 
 * -k is considered as relevant information for one or more topics expressed in
 * d
 * 
 * -the tokens composing k can not be used for another keyphrase in d (i.e. we
 * do not allow overlapping or nesting of keyphrases)
 * 
 * -k is a noun phrase (i.e. its semantic head is a noun) without determiner
 * (e.g. “Fondazione Kessler”, but not “la Fondazione Kessler”)
 * 
 * -k can be a nominalization of a verbal or of an adjectival phrase in d (e. g.
 * Trump’s election derived from “Trump was elected…”)
 * 
 * @author rzanoli
 *
 */
public class Keyphrase {

	//private int id;
	private Token[] tokens;
	private String text;

	/**
	 * The constructor
	 * 
	 * @param text
	 *            the text of the keyphrase
	 */
	public Keyphrase(String text) {

		this.text = text;

		if (text != null) {

			String[] tokenizedText = text.split("\\s");
			this.tokens = new Token[tokenizedText.length];
			for (int i = 0; i < tokenizedText.length; i++)
				this.tokens[i] = new Token(tokenizedText[i]);

		}

	}

	/**
	 * Set the keyphrase id
	 * 
	 * @param id
	 *            the keyphrase id
	 */
	//public void setIkD(int id) {
	//
	//	this.id = id;
	//
	//}

	/**
	 * Get the keyphrase length calculated as a number of its tokens.
	 * 
	 * @return the keyphrase length
	 */
	public int length() {

		if (this.text == null)
			return -1;

		return tokens.length;

	}

	/**
	 * get the token i of the keyphrase
	 * 
	 * @param i
	 *            the token position in the keyphrase
	 * 
	 * @return the token i
	 */
	public Token get(int i) {

		if (i < 0 || i >= tokens.length)
			return null;

		return tokens[i];

	}

	/**
	 * Get the keyphrase id
	 * 
	 * @return the keyphrase id
	 */
	//public int getID() {
	//
	//	return this.id;
	//
	//}

	/**
	 * Get the keyphrase text
	 * 
	 * @return the keyphrase text
	 */
	public String getText() {

		return this.text;

	}

	@Override
	public boolean equals(Object obj) {

		// null instanceof Object will always return false
		if (!(obj instanceof Keyphrase))
			return false;
		if (obj == this)
			return true;
		return this.text.equals(((Keyphrase) obj).text);
		//return Equality.evaluate(this, (Keyphrase) obj);

	}

	@Override
	public int hashCode() {

		return text.hashCode();
		//return Arrays.hashCode(tokens);

	}

}
