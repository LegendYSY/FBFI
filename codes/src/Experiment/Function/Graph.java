package Experiment.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import GenQ.EquivalentSet;
import GenQ.Fault;
import GenQ.Nodes;

public class Graph
{
	public String line;
	public Nodes[] nodes;
	public Nodes[] explore_nodes;
	public boolean[] crashArray;
	public boolean[][] omitMatrix;
	public Set<String> edges = new HashSet<>();
	public Map<Integer,Set<Fault>> map = new HashMap<Integer,Set<Fault>>();				//记录故障瓶颈
	public Set<Integer> validindex = new HashSet<>();									//记录有效index
	public int N;
	public int omitSum = 0;
	public int traceNum = 0;
	private Set<Set<Integer>> ES = new HashSet<>();
	
	public List<String> nodeName = new ArrayList<>();
	public Graph(String _line,int _omitSum){
		line = _line;
		Initial();
		omitSum = _omitSum;
	}
	public Graph(String _line){
		line = _line;
		Initial();
	}
	public void cutoff_edges(String edgeslist){
		if(edgeslist.length() > 0){
			String[] edges = edgeslist.split(",");
			for(String edge:edges){
				String[] N = edge.split("-");
				int N1 = Integer.parseInt(N[0]);
				int N2 = Integer.parseInt(N[1]);
				nodes[N2].PreNodes.remove(N1);
			}
		}	
	}
	public void change_omitsum(int _omitSum){
		omitSum = _omitSum;
		validindex = new HashSet<>();
	}
	public void Initial() {
		// TODO Auto-generated method stub
		String[] services = line.split("-|,");
		N = 0;
		for(String ser:services){
			//System.out.println(ser);
			N += Integer.parseInt(ser);
		}
	//	nodes_name.add("start_node");
		String[] traces = line.split(",");
		nodes = new Nodes[N+2];
		explore_nodes = new Nodes[N+2];
		for(int i = 0;i<N+2;i++){
			nodes[i] = new Nodes("");
			explore_nodes[i] = new Nodes("");
		}
		nodeName.add("start_node");
		int count = 1;
		int trace_no = 0;
		int prenode = 0;
		int prenum = 1;
		for(String tr:traces){
			services = tr.split("-");
			int service_no = 0;
			prenode = 0;
			prenum = 1;
			for(String ser:services){
				int service_num = Integer.parseInt(ser);
				String service_name = (char)('A' + trace_no) + "" + (char)('a' + service_no);
				for(int i = 1;i <= service_num;i++){
					nodes[count].AddPreNodes(prenode,prenum);
					nodes[count].AddInfo("Layer_" + service_name,service_no);
					nodeName.add(service_name + i);
					count ++;
				}
				prenum = service_num;
				prenode = count - prenum;	
				service_no++;
			}
			nodes[N + 1].AddPreNodes(prenode,prenum);
			trace_no++;
		}
		
		//for(int i = 0;i<N+2;i++)
		//	System.out.println(i + ":" + nodes[i].PreNodes);
		nodeName.add("final_node");
		//System.out.println(nodeName);
		crashArray = new boolean[N + 2];
		omitMatrix = new boolean[N + 2][N + 2];
		for(int i = 0;i<N + 2;i++)
		{
			crashArray[i] = false;
			for(int j = 0;j<N + 2;j++)
				omitMatrix[i][j] = false;
		}
	}
	public void InjectFaults(String fault_str){
		crashArray = new boolean[N + 2];
		omitMatrix = new boolean[N + 2][N + 2];
		for(int i = 0;i<N + 2;i++)
		{
			crashArray[i] = false;
			for(int j = 0;j<N + 2;j++)
				omitMatrix[i][j] = false;
		}
		String []faults = fault_str.split("&");
		for(String fau:faults){
			if(fau.length() > 0){
				if(fau.indexOf("-") == -1)
					crashArray[Integer.parseInt(fau)] = true;
				else{
					String[] f = fau.split("-");
					omitMatrix[Integer.parseInt(f[0])][Integer.parseInt(f[1])] = true;
				}
			}
		}
	}
	public void AddTrace(String trace){
		traceNum ++;
		int change_time = -1;
		String[] hosts = trace.split("-");
		int prenode = 0;
		int index = 0;
		for(String host:hosts){
			int cur = Integer.parseInt(host);
			explore_nodes[cur].AddOneNode(prenode);
			if(prenode != 0 && !edges.contains(prenode + "-" +cur)){
				edges.add(prenode + "-" +cur);
				if(change_time < 0)
					change_time = index;
			}
			prenode = cur;
			index++;
		}
		explore_nodes[N + 1].AddOneNode(prenode);
		/////////////////////保留之前结果/////////////
		validindex = new HashSet<>();
		for(int i = 1;i<N + 1;i++){
			//System.out.println(nodes[i].time);
			if(nodes[i].time < change_time)
				validindex.add(i);
		}
		//System.out.println(validindex);
	}
    public boolean IsTrueFaults(){						//注入故障
    	List<Integer> nodeset = new ArrayList();
    	nodeset.add(N + 1);
    	//for(int i = 0;i<=N;i++)
    		//System.out.println(explore_nodes[i].PreNodes);
    	for(int i = 0;i<nodeset.size();i++)
    	{		
    		int current = nodeset.get(i);
    		for(int prenode:explore_nodes[current].PreNodes){
    			if(!crashArray[prenode] && !omitMatrix[prenode][current]){
    				if(prenode == 0)
        				return false;
        			if(!nodeset.contains(prenode))
        				nodeset.add(prenode);
    			}
    		}
    	}
    	return true;
    }
    public String VirtualTrace(){
    	String trace = "";
    	int[] next_node = new int[N];
    	int count = 0;
    	List<Integer> nodeset = new ArrayList();
    	nodeset.add(N + 1);
    	for(int i = 0;i<nodeset.size();i++)
    	{
    		int current = nodeset.get(i);
    		for(int prenode:explore_nodes[current].PreNodes){
    			if(!crashArray[prenode] && !omitMatrix[prenode][current]){
    				if(prenode == 0){
    					int index = i - 1;
    					while(current != N + 1){
    						trace += current + "-";
    						index = next_node[index];
    						current = nodeset.get(index);
    						index --;
    					}
    					if(trace.endsWith("-"))
    						return trace.substring(0,trace.length() - 1);
    				}
        			if(!nodeset.contains(prenode)){
        				nodeset.add(prenode);
        				next_node[count] = i;
        				count++;			
        			}
        				
    			}
    		}
    	}
    	return trace;
    }
    public String TrackTrace(){
    	String trace = "";
    	
    	int[] next_node = new int[N];
    	int count = 0;
    	List<Integer> nodeset = new ArrayList<>();
    	nodeset.add(N + 1);
    	for(int i = 0;i<nodeset.size();i++)
    	{
    		int current = nodeset.get(i);
    		for(int prenode:nodes[current].PreNodes){
    			if(!crashArray[prenode] && !omitMatrix[prenode][current]){
    				if(prenode == 0){
    					int index = i - 1;
    					while(current != N + 1){
    						trace += current + "-";
    						index = next_node[index];
    						current = nodeset.get(index);
    						index --;
    					}
    					if(trace.endsWith("-"))
    						return trace.substring(0,trace.length() - 1);
    				}
        			if(!nodeset.contains(prenode)){
        				nodeset.add(prenode);
        				next_node[count] = i;
        				count++;			
        			}
        				
    			}
    		}
    	}
    	return trace;
    }
    public void UpdateFaultInfo(Set<Fault> FS){				//排序
    	for(Fault f : FS){
    		double min_time = Integer.MAX_VALUE;
    		for(String singlefault:f.set){
        		if(singlefault.contains("-")){
        			String[] s = singlefault.split("-");
        			int n = Integer.parseInt(s[0]);
        			if(nodes[n].time + 0.5 < min_time)
        				min_time = nodes[n].time + 0.5;
        		}else{
        			int n = Integer.parseInt(singlefault);
        			//f.layer_set.add(nodes[n].time);
        			if(nodes[n].time < min_time)
        				min_time = nodes[n].time;
        			
        		}
        	}
    		f.time = min_time;
    		if(f.omit_set.size() == 0)
    			f.type = 0;
    		else
    			f.type = 1;
    		//f.size = f.set.size();
    		//f.layers = f.layer_set.size();
    		//if(f.omit_set.size() > 0)
    			//f.type = 1;
    	}
    }
    public int GetWeight(Fault f){
    	this.InjectFaults(f.toString());
    	String path = this.TrackTrace();
    	String[] nodes = path.split("-");
    	int count = 0;
    	for(int i = 0;i<nodes.length;i++){
    		if(i > 0 && !edges.contains(nodes[i-1] + "-" + nodes[i]))
    			count ++;
    	}
    	return count;
    }
    public Set<Fault> getFaults(){
		ES = new HashSet();
		for(int i = 1;i<N+1;i++)
			AddNode(i);
    	//map = new HashMap<Integer,Set<Fault>>();
    	Set<Fault> set = getFS(N + 1);
    	/*System.out.println(map.get(1));
    	System.out.println(map.get(2));
    	System.out.println(map.get(3));
    	System.out.println(map.get(4));
    	System.out.println(map.get(5));
    	System.out.println(map.get(6));*/
    	
    	return set;
    }
    public Set<Fault> getFS(int n){
    	GenQ.Q q = new GenQ.Q();
    	Set<Fault> set = new HashSet<>();
    	if(n == 0)
    		return set;
    	else{
    		if(map.containsKey(n) && validindex.contains(n)){
    			return map.get(n);
    		}
    		else{
    			Set<Fault> fn = new HashSet<>();
    		//	System.out.println(n + ":" + nodes[n].PreNodes);
    			for(int m : explore_nodes[n].PreNodes){
    				//System.out.println("m = " + m);
    				Set<Fault> fm = new HashSet<>();
    				if(n != N + 1 && m != 0)
    					fm = q.Disjunctive(getFS(m), Integer.toString(m) + "-" + Integer.toString(n));
    				else
    					fm = getFS(m);
    				fn = q.Conjunctions(fn, fm, omitSum);
    			}
    			if(n != N + 1)
    				fn = q.Disjunctive(fn, Integer.toString(n));
    			//System.out.println(GenQ.Q.Conj_time + "," + GenQ.Q.Judge_time + "," + GenQ.Q.Redunc_time + "," + GenQ.Q.df.format((double)GenQ.Q.Redunc_time/(1 + GenQ.Q.Conj_time)));
    			map.put(n,fn);
    			//if(omitSum == 1)
    			//	//System.out.println("n = " + n + "N = " + N);
    			validindex.add(n);
    		//	System.out.println("n = " + n + ",fn = " + fn.size());
    			//System.out.println(fn);
    			//对等价集进行更新
    			Set<Integer> ES_n = getES(n);
    		//	System.out.println(ES_n);
    			for(int index:ES_n){
    				if(!map.containsKey(index)){
    					map.put(index, getES_Fault(n,index,fn));
    					validindex.add(index);
    				}
    			}
    			//System.out.println(map);
    			////////////////
    			//System.out.println(fn);
    			return fn;
    		}
    	}
    }
    //等价集
   
    public void AddNode(int index){
		boolean tag = true;
		for(Set<Integer> set:ES){
			for(int temp:set){
				if(explore_nodes[index].PreNodes.equals(explore_nodes[temp].PreNodes)){
					set.add(index);
					tag = false;
				}
				break;
			}
			if(!tag)
				break;
		}
		if(tag){
			Set<Integer> set = new HashSet<>();
			set.add(index);
			ES.add(set);
		}
	}
    public Set<Integer> getES(int key){
		for(Set<Integer> set:ES){
			if(set.contains(key))
				return set;
		}
		return new HashSet<>();
	}
    public Set<Fault> getES_Fault(int key,int index,Set<Fault> fn){
  		Set<Fault> sf = new HashSet<>();
    	for(Fault f:fn){
  			Fault new_f = new Fault();
  			for(String str : f.set){
  				if(!str.contains("-")){
  					if(str.equals(Integer.toString(key))){
  	  					new_f.set.add(Integer.toString(index));
  	  				}else{
  	  					new_f.set.add(str);
  	  				}
  				}
  			}
  			for(String omit_str:f.omit_set){
  				if(omit_str.startsWith(key + "-")){
  					new_f.omit_set.add(omit_str.replace(key + "-", index + "-"));
  				}else if(omit_str.endsWith("-" + key)){
  					new_f.omit_set.add(omit_str.replace("-" + key, "-" + index));
  				}
  				else
  					new_f.omit_set.add(omit_str);
  			}
  			new_f.set.addAll(new_f.omit_set);
  			sf.add(new_f);
  		}
  		return sf;
  	}
}
