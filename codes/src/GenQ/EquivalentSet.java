package GenQ;

import java.util.HashSet;
import java.util.Set;

public class EquivalentSet {
	public Set<Set<Integer>> ES = new HashSet<>();
	public void AddES(Set<Integer> set){
		ES.add(set);
	}
	public Set<Integer> getES(int key){
		for(Set<Integer> set:ES){
			if(set.contains(key))
				return set;
		}
		return new HashSet<>();
	}
}
