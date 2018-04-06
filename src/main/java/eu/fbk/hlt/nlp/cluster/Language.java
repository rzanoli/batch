package eu.fbk.hlt.nlp.cluster;

public class Language {

	public static enum VALUE {
		IT, DE, EN, MULTILINGUAL;
	}
	
	public static String GET_WORDNET_POS(String PoS, Language.VALUE language) {
		
		String result = null;
		
		if (language == Language.VALUE.IT) {
			if (PoS.startsWith("S") || PoS.startsWith("Y")) //- n (noun): SS,SP,SN,SPN,YA,YF
				result = "n";
			else if (PoS.startsWith("V")) //- v (verb): VI,VI+E,VIY,VIY+E,VF,VF+E,VFY,VFY+E,VSP,VSP+E,VSPY,VSPY+E,VPP,VPP+E,VPPY,VPPY+E,VG,VG+E,VGY,VGY+E,VM,VM+E,VMY,VMY+E
				result = "v";
			else if (PoS.startsWith("A") || PoS.startsWith("D")) // - a (adjective): AS,AP,AN,DS,DP,DN  
				result = "a" ;
			else if (PoS.startsWith("B")) // - r (adverb): B
				result = "r";
		}
		else if (language == Language.VALUE.DE) {
		
		}
		else if (language == Language.VALUE.EN) {
			// assign the wordnet PoS
			if (PoS.startsWith("N")) //- n (noun): NN0, NN1, NN2, NP0
				result = "n";
			else if (PoS.startsWith("V")) // - v (verb):
				result = "v";
			else if (PoS.startsWith("AJ")) // - a (adjective): 
				result = "a";
			else if (PoS.startsWith("AV")) // - r (adverb):
				result = "r";
		}
		
		return result;
		
	}
	
}
