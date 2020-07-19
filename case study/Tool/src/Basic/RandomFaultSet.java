package Basic;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomFaultSet {
	public Set<Fault> validfaults = new HashSet();
	public List<String> nodes = new ArrayList();
	public List<String> edges = new ArrayList();
	public RandomFaultSet(){
	}
	public void AddTrace(String trace){
		String[] hosts = trace.split("---");
		String prehost = "";
		for(String host:hosts){
			if(!nodes.contains(host))
				nodes.add(host);
			if(prehost.length() > 0 && !edges.contains(prehost + "---" + host))
				edges.add(prehost + "---" + host);
			prehost = host;
		}
	}
	public Fault getRandomFault(int t){
		Set<String> set = new HashSet();
		if(t > nodes.size())
			t = nodes.size();
		Random r = new Random();
		for(int i = 0;i<t-1;){
			int ran = r.nextInt(nodes.size());
			String node = nodes.get(ran);
			if(!set.contains(node)){
				set.add(node);
				i++;
			}
		}
		return new Fault(set);
	}
}
