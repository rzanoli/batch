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

	// the form
	private String form;
	// the lowercase form
	private String lowercaseForm;
	// the part-of-speech
	private String PoS;
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

		this.form = form;
		this.PoS = PoS;
		this.lemma = lemma;
		this.lowercaseForm = form.toLowerCase();

		// compute the length of the token ('.' are not considered)
		// is abbreviation
		// is acronym
		int capitalizedLetters = 0;
		int dots = 0;
		for (int i = 0; i < form.length(); i++) {
			if (form.charAt(i) != '.') {
				this.characters++;
				if (Character.isUpperCase(form.charAt(i)))
					capitalizedLetters++;
			} else
				dots++;
		}
		if (dots >= 2 || capitalizedLetters >= 2)
			this.isAcronym = true;
		else if (dots == 1 && this.characters >= 1)
			this.isAbbreviation = true;

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
	 * lemma of the tokentext of the token
	 * 
	 * @return
	 */
	public String getLemma() {

		return this.lemma;

	}

	/**
	 * lemma of the part-of-speech of the token
	 * 
	 * @return
	 */
	public String getPoS() {

		return this.PoS;

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

		return this.lowercaseForm.charAt(index);

	}

	/**
	 * Get the first character of the token
	 * 
	 * @return the first character
	 */
	public char startWith() {

		return this.lowercaseForm.charAt(0);

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

		return token.lowercaseForm.matches(this.lowercaseForm.replaceAll("\\.", "\\.+"));

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
	public boolean equalsLemma(Token token) {

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
	public boolean equalsForm(Token token) {

		return this.characters == token.characters && 
				this.form.equals(token.form);

	}

	/**
	 * Return true if the current token and the given token have the same form; the
	 * comparison is case insensitive. False otherwise
	 * 
	 * @param token
	 *            the token to compare with the given token
	 *            
	 * @return if the 2 tokens have the same form
	 */
	public boolean equalsFormIgnoreCase(Token token) {

		return this.characters == token.characters && 
				this.lowercaseForm.equals(token.lowercaseForm);

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
