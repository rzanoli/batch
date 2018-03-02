package eu.fbk.hlt.nlp.cluster;

import java.util.Objects;

/**
 * 
 * This class represents a token of a keyphrase
 * 
 * @author rzanoli
 *
 */
public class Token {

	// the lowercase form
	private String form;
	// the part-of-speech
	private String PoS;
	// the wordnet PoS
	private String wordnetAnnotation;
	// the lemma
	private String lemma;
	// the number of its characters
	private int characters;
	// if it is an abbreviation
	// We consider an abbreviation any token which finishes with a dot
	private boolean isAbbreviation;
	// if it is an acronym
	// We consider as acronym:
	// any token with two or more capitalized letters, either with dots or without
	// dots (e.g. FBK, S.p.A.)
	// any token which has two or more dots e.g. (S.r.l.)
	private boolean isAcronym;

	public Token(String form, String PoS, String lemma) {

		this.form = form.toLowerCase();
		this.PoS = PoS;
		this.lemma = lemma;

		// compute the length of the token ('.' are not considered)
		// is abbreviation
		// is acronym
		int capitalizedLetters = 0;
		int dots = 0;
		for (int i = 0; i < form.length(); i++) {
			if (form.charAt(i) != '.') {
				this.characters++;
			} else
				dots++;
		}
		if ((dots >= 2 || this.characters >= 2) &&
				(this.PoS.equals("SS") || this.PoS.equals("SP") || this.PoS.equals("SPN") || 
						this.PoS.equals("YA") || this.PoS.equals("YF")))
			this.isAcronym = true;
		else if (dots == 1 && this.characters >= 1)
			this.isAbbreviation = true;
		
		// assign the wordnet PoS
		if (this.PoS.startsWith("S") || this.PoS.startsWith("Y")) //- n (noun): SS,SP,SN,SPN,YA,YF
			this.wordnetAnnotation = "n#" + this.lemma.toLowerCase();
		else if (this.PoS.startsWith("V")) //- v (verb): VI,VI+E,VIY,VIY+E,VF,VF+E,VFY,VFY+E,VSP,VSP+E,VSPY,VSPY+E,VPP,VPP+E,VPPY,VPPY+E,VG,VG+E,VGY,VGY+E,VM,VM+E,VMY,VMY+E
			this.wordnetAnnotation = "v#" + this.lemma.toLowerCase();
		else if (this.PoS.startsWith("A") || this.PoS.startsWith("D")) // - a (adjective): AS,AP,AN,DS,DP,DN  
			this.wordnetAnnotation = "a#" + this.lemma.toLowerCase();
		else if (this.PoS.startsWith("B")) // - r (adverb): B
			this.wordnetAnnotation = "r#" + this.lemma.toLowerCase();
		
	}

	/**
	 * Get the text of the token
	 * 
	 * @return
	 */
	public String getForm() {

		return this.form;

	}

	/**
	 * Get the lemma of the token
	 * 
	 * @return
	 */
	public String getLemma() {

		return this.lemma;

	}

	/**
	 * Get the part-of-speech of the token
	 * 
	 * @return
	 */
	public String getPoS() {

		return this.PoS;

	}
	
	/**
	 * Get the Wordnet part-of-speech of the token
	 * 
	 * @return
	 */
	public String getWordnetAnnotation() {

		return this.wordnetAnnotation;

	}

	/**
	 * Get the token length
	 * 
	 * @return
	 */
	public int length() {

		return this.form.length();

	}

	/**
	 * Get the number of character of the token
	 * 
	 * @return
	 */
	public int characters() {

		return this.characters;

	}

	/**
	 * Get the character at position index
	 * 
	 * @param index
	 *            the position of the character in the token
	 * 
	 * @return the character
	 */
	public char charAt(int index) {

		return this.form.charAt(index);

	}

	/**
	 * Get the first character of the token
	 * 
	 * @return the first character
	 */
	public char startWith() {

		return this.form.charAt(0);

	}

	/**
	 * modo: per compendio (utilizzando una o più lettere iniziali della parola) es,
	 * dottor--> dott. modo: per contrazione (quando in una parola sono soppresse
	 * lettere o sillabe intermedie) es, fratelli--> f.lli
	 * 
	 * @return
	 */
	public boolean isAbbreviation() {

		return this.isAbbreviation;

	}

	public boolean isAcronym() {

		return this.isAcronym;

	}

	/**
	 * If the current token is an abbreviation of the given token in input.
	 * 
	 * @param token
	 * 
	 * @return true if it is an abbreviation of the token in input; false otherwise
	 */
	public boolean isAbbreviationOf(Token token) {

		if (isAbbreviation == false || this.characters >= token.characters || startWith() != token.startWith())
			return false;

		return token.form.matches(this.form.replaceAll("\\.", "\\.+"));

	}

	/**
	 * Return true if the current token and the given token have the same lemma;
	 * false otherwise
	 * 
	 * @param token
	 *            the token to compare with the given token
	 *            
	 * @return if the 2 tokens have the same lemma
	 */
	public boolean equalsLemma2(Token token) {

		return this.lemma.equals(token.lemma);

	}

	/**
	 * Return true if the current token and the given token have the same form;
	 * false otherwise
	 * 
	 * @param token
	 *            the token to compare with the given token
	 *            
	 * @return if the 2 tokens have the same form
	 */
	public boolean equalsForm2(Token token) {

		return this.characters == token.characters && 
				this.form.equals(token.form);

	}
	
	/**
	 * Return true if the current token and the given token have the same form;
	 * false otherwise
	 * 
	 * @param token
	 *            the token to compare with the given token
	 *            
	 * @return if the 2 tokens have the same form
	 */
	public boolean equalsPoS22(Token token) {

		return this.PoS.equals(token.PoS);

	}


	/**
	 * If the current token is equal (they have the same form and PoS) to the token in input
	 * 
	 * @param token
	 * 
	 * @return true if it is equal to the token in input; false otherwise
	 */
	@Override
	public boolean equals(Object obj) {

		// null instanceof Object will always return false
		if (!(obj instanceof Token))
			return false;
		if (obj == this)
			return true;

		return this.characters == ((Token) obj).characters && 
				this.form.equals(((Token) obj).form) && this.PoS.equals(((Token) obj).PoS);

	}

	@Override
	public int hashCode() {

		// return id.hashCode();
		return Objects.hash(form, PoS);

	}

}
