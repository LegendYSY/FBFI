package FBFI;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import Basic.HttpRequest;
import Basic.SolvingBottleNeckByPath;
import CreateToken.JWT;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
public class Main {
	public static String[] services = {"ts-travel-service","ts-assurance-service","ts-food-service","ts-contacts-service","ts-preserve-service","ts-order-service","ts-inside-payment-service","ts-order-other-service"};
	public Set<String> InsertServices = new HashSet<>();
	public void InsertFaults(Set<String> testcase){
		try {
			System.out.println("Testcase is '" + testcase + "'");
			System.out.println("Insert faults...");
			InsertServices = new HashSet<>();
			for(String serviceInstance : testcase){
				if(serviceInstance.startsWith("Crash"))
					serviceInstance = serviceInstance.substring(6,serviceInstance.length() - 1);
				//String ser = serviceInstance.substring(6,serviceInstance.length() - 1);
				for(String str:services){
					if(serviceInstance.indexOf(str) > 0){
						InsertServices.add(str);
						break;
					}
				}
				Process ps = Runtime.getRuntime().exec("docker stop " + serviceInstance);
				BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
			    String line = null;
			    while ((line = br.readLine()) != null) {
			    	 //System.out.println(line);
			    }
			    System.out.println("stop " + serviceInstance + " success");
			}
		} catch (IOException e) {
			 System.out.println("Insert faults failed");
		}
		
	}
	public void RestoreFaults(int testcaseLength){
		try {
			/*for(String service : InsertServices){
				Process ps = Runtime.getRuntime().exec("docker-compose -f deployment/quickstart-docker-compose/quickstart-docker-compose.yml start " + service);
				BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
			    String line = null;
			    while ((line = br.readLine()) != null) {
			    	 //System.out.println(line);
			    }
			    Thread.sleep(40*1000);
			    System.out.println("restart " + service + " success");
			}*/
			/*Process ps = Runtime.getRuntime().exec("../Desktop/restartTrainWeb.sh");

			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
		    String line = null;
		    while ((line = br.readLine()) != null) {
		    	System.out.println(line);
		    	System.out.println(line.length());
		    	System.out.println(ps.exitValue());
		    }*/
			Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "cd ../train-ticket" }, null);
			Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "sh restartTrainWeb.sh" }, null);
			System.out.println("testcaseLength=" + testcaseLength);
			System.out.println("cost"+(testcaseLength+2)*5+"s");
			Thread.sleep((testcaseLength+2)*5*1000);
			System.out.println("restart success");
		} catch (Exception e) {
			System.out.println("Reset system failed");
		}
	}
	public boolean Execution(String auth) throws Exception{
		boolean tag = true;
		HttpRequest request = new HttpRequest(auth);
		String url;
		String para;
		//travel service
		url = "http://localhost:8080/api/v1/travelservice/trips/left";
		para = "{\"departureTime\":\"2020-07-20\", \"endPlace\":\"Su Zhou\", \"startingPlace\":\"Shang Hai\"}";
		request.doPost(url,para);
		if(request.ResponseCode != 200){
			System.out.println("travelservice failed,resondeCode is " + request.ResponseCode + ",response is " + request.response);
			return false;
		}
		System.out.println("travel service is true");
		Thread.sleep(500);
		//assurance service
		url = "http://localhost:8080/api/v1/assuranceservice/assurances/types";
		request.doGet(url);
		if(request.ResponseCode != 200){
			System.out.println("assuranceservice failed,resondeCode is " + request.ResponseCode + ",response is " + request.response);
			return false;
		}
		System.out.println("assurance service is true");
		Thread.sleep(500);
		//	System.out.println(request.response);
		//food service
		url = "http://localhost:8080/api/v1/foodservice/foods/2020-07-15/Shang%20Hai/Su%20Zhou/D1345";
		request.doGet(url);
		if(request.ResponseCode != 200){
			System.out.println("foodservice failed,resondeCode is " + request.ResponseCode + ",response is " + request.response);
			return false;
		}
		System.out.println("food service is true");
		Thread.sleep(500);
		//contact service
		url = "http://localhost:8080/api/v1/contactservice/contacts/account/4d2a46c7-71cb-4cf1-b5bb-b68406d9da6f";
		request.doGet(url);
		if(request.ResponseCode != 200){
			System.out.println("contactservice failed,resondeCode is " + request.ResponseCode + ",response is " + request.response);
			return false;
		}
		System.out.println("contact service is true");
		Thread.sleep(500);
		//preserve service
		url = "http://localhost:8080/api/v1/preserveservice/preserve";
		para = "{\"accountId\":\"4d2a46c7-71cb-4cf1-b5bb-b68406d9da6f\",\"contactsId\":\"2c667269-09fa-4bf0-8fa7-fcfdef9dac0e\",\"tripId\":\"D1345\",\"seatType\":\"2\",\"date\":\"2020-07-20\",\"from\":\"Shang Hai\",\"to\":\"Su Zhou\",\"assurance\":\"0\",\"foodType\":1,\"foodName\":\"Bone Soup\",\"foodPrice\":2.5,\"stationName\":\"\",\"storeName\":\"\"}";
		request.doPost(url,para);
		if(request.ResponseCode != 200){
			System.out.println("preserveservice failed,resondeCode is " + request.ResponseCode + ",response is " + request.response);
			return false;
		}
		System.out.println("preserve service is true");
		Thread.sleep(500);
		//order service
		String orderid = "";
		url = "http://localhost:8080/api/v1/orderservice/order/refresh";
		para = "{\"loginId\":\"4d2a46c7-71cb-4cf1-b5bb-b68406d9da6f\",\"enableStateQuery\":false,\"enableTravelDateQuery\":false,\"enableBoughtDateQuery\":false,\"travelDateStart\":null,\"travelDateEnd\":null,\"boughtDateStart\":null,\"boughtDateEnd\":null}";
		request.doPost(url,para);
		if(request.ResponseCode != 200){
			System.out.println("orderservice failed,resondeCode is " + request.ResponseCode + ",response is " + request.response);
			return false;
		}else{
			//获取
			JSONObject response = JSONObject.fromObject(request.response);

			if(response.containsKey("data")){
				JSONArray orders = JSONArray.fromObject(response.get("data"));
				JSONObject latestorder = orders.getJSONObject(orders.size() - 1);
				//System.out.println(latestorder);
				orderid = (String)latestorder.get("id");
				// System.out.println(latestorder.get("id"));
			}

		}
		System.out.println("order service is true");
		Thread.sleep(500);
		//order other service
		url = "http://localhost:8080/api/v1/orderOtherService/orderOther/refresh";
		para = "{\"loginId\":\"4d2a46c7-71cb-4cf1-b5bb-b68406d9da6f\",\"enableStateQuery\":false,\"enableTravelDateQuery\":false,\"enableBoughtDateQuery\":false,\"travelDateStart\":null,\"travelDateEnd\":null,\"boughtDateStart\":null,\"boughtDateEnd\":null}";
		request.doPost(url,para);
		if(request.ResponseCode != 200){
			System.out.println("orderotherservice failed,resondeCode is " + request.ResponseCode + ",response is " + request.response);
			return false;
		}
		System.out.println("order other service is true");
		Thread.sleep(500);
		//inside_pay_service
		url = "http://localhost:8080/api/v1/inside_pay_service/inside_payment";
		para = "{\"orderId\":\"" + orderid + "\",\"tripId\":\"D1345\"}";
		//System.out.println(para);
		request.doPost(url,para);
		if(request.ResponseCode != 200){
			System.out.println("insidepaymentservice failed,resondeCode is " + request.ResponseCode + ",response is " + request.response);
			return false;
		}
		System.out.println("inside payment service is true");
		Thread.sleep(500);
		return tag;
	}
	public String getServiceValue(String service) throws Exception{
		HttpRequest request = new HttpRequest("");
		//request.doGet("http://localhost:8080/api/v1/assuranceservice/assurances/types");
		long end = System.currentTimeMillis();
		long start = end - 3600000;
		String url = "http://localhost:16686/api/traces?end="+(end*1000)+"&limit=1&lookback=1h&maxDuration&minDuration&service=" + service + "&start="+(start*1000);
		request.doGet(url);
		Thread.sleep(500);
		//System.out.println("------" + service + "--------");
		//String result = request.response;
		//System.out.println(request.ResponseCode);
		//System.out.println(request.response);
		JSONObject result = JSONObject.fromObject(request.response);
		JSONArray data = JSONArray.fromObject(result.get("data"));
		if(data.size() > 0){
			JSONObject data_0 = data.getJSONObject(0);
			JSONObject p1 = JSONObject.fromObject(JSONObject.fromObject(data_0.get("processes")).get("p1"));
			//System.out.println("p1 = " + p1);
			if(p1.get("serviceName").equals(service)){
				JSONArray tags = JSONArray.fromObject(p1.get("tags"));
				//System.out.println("tags = " + tags);
				//System.out.println("tags.get(0) = " + tags.get(0));
				return JSONObject.fromObject(tags.get(0)).getString("value");
			}
			JSONObject p2 = JSONObject.fromObject(JSONObject.fromObject(data_0.get("processes")).get("p2"));
			//System.out.println("p2 = " + p2);
			if(p2.get("serviceName").equals(service)){
				JSONArray tags = JSONArray.fromObject(p2.get("tags"));
				//System.out.println("tags = " + tags);
				//System.out.println("tags.get(0) = " + tags.get(0));
				return JSONObject.fromObject(tags.get(0)).getString("value");
			}
		}
		return "";
	}
	public Map<String,String> getServiceByValue(){
		Map<String,String> map = new HashMap<>();
		BufferedReader br = null;
		try {
			Process ps = Runtime.getRuntime().exec("docker ps");
			br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
			String line = null;
			String temp = "";
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				String[] S = line.split("\\s+");
				map.put(S[0],S[S.length - 1]);
				//System.out.println(S[0] + "," + S[S.length - 1]);
			}
			// System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	public String ExecutionPath() throws Exception{
		String path = "";
		Map<String,String> map = getServiceByValue();
		for(String service:services){
			String value = getServiceValue(service);
		//	System.out.println("value = " + value);
			//System.out.println("*******");
			path += map.get(value) + "---";
		}
		path = path.substring(0, path.length() - 3);
		//System.out.println(path);
		return path;
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		long time1 = System.currentTimeMillis();
		Main m = new Main();
		SolvingBottleNeckByPath sb = new SolvingBottleNeckByPath(0);
		//System.out.println(m.Execution(args[0]));
		//m.ExecutionPath();
		Set<Set<String>> TestcaseSet = new HashSet<>();	//测试用例集
		TestcaseSet.add(new HashSet<>());
		Set<Set<String>> FailcaseSet = new HashSet<>();		//已测用例集
		int count = 1;
		int testcaseLength=0;
		while(true){
			JWT jwt = new JWT();
			String auth = jwt.createToken();
			////////////选取测试用例//////////////
			System.out.println("-----------" + count + "------------" + FailcaseSet.size() + "," + TestcaseSet.size());
			Set<String> testcase = new HashSet<>();
			boolean tag = false;
			for(Set<String> tc:TestcaseSet){
				if(!FailcaseSet.contains(tc)){
					testcase = tc;
					tag = true;
					break;
				}
			}
			if(!tag)
				break;
			///恢复系统正常
			System.out.println("Reset system...");
			m.RestoreFaults(testcaseLength);
			testcaseLength=testcase.size();
			/////////测试用例注入////////////
			System.out.println("Insert faults is " + testcase);
			m.InsertFaults(testcase);
			if(m.Execution(auth)){
				System.out.println("Execution is successful!");
				String path = m.ExecutionPath();		//追踪调用链
				System.out.println("path = " + path);
				sb.AddPath(path);
				TestcaseSet = sb.getCases();
				System.out.println("TestcaseSum = " + TestcaseSet.size());
			}else{
				System.out.println("Execution is failed!");
				FailcaseSet.add(testcase);
			}
			count++;
		}
		long time2 = System.currentTimeMillis() ;
		System. out.println("Total time is " + (time2 - time1)/1000 +"s");
		//restart system
		Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "cd ../train-ticket" }, null);
		Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "sh restartTrainWeb.sh" }, null);
	}




}