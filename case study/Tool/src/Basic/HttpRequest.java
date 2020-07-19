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
	            //发送POST请求必须设置为true
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            //认证
	            conn.addRequestProperty("Authorization", Authorization);
	            //设置连接超时时间和读取超时时间
	            conn.setConnectTimeout(30000);
	            conn.setReadTimeout(60000);
	            conn.setRequestProperty("Content-Type", "application/json");
	            conn.setRequestProperty("Accept", "application/json");
	            //获取输出流
	            out = new OutputStreamWriter(conn.getOutputStream());
	            
	            out.write(jsonStr);
	            out.flush();
	            out.close();
	            //取得输入流，并使用Reader读取
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
	            //创建远程url连接对象
	            URL url = new URL(URL);
	            //通过远程url连接对象打开一个连接，强转成HTTPURLConnection类
	            conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            //认证
	            conn.addRequestProperty("Authorization", Authorization);
	            //设置连接超时时间和读取超时时间
	            conn.setConnectTimeout(15000);
	            conn.setReadTimeout(60000);
	            conn.setRequestProperty("Accept", "application/json");
	            //发送请求
	            conn.connect();
	            //通过conn取得输入流，并使用Reader读取
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
