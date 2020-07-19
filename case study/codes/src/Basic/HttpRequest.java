package Basic;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class HttpRequest {
	public int ResponseCode;
	public String Authorization;
	public String response = "";
	public HttpURLConnection conn;
	public HttpRequest(String auth){
		//String auth = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZHNlX21pY3Jvc2VydmljZSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpZCI6IjRkMmE0NmM3LTcxY2ItNGNmMS1iNWJiLWI2ODQwNmQ5ZGE2ZiIsImlhdCI6MTU5NDMzODQ3OCwiZXhwIjoxNTk0MzQyMDc4fQ.fMuXzPdXuWaefQf4foM9dsKqq7ydRvROedah6rLdz4A";
		Authorization = "Bearer " + auth;
		//Authorization = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZHNlX21pY3Jvc2VydmljZSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpZCI6IjRkMmE0NmM3LTcxY2ItNGNmMS1iNWJiLWI2ODQwNmQ5ZGE2ZiIsImlhdCI6MTU5NDMzODQ3OCwiZXhwIjoxNTk0MzQyMDc4fQ.fMuXzPdXuWaefQf4foM9dsKqq7ydRvROedah6rLdz4A";
	}
	public void doPost(String URL,String jsonStr){
	        OutputStreamWriter out = null;
	        BufferedReader in = null;
	     //   StringBuilder result = new StringBuilder();
	        conn = null;
	        try{
	            URL url = new URL(URL);
	            conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            //����POST�����������Ϊtrue
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            //��֤
	            conn.addRequestProperty("Authorization", Authorization);
	            //�������ӳ�ʱʱ��Ͷ�ȡ��ʱʱ��
	            conn.setConnectTimeout(30000);
	            conn.setReadTimeout(60000);
	            conn.setRequestProperty("Content-Type", "application/json");
	            conn.setRequestProperty("Accept", "application/json");
	            //��ȡ�����
	            out = new OutputStreamWriter(conn.getOutputStream());
	            
	            out.write(jsonStr);
	            out.flush();
	            out.close();
	            //ȡ������������ʹ��Reader��ȡ
	            ResponseCode = conn.getResponseCode();
	            response = "";
	            if (ResponseCode == 200){
	                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	                String result = "";
	                String line;
	                while ((line = in.readLine()) != null){
	                    result += line;
	                }
	                response = result;
	            }else{
	                System.out.println("ResponseCode is an error code:" + conn.getResponseCode());
	            }
	        }catch (Exception e){
	            e.printStackTrace();
	        }finally {
	            try{
	                if(out != null){
	                    out.close();
	                }
	                if(in != null){
	                    in.close();
	                }
	            }catch (IOException ioe){
	                ioe.printStackTrace();
	            }
	            
	            conn.disconnect();
	            //System.out.println("post disconnect");
	        }
	    }
     public void doGet(String URL){
	        conn = null;
	        InputStream is = null;
	        BufferedReader br = null;
	     //   StringBuilder result = new StringBuilder();
	        try{
	            //����Զ��url���Ӷ���
	            URL url = new URL(URL);
	            //ͨ��Զ��url���Ӷ����һ�����ӣ�ǿת��HTTPURLConnection��
	            conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            //��֤
	            conn.addRequestProperty("Authorization", Authorization);
	            //�������ӳ�ʱʱ��Ͷ�ȡ��ʱʱ��
	            conn.setConnectTimeout(15000);
	            conn.setReadTimeout(60000);
	            conn.setRequestProperty("Accept", "application/json");
	            //��������
	            conn.connect();
	            //ͨ��connȡ������������ʹ��Reader��ȡ
	            ResponseCode = conn.getResponseCode();
	            response = "";
	            if (ResponseCode == 200){
	                is = conn.getInputStream();
	                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	                String result = "";
	                String line;
	                while ((line = br.readLine()) != null){
	                	//System.out.println(line);
	                    result += line;
	                }
	                response = result;
	            }else{
	                System.out.println("ResponseCode is an error code:" + conn.getResponseCode());
	            }
	        }catch (MalformedURLException e){
	            e.printStackTrace();
	        }catch (IOException e){
	            e.printStackTrace();
	        }catch (Exception e){
	            e.printStackTrace();
	        }finally {
	            try{
	                if(br != null){
	                    br.close();
	                }
	                if(is != null){
	                    is.close();
	                }
	            }catch (IOException ioe){
	                ioe.printStackTrace();
	            }
	            conn.disconnect();
	            //System.out.println("get disconnect");
	        }
	        //return result.toString();
	    }


}
