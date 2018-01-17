package eu.fbk.hlt.nlp.gcluster;

public class Token {
	
	private String str;
	private int characters;
	private boolean isAbbreviation;
	
	public Token(String str) {
		
		this.str = str;
		// compute the length of the token ('.' are not considered)
		// is abbreviation
		// is acronym
		int dots = 0;
		for (int i = 0; i < this.str.length(); i++) {
			if (this.str.charAt(i) != '.')
				this.characters++;
			else
				dots++;
		}
		if (this.characters >= 1 && dots == 1)
			this.isAbbreviation = true;
		
	}
	
	public String getText() {
		
		return this.str;
		
	}
	
	public int length() {
		
		return this.str.length();
		
	}
	
	public int characters() {
		
		return this.characters;
		
	}
	
	public char charAt(int index) {
		
		return this.str.charAt(index);
		
	}
	
	public char startWith() {
		
		return this.str.charAt(0);
		
	}
	
	
	/**
	 * modo: per compendio (utilizzando una o più lettere iniziali della parola)
	 *	es,
	 * dottor--> dott.
	 * modo: per contrazione (quando in una parola sono soppresse lettere o sillabe intermedie)
	 * es,
	 * fratelli--> f.lli
	 * 
	 * @return
	 */
	public boolean isAbbreviation() {
		
		return this.isAbbreviation;
		
	}
	
	
	public boolean isAbbreviationOf(Token token) {
		
		if (isAbbreviation == false || 
				this.characters >= token.characters ||
				startWith() != token.startWith())
			return false;

		return token.getText().matches(this.str.replaceAll(".", ".+"));
		
	}
	
	/**
	 * 
	 * We don't use it anymore given that we consider as an acronym any token!!
	 * 
	 * 
	 * We consider as acronym:
	 * 
	 * any token with two or more capitalized letters, either with dots or without dots (e.g. FBK, S.p.A.) 
	 * any token which has two or more dots e.g. (S.r.l.)
	 *
	 * @return
	 * 
	 */
	public boolean isAcronym2() {
		
		int capitalizedLetters = 0;
		int dots = 0;
		for (int i = 0; i < this.str.length(); i++) {
			if(Character.isUpperCase(str.charAt(i))) {
				capitalizedLetters++;
	        }
			if(str.charAt(i) == '.') {
				dots++;
	        }
		}
		if (capitalizedLetters >= 2 || dots >= 2)
			return true;
		
		return false;
		
	}
	
	
	public boolean equals( Token token ) {
		
		if( this.characters != token.characters ||
				!token.str.equals(str) )
			return false;
		
		return true;
		
	}

}
