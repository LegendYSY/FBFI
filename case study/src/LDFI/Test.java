package LDFI;

import java.util.HashSet;

import Basic.Graph;
import Z3.Z3_Solver;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Graph g = new Graph();
		g.AddTrace("A1---B---C1");
		g.AddTrace("A2---B---C2");
	//	System.out.println(g.NodesName);
		String cnf = g.CNF_output();
		Z3_Solver z = new Z3_Solver();
		System.out.println(z.SolveLDFI(g,cnf,new HashSet<>(),2));
		
		System.out.println(z.result);
	}

}
