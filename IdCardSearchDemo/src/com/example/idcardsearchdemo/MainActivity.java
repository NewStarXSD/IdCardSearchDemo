package com.example.idcardsearchdemo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String DEF_CHATSET = "UTF-8";
	public static final int DEF_CONN_TIMEOUT = 30000;
	public static final int DEF_READ_TIMEOUT = 30000;
	public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
	public static final int SHOW_RESPONSE = 0;
	// 配置您申请的KEY
	public static final String APPKEY = "f1b72cadc3b8a3529ed25fb42fb2c3ea";

	private Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case SHOW_RESPONSE:
				String result = (String) msg.obj;
				Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button okButton = (Button) findViewById(R.id.ok);

		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				EditText idCard = (EditText) findViewById(R.id.idcard);
				String idcard = idCard.getText().toString();
				getRequest1(idcard);
			}
		});
	}

	public void getRequest1(final String a){
		new Thread(new Runnable() {
			public void run() {
				 String result =null;
			        String url ="http://apis.juhe.cn/idcard/index";//请求接口地址
			        Map<String, Object> params = new HashMap<String, Object>();//请求参数
			            params.put("cardno",a);//身份证号码
			            params.put("dtype","");//返回数据格式：json或xml,默认json
			            params.put("key",APPKEY);//你申请的key
			 
			        try {
			            result =net(url, params, "GET");
			            JSONObject object = new JSONObject(result);
			            if(object.getInt("error_code")==0){
			            	Message message = new Message();
			            	message.what = SHOW_RESPONSE;
			            	message.obj = object.get("result").toString();
			            	handler.sendMessage(message);
			                System.out.println(object.get("result"));
			            }else{
			                System.out.println(object.get("error_code")+":"+object.get("reason"));
			            }
			        } catch (Exception e) {
			            	Log.e("123","12343124");
			            e.printStackTrace();
			        }
			}
		}).start();
       
    }
	
	public static String net(String strUrl, Map<String, Object> params,
			String method) throws Exception {
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		String rs = null;
		try {
			StringBuffer sb = new StringBuffer();
			if (method == null || method.equals("GET")) {
				strUrl = strUrl + "?" + urlencode(params);
			}
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (method == null || method.equals("GET")) {
				conn.setRequestMethod("GET");
			} else {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
			}
			conn.setRequestProperty("User-agent", userAgent);
			conn.setUseCaches(false);
			conn.setConnectTimeout(DEF_CONN_TIMEOUT);
			conn.setReadTimeout(DEF_READ_TIMEOUT);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			if (params != null && method.equals("POST")) {
				try (DataOutputStream out = new DataOutputStream(
						conn.getOutputStream())) {
					out.writeBytes(urlencode(params));
				}
			}
			InputStream is = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sb.append(strRead);
			}
			rs = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return rs;
	}

	public static String urlencode(Map<String, ?> data) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, ?> i : data.entrySet()) {
			try {
				sb.append(i.getKey()).append("=")
						.append(URLEncoder.encode(i.getValue() + "", "UTF-8"))
						.append("&");
			} catch (UnsupportedEncodingException e) {
			Log.e("not","why");
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
