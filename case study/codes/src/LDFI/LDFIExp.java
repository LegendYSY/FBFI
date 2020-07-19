package LDFI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Z3.Z3_Solver;
import Basic.Graph;
import CreateToken.JWT;
import FBFI.Main;

public class LDFIExp {

	public List<String> Nodes_set = new ArrayList<>();
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		long time1 = System.currentTimeMillis();
		Main m = new Main();
		JWT jwt = new JWT();
		String auth = jwt.createToken();
		Graph g = new Graph();
		if(m.Execution(auth)){
			System.out.println("Execution is successful!");
			String path = m.ExecutionPath();		//×·×Ùµ÷ÓÃÁ´
			System.out.println("initial path = " + path);
			g.AddTrace(path);
			//////////////////
			int index = 1;
			Z3_Solver z = new Z3_Solver();
			String cnf = g.CNF_output();
			Set<Set<String>> FaultyCases = new HashSet<>();
			boolean satis = z.SolveLDFI(g,cnf,FaultyCases,index);
			//System.out.println(satis);
			int count = 1;
			int failcase = 0;
			while(satis){
				//System.out.println("--------------------" + count + "-------------");
				String result = z.result;
				result = result.replaceAll("-", "");
				//System.out.println("result is " + result);
				String[] hosts = result.split(",");
				Set<String> temp = new HashSet<>();
				for(String h:hosts){
					temp.add(h);
				}
				//System.out.println("Fault is " + Fault);
				m.InsertFaults(temp);
				auth = jwt.createToken();
				if(m.Execution(auth)){
					System.out.println("Successful");
	            	System.out.println("path= " + path);
					path = m.ExecutionPath();
	            	g.AddTrace(path);
	            	cnf = g.CNF_output();
				}else{
					System.out.println("Failed");
					Set<Set<String>> TC = new HashSet<>();
	            	for(Set<String> El:FaultyCases){
	            		if(!El.containsAll(temp))
	            			TC.add(El);
	            	}
	            	TC.add(temp);
	            	FaultyCases = TC;
	            	failcase ++;
	            	//bottlenecks = FaultyCases.size();
				}
				m.RestoreFaults(temp.size());
	            while(!(satis = z.Solve(g.N,cnf,FaultyCases,index)) && index < g.N){
	            	index ++ ;
	            }
	    		count++;
			}
			long time2 = System.currentTimeMillis();
			System.out.println(count + ",Nodes= " + g.NodesSet.size() + "," + failcase + ",time=" + (time2 - time1)/1000 + "s");
		}else{
			System.out.println("System error!");
		}
		
		
	}

}
