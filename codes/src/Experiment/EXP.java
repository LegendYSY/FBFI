package Experiment;

import java.util.List;

import Experiment.Algorithm.FBFI;
import Experiment.Algorithm.RandomAlgorithm;

public class EXP {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int omitSum = 0;
		int sequence = -1;
		String model_addr;
		try{
			omitSum = Integer.parseInt(args[0]);
			model_addr = args[1];
		}catch(Exception e){
			try{
				model_addr = args[0];
			}catch(Exception e1){
				System.out.println("Please execute the command : java -jar EXP.jar [k] model [sequence]");
				return;
			}
		}
		try{
			sequence = Integer.parseInt(args[args.length - 1]);
		}catch(Exception e2){
			sequence = -1;
		}
		////////////////////////////////////////////////////
		String output = "";
		List<String> models = new GenQ.GenFile().ReadFile(model_addr);
		if(sequence == -1){
			for (int i = 0;i<models.size();i++) {
				String content = models.get(i);
				String model = content.split(",\\[excludes\\] ")[0].replace("model is ", "");
				String excludes_edges = "";
				try{
					excludes_edges = content.split(",\\[excludes\\] ")[1];
				}catch(Exception e)
				{}
				String str = "Model" + (i+1) + ":\n";
				FBFI fb = new FBFI();
				str += fb.MainFunc(model, excludes_edges, omitSum) + "\n";
				str += "Random-1x" + ":";
				str += new RandomAlgorithm().MainFunc(model, excludes_edges, fb.M, fb.T) + "\n";
				str += "Random-2x" + ":";
				str += new RandomAlgorithm().MainFunc(model, excludes_edges, 2*fb.M, fb.T) + "\n";
				str += "Random-4x" + ":";
				str += new RandomAlgorithm().MainFunc(model, excludes_edges, 4*fb.M, fb.T) + "\n";
				System.out.println(str);
				output += str + "\n";
			}
		}else{
			try{
				String content = models.get(sequence - 1);
				String model = content.split(",\\[excludes\\] ")[0].replace("model is ", "");
				String excludes_edges = "";
				try{
					excludes_edges = content.split(",\\[excludes\\] ")[1];
				}catch(Exception e)
				{}
				output += "Model" + sequence + ":\n";
				FBFI fb = new FBFI();
				output += fb.MainFunc(model, excludes_edges, omitSum) + "\n";
				output += "Random-1x" + ":";
				output += new RandomAlgorithm().MainFunc(model, excludes_edges, fb.M, fb.T) + "\n";
				output += "Random-2x" + ":";
				output += new RandomAlgorithm().MainFunc(model, excludes_edges, 2*fb.M, fb.T) + "\n";
				output += "Random-4x" + ":";
				output += new RandomAlgorithm().MainFunc(model, excludes_edges, 4*fb.M, fb.T) + "\n";
				System.out.println(output);
			}catch(Exception e){
				System.out.println("sequence must be an integer in [1," + models.size() + "]");
			}
			
		}
		System.out.println("The results is saved in " + System.getProperty("user.dir") + "/out.txt.");
		new GenQ.GenFile().WriteFile(System.getProperty("user.dir") + "/output.txt", output);
	}

}
