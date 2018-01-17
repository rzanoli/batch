package eu.fbk.hlt.nlp.gcluster;


public class Keyphrase {

	private int id;
	private Token[] tokens;
	private String text;

	public Keyphrase(String text) {

		this.text = text;
		
		if (text != null) {
			String[] tokenizedText = text.split("\\s");
			this.tokens = new Token[tokenizedText.length];
			for (int i = 0; i < tokenizedText.length; i++)
				this.tokens[i] = new Token(tokenizedText[i]);
		}

	}
	
	public void setID(int id) {
		
		this.id = id;
		
	}

	public int length() {

		if (this.text == null)
			return -1;

		return tokens.length;

	}

	public Token get(int i) {

		if (i < 0 || i >= tokens.length)
			return null;

		return tokens[i];

	}
	
	public int getId() {
		
		return this.id;
		
	}
	
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
		
	}

	@Override
	public int hashCode() {
		
		return text.hashCode();
		
	}

}
