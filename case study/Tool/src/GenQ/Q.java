package GenQ;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Q {
	public static DecimalFormat df = new DecimalFormat("######0.00");  
	public static int Conj_time = 0;
	public static int Judge_time = 0;
	public static int Redunc_time = 0;
	public Set<Fault> Conjunctions(Set<Fault> set1,Set<Fault> set2,int omitSum){
		long startTime=System.currentTimeMillis();
		if(set1.size() == 0){
			//System.out.println(set1.size() + "*" + set2.size() + "=" + set2.size());
			return set2;
		}
		else if(set2.size() == 0){
			//System.out.println(set1.size() + "*" + set2.size() + "=" + set1.size());
			return set1;
		}else{
		//	System.out.print(set1.size() + "*" + set2.size() + "=");
			Set<Fault> set = new HashSet<Fault>();
			for(Fault f1:set1){
				for(Fault f2:set2){		
					Fault F = new Fault(f1);
					F.add(f2);
					if(omitSum == -1){
						if(F.omit_set.size()*(F.set.size() - F.omit_set.size()) == 0){
							if(Judge(set1,set2,f1,f2,F.set))
								set.add(F);
						}
					}else if(F.omit_set.size() <= omitSum){
						long startTime1 = System.currentTimeMillis();
						if(Judge(set1,set2,f1,f2,F.set))
							set.add(F);
						long endTime1=System.currentTimeMillis();
						Redunc_time += endTime1 - startTime1;			
					}		
					
				}
				//System.out.println("f1 = " + f1);
			}
			//System.out.println("set = " + set);
			long endTime=System.currentTimeMillis();
			Conj_time += endTime - startTime;
		//	System.out.println(set.size());
		//	return Reduction(set);
			return set;
		}
	}
	public Set<Fault> Disjunctive(Set<Fault> set1,String str){
		Set<Fault> set = new HashSet<>();
		Fault fault = new Fault(str);
		set.addAll(set1);
		set.add(fault);
		return set;
	}
	public boolean Judge(Set<Fault> set1,Set<Fault> set2,Fault f1,Fault f2,Set<String> union){	//若存在F1'包含于F1*F2且F1-F1^F2不包含于F1'或者存在F2'包含于F1*F2且F2-F1^F2不包含于F2',则F1*F2非最简
		long startTime = System.currentTimeMillis();
		Set<String> temp1 = new HashSet<>();
		Set<String> temp2 = new HashSet<>();
		
		temp1.addAll(union);
		temp1.removeAll(f2.set);
		temp2.addAll(union);
		temp2.removeAll(f1.set);
		
		for(Fault _f1:set1){					
			if(union.containsAll(_f1.set) && !_f1.set.containsAll(temp1))
				return false;
		}
		for(Fault _f2:set2){
			if(union.containsAll(_f2.set) && !_f2.set.containsAll(temp2))
				return false;
		}
		long endTime = System.currentTimeMillis();
		Judge_time += endTime - startTime;
		return true;
	}
	public Set<Fault> Reduction(Set<Fault> set){
		long startTime=System.currentTimeMillis();
		Set<Fault> result = new HashSet<>();
		int n1 = 0;
		for(Fault f1:set){
				boolean tag = true;
				int n2 = 0;
				for(Fault f2:set){
					if(n1 != n2 && f1.set.containsAll(f2.set)){
						tag = false;
						System.out.println("contains:" + f1.set + "," + f2.set);
						break;
					}
					n2 ++;
				}
				//System.out.println(tag);
				if(tag)
					result.add(f1);
			n1 ++;
		}
		long endTime=System.currentTimeMillis();
		Redunc_time += endTime - startTime;
		return result;
		
	}
}
