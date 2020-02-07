package Experiment.Algorithm;

import java.util.HashSet;
import java.util.Set;

import Experiment.Function.Graph;
import Experiment.Function.RandomFaultSet;
import GenQ.Fault;
import GenQ.Q;

public class RandomAlgorithm {

	// TODO Auto-generated method stub
	public String MainFunc(String model,String excludes_edges,int M,int T){
		int failcases = 0;
		int bottlecases = 0;
		int random_nodesnum = 0;
		int random_edgesnum = 0;
		long startTime = System.currentTimeMillis();
		for(int index = 0;index < 30;index ++){
			Graph g = new Graph(model);
			g.cutoff_edges(excludes_edges);
			//System.out.println(model);
			String path = g.TrackTrace();
			RandomFaultSet rfs = new RandomFaultSet();
			rfs.AddTrace(path);
			
			for(int j = 0;j<M;j++){
				int t = Math.min(T, rfs.nodes.size());
				Fault fault = rfs.getRandomFault(t);
				g.InjectFaults(fault.toString());
				if((path = g.TrackTrace()) == ""){					//³É¹¦ÆÆ»µ
					rfs.validfaults.add(fault);
			//		System.out.println(fault.toString());
				}else
					rfs.AddTrace(path);
			}
			//////////////////////
		//	System.out.println("-----------------");
			failcases += rfs.validfaults.size();
			random_nodesnum += rfs.nodes.size();
			random_edgesnum += rfs.edges.size();
			Set<Fault> vf = rfs.validfaults;
			Set<Fault> fs = new HashSet();
			for(Fault f:vf){
				Fault temp;
				boolean tf = true;
				for(Fault s:fs){
					temp = new Fault(f.set);
					temp.set.retainAll(s.set);
					//System.out.println(temp.toString());
					g.InjectFaults(temp.toString());
					if(g.TrackTrace() == "")
					{
						tf = false;
						break;
					}
				}
				if(tf)
				{
					fs.add(f);
				//	System.out.println(f.toString());
				}
			}
			bottlecases += fs.size();
		}
		Q q = new Q();
		long endTime = System.currentTimeMillis();
		return "rounds = " + M + ",t_max = " + T + ",failcases = " +  q.df.format((double)failcases/30) + ",bottlenecks = " +  q.df.format((double)bottlecases/30) + ",explore_nodes = " + q.df.format((double)random_nodesnum/30) + ",explore_edges = " + q.df.format((double)random_edgesnum/30) + ",total_time = " + (endTime - startTime) + "ms";
		
	}
}
