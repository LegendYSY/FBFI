package Z3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import Basic.Graph;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class Z3_Solver {
	public int N;
	public String CNF;
	public boolean Satisfible = false;
	public String result = "";
	public boolean Solve(int n,String cnf,Set<Set<String>> FaultyCases,int mostTrue){
		result = "";
		
		HashMap<String, String> cfg = new HashMap<String, String>();  
        cfg.put("model", "true");  
        Context ctx = new Context(cfg);  
        Solver s = ctx.mkSolver();  
        
        
		N = n;
		//System.out.println(N);
		BoolExpr[] x = new BoolExpr[N];
		for(int i = 0;i<N;i++){
			 x[i] = ctx.mkBoolConst("x"+i);  
		}
		
		//
		BoolExpr most = ctx.mkAtMost(x, mostTrue);  
		s.add(most);
		String[] fs = cnf.split("&");
		for(String f : fs){
			String []hs = f.split(",");
			BoolExpr OR = ctx.mkBool(false);
			for(String h : hs){
				if(h.length() > 0)
					OR = ctx.mkOr(OR,x[Integer.parseInt(h) - 1]);
			}
			s.add(OR);
		}
		///////////////
		for(Set<String> fc : FaultyCases){
			BoolExpr hasTest = ctx.mkBool(true);
			for(String h : fc)
				hasTest = ctx.mkAnd(hasTest,x[Integer.parseInt(h) - 1]);
			hasTest = ctx.mkNot(hasTest);
			s.add(hasTest);
		}
		Status response = s.check();  
		 
        if (response == Status.SATISFIABLE){  
         	final Model model = s.getModel();
         	for(int i = 0;i<N;i++){
         		if(model.getConstInterp(x[i]).toString().equals("true"))
         			result += (i+1)+",";
         	}
         	if(result.length() > 0)
         		result = result.substring(0, result.length() - 1);
         	return true;  
         }  
         else if(response == Status.UNSATISFIABLE){  
                 return false;
         }
         else{
             System.out.println("unknow");  
             return false;
         }
	}public boolean SolveLDFI(Graph g,String cnf,Set<Set<String>> FaultyCases,int mostTrue){
		result = "";
		
		HashMap<String, String> cfg = new HashMap<String, String>();  
        cfg.put("model", "true");  
        Context ctx = new Context(cfg);  
        Solver s = ctx.mkSolver();  
        
        
		N = g.N;
	//	System.out.println(N);
		BoolExpr[] x = new BoolExpr[N];
		for(int i = 0;i<N;i++){
			 x[i] = ctx.mkBoolConst("x"+i);  
		}
		
		//
		BoolExpr most = ctx.mkAtMost(x, mostTrue);  
		s.add(most);
		String[] fs = cnf.split("&");
		for(String f : fs){
			String []hs = f.split(",");
			BoolExpr OR = ctx.mkBool(false);
			for(String h : hs){
				if(h.length() > 0){
					//OR = ctx.mkOr(OR,x[Integer.parseInt(h) - 1]);
					OR = ctx.mkOr(OR,x[g.NodesName.indexOf(h) - 2]);
				}
			}
			s.add(OR);
		}
		///////////////
		for(Set<String> fc : FaultyCases){
			BoolExpr hasTest = ctx.mkBool(true);
			for(String h : fc)
				hasTest = ctx.mkAnd(hasTest,x[g.NodesName.indexOf(h) - 2]);
				//hasTest = ctx.mkAnd(hasTest,x[Integer.parseInt(h) - 1]);
			hasTest = ctx.mkNot(hasTest);
			s.add(hasTest);
		}
		Status response = s.check();  
        if (response == Status.SATISFIABLE){  
         	final Model model = s.getModel();
         	for(int i = 0;i<N;i++){
         		if(model.getConstInterp(x[i]).toString().equals("true")){
         			result += g.NodesName.get(i + 2) + ",";
         		}
         	}
         	if(result.length() > 0)
         		result = result.substring(0, result.length() - 1);
         	return true;  
         }  
         else if(response == Status.UNSATISFIABLE){  
                 return false;
         }
         else{
             System.out.println("unknow");  
             return false;
         }
	}
}
