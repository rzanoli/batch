package eu.fbk.hlt.nlp.gcluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Keyphrases {
	
	private Map<Keyphrase,List<String>> masterList;
	private List<Keyphrase> innerList;
	
	private int i;
	private int j;
	private int nKeyphrases;
	
	public Keyphrases() {
		
		masterList = new HashMap<Keyphrase,List<String>>();
		innerList = new ArrayList<Keyphrase>();
		
	}
	
	public void add(String id, Keyphrase kx) {
		
		nKeyphrases++;
		
		if (!masterList.containsKey(kx)) {
			List<String> ids = new ArrayList<String>();
			ids.add(id);
			kx.setID(innerList.size());
			masterList.put(kx, ids);
			innerList.add(kx);
		}
		else {
			List<String> ids = masterList.get(kx);
			ids.add(id);
			masterList.put(kx, ids);
		}
		
	}
	
	public String getIDs(Keyphrase kx) {
		
		StringBuffer result = new StringBuffer();
		for (String id : masterList.get(kx)) {
			result.append(id);
			result.append(" ");
		}
		return result.toString().trim();
		
	}
	
	public Keyphrase get(int id) {
		
		return innerList.get(id);
		
	}
	
	public int size() {
		
		return this.innerList.size();
		
	}
	
	public int nKephrases() {
		
		return this.nKeyphrases;
		
	}
	
	public synchronized int increaseI() {
		
		return i++;
		
	}
	
	
	public synchronized Keyphrase[] next2() {
		
		Keyphrase[] result = null;
		
		j++;
		
		if (j == this.innerList.size())
			j = 0;
		
		if (j == i) {
			 i++;
			 if (i == this.innerList.size()) {
				i--;
				return null;
			 }
			 j = i + 1;
			 if (j == this.innerList.size())
					j = 0;
		}
		
		result = new Keyphrase[2];
		result[0] = this.innerList.get(i); 
		result[1] = this.innerList.get(j);
			
		return result;
		
	}
	
}
