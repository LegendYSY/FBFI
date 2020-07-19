package Basic;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import GenQ.Fault;
import GenQ.Nodes;

public class SolvingBottleNeckByPath {
	private int omitSum = 0;
	private List<Nodes> array = new ArrayList<>();
	public SolvingBottleNeckByPath(int _t){
		omitSum = _t;
		array.add(new Nodes("Final_goal"));
	}
	public void AddPath(String path){
		String[] X = path.split("---");
		int pre = -1;
		for(int i = 0;i<X.length;i++){
			int cur = AddNode(X[i]);
			if(pre > 0)
				array.get(cur).AddPreNode(pre);
			pre = cur;
		}
		array.get(0).AddPreNode(pre);
	}
	public int AddNode(String x){
		int i = 0;
		for(Nodes y : array){
			if(y.layer.equals(x))
				return i;
			i++;
		}
		array.add(new Nodes(x));
		return i;
	}
	public Set<Set<String>> getCases(){
		Set<Fault> tcs = getFaults();
		Set<Set<String>> cases = new HashSet<>();
		for(Fault f : tcs){
			cases.add(f.set);
		}
		return cases;
	}
	public Set<Fault> getFaults(){
    	return getFS("Final_goal");
	}
	public Set<Fault> getFS(String layer){
		GenQ.Q q = new GenQ.Q();
		Set<Fault> set = new HashSet<>();
		int index = AddNode(layer);
		Set<Fault> fn = new HashSet<>();
		//	System.out.println(n + ":" + nodes[n].PreNodes);
		//System.out.println(layer + ":" + array.get(index).PreNodes);
		for(int m : array.get(index).PreNodes){
			//System.out.println(array.get(m).layer);
			Set<Fault> fm = getFS(array.get(m).layer);
			if(!layer.equals("Final_goal") && omitSum > 0)
				fm = q.Disjunctive(fm, "Loss(" + array.get(m).layer + "," + layer + ")");
			fn = q.Conjunctions(fn, fm, omitSum);
		}
		if(!layer.equals("Final_goal"))
			fn = q.Disjunctive(fn, "Crash(" + layer + ")");
		//System.out.println(layer + ":" + fn);
		return fn;
	}
	
	
}

