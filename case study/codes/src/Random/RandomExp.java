package Random;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import Basic.Fault;
import Basic.RandomFaultSet;
import CreateToken.JWT;
import FBFI.Main;
import GenQ.Q;


public class RandomExp {
	public static void main(String[] args) throws Exception {
		Main m = new Main();
		Set<Set<String>> Bottlenecks = new HashSet<>();
		//初始化Bottlenecks
		Set<String> temp = new HashSet<>();
		temp.add("quickstart-docker-compose_ts-travel-service_1");
		temp.add("quickstart-docker-compose_ts-travel-service_2");
		temp.add("quickstart-docker-compose_ts-travel-service_3");
		Bottlenecks.add(temp);
		temp = new HashSet<>();
		temp.add("quickstart-docker-compose_ts-assurance-service_1");
		temp.add("quickstart-docker-compose_ts-assurance-service_2");
		temp.add("quickstart-docker-compose_ts-assurance-service_3");
		Bottlenecks.add(temp);
		temp = new HashSet<>();
		temp.add("quickstart-docker-compose_ts-food-service_1");
		temp.add("quickstart-docker-compose_ts-food-service_2");
		Bottlenecks.add(temp);
		temp = new HashSet<>();
		temp.add("quickstart-docker-compose_ts-contacts-service_1");
		temp.add("quickstart-docker-compose_ts-contacts-service_2");
		Bottlenecks.add(temp);
		temp = new HashSet<>();
		temp.add("quickstart-docker-compose_ts-preserve-service_1");
		temp.add("quickstart-docker-compose_ts-preserve-service_2");
		temp.add("quickstart-docker-compose_ts-preserve-service_3");
		Bottlenecks.add(temp);
		temp = new HashSet<>();
		temp.add("quickstart-docker-compose_ts-order-service_1");
		temp.add("quickstart-docker-compose_ts-order-service_2");
		Bottlenecks.add(temp);
		temp = new HashSet<>();
		temp.add("quickstart-docker-compose_ts-inside-payment-service_1");
		temp.add("quickstart-docker-compose_ts-inside-payment-service_2");
		Bottlenecks.add(temp);
		temp = new HashSet<>();
		temp.add("quickstart-docker-compose_ts-order-other-service_1");
		temp.add("quickstart-docker-compose_ts-order-other-service_2");
		temp.add("quickstart-docker-compose_ts-order-other-service_3");
		Bottlenecks.add(temp);
		//////////////////////////////
		int size = 1;
		int times = 1;
		if(args.length > 0)
			size = Integer.parseInt(args[0]);
		if(args.length > 1)
			times = Integer.parseInt(args[1]);
		/////////////////////
		Random r = new Random();
		int bottlenecks_count = 0;
		int failcases_count = 0;
		long time_count = 0;
		int node_count = 0;
		int edge_count = 0;
		int faultlength = 0;
		for(int i = 0;i<times;i++){
			Thread.sleep(2*60*1000);
			long time1 = System.currentTimeMillis();
			int failno = 0;
			RandomFaultSet rfs = new RandomFaultSet();
			
			for(int j = 0;j<size;j++){
				//恢复系统
				System.out.println("Reset system...");
				m.RestoreFaults(faultlength);
				//生成用例并执行
				int t = 1;
				if(rfs.nodes.size() > 0)
					t = r.nextInt(rfs.nodes.size()) + 1;
				Fault fault = rfs.getRandomFault(t);
				faultlength = fault.set.size();
				m.InsertFaults(fault.set);
				//执行用例
				JWT jwt = new JWT();
				String auth = jwt.createToken();
				if(m.Execution(auth)){
					System.out.println("Execution is successful!");
					String path = m.ExecutionPath();		//追踪调用链
					System.out.println("path = " + path);
					rfs.AddTrace(path);
				}else{
					failno ++;
					System.out.println("Execution is failed!");
					rfs.validfaults.add(fault);
				}
			}
			long time2 = System.currentTimeMillis();
			time_count += (time2 - time1);
			failcases_count += failno;
			Set<Set<String>> detectedbottlenecks = new HashSet<>();
			for(Set<String> set:Bottlenecks){
				if(rfs.validfaults.containsAll(set))
					detectedbottlenecks.add(set);
			}
			bottlenecks_count += detectedbottlenecks.size();
			node_count += rfs.nodes.size();
			edge_count += rfs.edges.size();
		}
		Q q = new Q();
		System.out.println("Size=" + size + ",random_times=" + times + ",average_time=" + q.df.format((double)time_count/(1000*times)) + "s" + ",average_failcase=" + q.df.format((double)failcases_count/times) + ",nodes=" + q.df.format((double)node_count/times) + ",edges=" + q.df.format((double)edge_count/times) + ",average_bottlenecks=" + q.df.format((double)bottlenecks_count/times));
		//restart system
		Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "cd ../train-ticket" }, null);
		Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "sh restartTrainWeb.sh" }, null);
	}
}
