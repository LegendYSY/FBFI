package GenQ;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class GenFile {
	public List<String> ReadFile(String fileaddr){
		List<String> content = new ArrayList();
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileaddr),"GBK"));
	        String line = null;
	        while ((line = in.readLine()) != null) {
	            content.add(line);
	        }   
	        in.close();
		}catch(IOException e){}
        return content;
	}
	public void WriteFile(String fileaddr,List<String> content){
		try{
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileaddr),"GBK"));
	        for(String line:content){
	        	 out.write(line);
	        	 out.newLine();
	        }
	        out.flush();
	        out.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}    
	}
	public void WriteFile(String fileaddr,String content){
		try{
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileaddr),"GBK"));
	        out.write(content);
	        out.flush();
	        out.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}    
	}
}
