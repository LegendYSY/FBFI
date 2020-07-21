package GenQ;

import java.util.HashSet;
import java.util.Set;

public class Fault {
	public Set<String> set = new HashSet<>();
	public Set<String> omit_set = new HashSet<>();
	///////////////排序//////
	public int layers = 0;			//	层数
	public Set<Integer> layer_set = new HashSet<>();
	public int type = 0;			//	0表示仅包含crash故障，1表示omit故障
	public int size = 0;			
	public double time = 0;
	//////////////////////
	public int omit = 0;
	public Fault(){
	}
	public void OrderInfo(int _layers,double _time){
		layers = _layers;
		time = _time;
		size = set.size();
		if(omit > 0 && omit < size)
			type = 1;
		else
			type = 0;
	}
	public Fault(Set<String> et){
		set.addAll(et);
		for(String s:et){
			if(s.contains("-") && omit > 0)
				omit_set.add(s);
		}
	}
	public void add(Fault f){
		this.set.addAll(f.set);
		this.omit_set.addAll(f.omit_set);
	}
	public Fault(Fault f){
		this.set.addAll(f.set);
		this.omit_set.addAll(f.omit_set);
	}
	public Fault(String str){
		set.add(str);
		if(str.contains("-") && omit > 0)
			this.omit_set.add(str);
	}
	public boolean containFaultExcludingf(Fault F, String f){
		for(String s:F.set){
			if(!set.contains(s) && !s.equals(f))
				return false;
		}
		return true;
	}
	public boolean containSingleFault(String str){
		return set.contains(str);
	}
	public void addSingleFault(String single){
		set.add(single);
		if(single.contains("-"))
			omit++;
	}
	public String toString(){
		String str = "";
		for(String f:set){
			str += f + "&";
		}
		return str.length() == 0 ? str:str.substring(0,str.length() - 1);
	}
	
	public boolean BetterThan(Fault f,int[] seq,int[] val){
		boolean[][] P = new boolean[3][2];
		P[0][0] = this.layers > f.layers;
		P[0][1] = this.layers < f.layers;
		P[1][0] = this.type > f.type;
		P[1][1] = this.type < f.type;
		P[2][0] = this.size > f.size;
		P[2][1] = this.size < f.size;
		if(P[seq[0]][val[0]])
			return true;
		else if(P[seq[0]][0] == P[seq[0]][1] && P[seq[1]][val[1]])
			return true;
		else if(P[seq[0]][0] == P[seq[0]][1] && P[seq[1]][0] == P[seq[1]][1] && P[seq[2]][val[2]])
			return true;
		else 
			return false;
		
	}
	 public static boolean BetterThan(Fault f1,Fault f2){
	    	if(f1.layers < f2.layers)
	    		return true;
	    	else if(f1.layers == f2.layers){
	    		if(f1.size > f2.size)
	    			return true;
	    		else if(f1.size == f2.size){
	    			if(f1.type > f2.type)
	    				return true;
	    		}
	    			
	    	}
	    	return false;
	    	
	    }
}
