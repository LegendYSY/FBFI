package Basic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import GenQ.Nodes;

public class Graph {
	public int N = 0;
	public List<String> NodesName = new ArrayList<>();
	public List<Nodes> NodesSet = new ArrayList<>();
	public Graph(){
		NodesName.add("Start");
		NodesSet.add(new Nodes("Start"));
		NodesName.add("Final");
		NodesSet.add(new Nodes("Final"));
	}
	public void AddTrace(String trace){
		String[] hosts = trace.split("---");
		String pre_host = "Start";
		for(String host:hosts){
			if(NodesName.indexOf(host) == -1){
				NodesName.add(host);
				NodesSet.add(new Nodes(host));
				N ++;
			}
			///
			NodesSet.get(NodesName.indexOf(host)).AddPreNode(NodesName.indexOf(pre_host));
			pre_host = host;
		}
		NodesSet.get(NodesName.indexOf("Final")).AddPreNode(NodesName.indexOf(pre_host));
	}
	/////////CNFµ›πÈ«ÛΩ‚////////////////
	public Set<Set<String>> CNF(String host){
		Set<Set<String>> temp = new HashSet<>();
		if(host.equals("Start")){
			Set<String> empty = new HashSet<>();
			temp.add(empty);
		}
		else{
			/*for(int i = 0;i<N+2;i++){
				if(Connect[i][n]){
					Set<Set<String>> cnf_i = CNF(i);
					if(n < N + 1){
						for(Set<String> set_i:cnf_i)
							set_i.add(Integer.toString(n));
					}
					temp.addAll(cnf_i);
				}
			}*/
			int index = NodesName.indexOf(host);
			for(int preNode : NodesSet.get(index).PreNodes){
				Set<Set<String>> cnf_i = CNF(NodesName.get(preNode));
				if(!host.equals("Final")){
					for(Set<String> set_i:cnf_i)
						set_i.add(host);
				}
				temp.addAll(cnf_i);
			}
		}
		return temp;
	}
	public String CNF_output(){
		String result = "";
		Set<Set<String>> cnf = CNF("Final");
		for(Set<String> set:cnf){
			for(String str:set){
				result += str + ",";
			}
			result += "&";
		}
		result = result.substring(0,result.length() - 1);
		System.out.println(result);
		return result;
		
	}
}
