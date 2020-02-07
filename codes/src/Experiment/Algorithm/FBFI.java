package Experiment.Algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Experiment.Function.Graph;
import GenQ.Fault;



public class FBFI {
	public int T = 0;
	public int M;
	public String MainFunc(String model,String excludes_edges,int omitSum) {
		// TODO Auto-generated method stub
		String result = "";
		Graph g = new Graph(model,0);
		g.cutoff_edges(excludes_edges);
		String fault = "";
		Set<Fault> FS = new HashSet<>();
		//FS = g.getFaults();
		//System.out.println(FS.size());
		//System.out.println(FS);
		Map tested = new HashMap();
		long Time1 = System.currentTimeMillis();
		long Time3 = System.currentTimeMillis();
		int count = 0;
		int failcases = 0;
		while(true){
			g.InjectFaults(fault);
			if(fault.split("&").length > T)
				T = fault.split("&").length;
			String trace = g.TrackTrace();
			if(trace.length() > 0){
				result += "SUCCESS;";
				//System.out.println("SUCCESS;");
				g.AddTrace(trace);
				FS = g.getFaults();
				tested.put(fault, true);
			}else{
				result += "FAIL;";
				//System.out.println("FAIL;");
				failcases ++;
				tested.put(fault, false);
			}
			result += count + ":edges = " + g.edges.size() + ",failcases = " + failcases + ",fault = " + fault + ",";
			g.UpdateFaultInfo(FS);
			boolean stop = true;
			Fault optimal = new Fault();
			for(Fault f:FS){	
				if(!tested.containsKey(f.toString())){
					if(stop){
						optimal = f;
						stop = false;
						//break;
					}else{
						if(f.time < optimal.time)
							optimal = f;
					}
				}
			}
			count++;
			if(stop)
			{
				long Time2 = System.currentTimeMillis();	
				if(g.omitSum != omitSum){
					result += "\n" + FS + "\n";
				//	System.out.println(index + ":Fail = " + FS.size() + ",Round = " + count + ";Time = " + (Time2 - Time1) + "ms;");
					g.change_omitsum(omitSum);
					FS = g.getFaults();
				}else
					break;
			}
			else
				fault = optimal.toString();
			result += "\n";
			Time3 = System.currentTimeMillis();
			/*if(Time3 - Time1 > 3600000){
				break;
			}*/
		}
		M = count - 1;
		return "FBFI: " + "rounds = " + M + ",failcases = " + FS.size() + ",bottlenecks = " + FS.size() + ",explore_nodes = " + g.N + ",explore_edges = " + g.edges.size() +",time = " + (Time3 - Time1) + "ms;";
		//System.out.println(FS);
		//new GenQ.GenFile().WriteFile(FileAddr, result);
	}
}
