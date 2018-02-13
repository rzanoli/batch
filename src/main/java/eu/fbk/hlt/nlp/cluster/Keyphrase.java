package eu.fbk.hlt.nlp.cluster;

import java.util.Arrays;

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
	//private String id;
	private String head;
	private int headPosition;
	private boolean containsAbbreviations;
	private boolean containsPrepositions;

	/**
	 * The constructor
	 * 
	 * @param text
	 *            the text of the keyphrase
	 */
	/*
	@Deprecated
	public Keyphrase(String text) {

		this.text = text;

		if (text != null) {

			String[] tokenizedText = text.split("\\s");
			this.tokens = new ArrayList<Token>(tokenizedText.length);
			for (int i = 0; i < tokenizedText.length; i++)
				this.tokens.add(new Token(tokenizedText[i]));

		}

	}*/
	
	/**
	 * The constructor
	 * 
	 */
	public Keyphrase(int size) {

		this.tokens = new Token[size];
		//this.id = "";
		this.headPosition = -1;
		
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

		return tokens.length;

	}
	
	/**
	 * Get if the keyphrase contains an abbreviation
	 * 
	 * @return true if it contains an abbreviation; false otherwise
	 */
	public boolean containsAbbreviations() {

		return this.containsAbbreviations;

	}
	
	/**
	 * Get if the keyphrase contains a preposition
	 * 
	 * @return true if it contains a preposition; false otherwise
	 */
	public boolean containsPrepositions() {

		return this.containsPrepositions;

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
	 * Add a new token of the keyphrase
	 * 
	 * @param token
	 *            the new token
	 * 
	 */
	public void add(int index, Token token) {

		this.tokens[index] = token;
		if (this.head == null &&
				token.getPoS() != null &&
				token.getPoS().startsWith("S")) {
					this.head = token.getForm();
					this.headPosition = index;
				}
		
		if (token.isAbbreviation())
			containsAbbreviations = true;
		else if (token.getPoS().startsWith("E"))
			containsPrepositions = true;
		//id = id + "_" + token.getForm().toLowerCase();
		//if (token.getText().toLowerCase().equals("allarme") || token.getText().toLowerCase().equals("sec"))
			//System.out.println(id);
		
	}
	
	
	
	/**
	 * Get the tokens in the keyphrase
	 * 
	 * @return the tokens
	 * 
	 */
	public Token[] getTokens() {

		return this.tokens;

	}

	/**
	 * Get the keyphrase id
	 * 
	 * @return the keyphrase id
	 */
	/*
	public String getID() {
	//
		return this.id;
	//
	}*/

	/**
	 * Get the keyphrase text
	 * 
	 * @return the keyphrase text
	 */
	public String getText() {

		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < this.tokens.length; i++) {
			buffer.append(this.tokens[i].getForm());
			if (i < this.tokens.length -1)
				buffer.append(" ");
		}

		return buffer.toString();
		
	}
	
	/**
	 * Get head of the keyphrase 
	 * 
	 * @return the head
	 */
	public String getHead() {

		return this.head;
		
	}
	
	
	/**
	 * Get the head position 
	 * 
	 * @return the head position
	 */
	public int getHeadPosition() {

		return this.headPosition;
		
	}
	
	

	@Override
	public boolean equals(Object obj) {

		// null instanceof Object will always return false
		if (!(obj instanceof Keyphrase))
			return false;
		if (obj == this)
			return true;
		
		//return this.id.equals(((Keyphrase) obj).id);
		//return Equality.evaluate(this, (Keyphrase) obj);
		return Arrays.equals(this.tokens, ((Keyphrase) obj).tokens);

	}

	@Override
	public int hashCode() {

		//return id.hashCode();
		return Arrays.hashCode(tokens);

	}

}
