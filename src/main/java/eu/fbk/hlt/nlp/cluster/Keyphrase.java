package eu.fbk.hlt.nlp.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
	// the keyphrase tokens
	private Token[] tokens;
	//private String id;
	// the keyphrase text
	private String text;
	// the head of the keyphrase
	private String head;
	// if the head can be set ot it already exists
	private boolean writableHead;
	// the token position of the head in the keyphrase
	private int headPosition;
	// number of abbreviations in th ekeyphrase
	private int abbreviationsOccurrences;
	// number of prepositions in the keyphrase
	private int prepositionsOccurrences;
	// the keyphrase language
	private Language language;
	// babelnet synsets
	private List<String> babelnetSynsets;

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
	public Keyphrase(int size, Language language) {

		this.tokens = new Token[size];
		//this.id = "";
		this.headPosition = -1;
		this.writableHead = true;
		this.language = language;
		this.babelnetSynsets = new ArrayList<String>();
		
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
	 * Get the language of the keyphrase
	 * 
	 * @return the language
	 */
	public Language getLanguage() {
		
		return this.language;
		
	}
	
	/**
	 * Get the number of abbreviations in the keyphrase
	 * 
	 * @return the number of abbreviations
	 */
	public int abbreviations() {

		return this.abbreviationsOccurrences;

	}
	
	/**
	 * Get the number of prepositions in the keyphrase
	 * 
	 * @return the number of prepositions
	 */
	public int prepositions() {

		return this.prepositionsOccurrences;

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
		
		// set the head of the keyphrase
		//
		// Italian language: the first noun of the keyphrase is the head
		if (this.language == Language.IT) {
			if (this.writableHead == true) {
				if (token.getPoS() != null &&
					token.getPoS().startsWith("S")) {
					this.head = token.getForm();
					//System.out.println(head);
					this.headPosition = index;
					this.writableHead = false;
				}
			}
		}
		// German language: the last noun of the keyphrase before the first preposition if present
		// otherwise the last noun is the head;
		else if(this.language == Language.DE) {
			if (this.writableHead == true) {
				if (token.getPoS() != null) {
					if (token.getPoS().startsWith("AP")) { //AP: preposition
						this.writableHead = false;
					}
					else if (token.getPoS().startsWith("N")) { //N: noun
						this.head = token.getForm();
						//System.out.println(head);
						this.headPosition = index;
					}
				}
			}
		}
		
		if (token.isAbbreviation())
			this.abbreviationsOccurrences++;
		
		else if (token.getPoS().startsWith("E"))
			this.prepositionsOccurrences++;
		//id = id + "_" + token.getForm().toLowerCase();
		//if (token.getText().toLowerCase().equals("allarme") || token.getText().toLowerCase().equals("sec"))
			//System.out.println(id);
		
		if (text == null)
			text = token.getForm();
		else
			text = text + " " + token.getForm();
		
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

		return this.text;
		
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
	
	/**
	 * Add the babelnet synset containing the keyphrase
	 * 
	 * @param synset the synset id
	 */
	public void addbabelnetSynset(String synset) {

		this.babelnetSynsets.add(synset);
		
	}
	
	/**
	 * Get the babel synsets iterator
	 * 
	 * @return the iterator
	 */
	public List<String> getbabelnetSynset() {

		return this.babelnetSynsets;
		
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
		return this.language.name().equals(((Keyphrase) obj).language.name()) && 
				Arrays.equals(this.tokens, ((Keyphrase) obj).tokens);
		//return Arrays.equals(this.tokens, ((Keyphrase) obj).tokens);

	}
	

	@Override
	public int hashCode() {

		//return id.hashCode();
		return Objects.hash(this.language.name(), Arrays.hashCode(tokens));
		//return Arrays.hashCode(tokens);

	}

}
