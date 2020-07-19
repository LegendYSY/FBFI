package GenQ;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Nodes {
	public Set<Integer> PreNodes = new HashSet();
	public String layer;										//层名;
	public int time;											//时间;
	public Nodes(String _layer){
		layer = _layer;
	}
	public void AddInfo(String _layer,int _time){
		layer = _layer;
		time = _time;
	}
	public void AddPreNodes(int prenode,int prenum){			//添加前置node;
		for(int i = 0;i<prenum;i++)
			PreNodes.add(prenode + i);
	//	System.out.println(PreNodes);
	}
	public void AddPreNode(int prenode){			//添加前置node;
		PreNodes.add(prenode);
	}
	public void AddOneNode(int node){
		if(!PreNodes.contains(node))
			PreNodes.add(node);
	}
	public boolean IsNext(int node){
		return PreNodes.contains(node);
	}
}
