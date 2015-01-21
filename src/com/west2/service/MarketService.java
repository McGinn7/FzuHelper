package com.west2.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceActivity.Header;
import android.provider.SyncStateContract.Constants;
import android.text.TextUtils;
import android.util.Log;

import com.west2.domain.MarketMessage;
import com.west2.main.R;
import com.west2.utils.HttpUtils;

public class MarketService {

	public static List<MarketMessage> getMarketMessage(Activity context,
			int page) {

		String url = context.getResources().getString(R.string.url_market)
				+ page;
		String res = HttpUtils.getData(url);
		List<MarketMessage> data = new ArrayList<MarketMessage>();
		if (res != null) {
			try {
				JSONArray jsonArray = new JSONArray(res);
				if (jsonArray != null) {

					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.optJSONObject(i);
						MarketMessage message = new MarketMessage();
						message.setTitle(jsonObject.getString("title"));
						message.setKind(jsonObject.getString("kind"));
						message.setAuthor(jsonObject.getString("author"));
						message.setTime(jsonObject.getString("time"));
						message.setUrl(jsonObject.getString("url"));
						message.setNum(Integer.parseInt(jsonObject
								.getString("num")));
						data.add(message);
					}

				}

				return data;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return data;
			}
		}

		return data;

	}

	/*
	 * 获取解析后的 二手市场详细页的html
	 */
	public static String getMarketHtml(Context context, String url) {
		
		String res = getData(context, url);
		String html = "";
		String head = "";
		String floors = "";

		if (res != null) {
			Document doc = Jsoup.parse(res);
			Elements links1;

			links1 = doc.select("[class=plc cl]"); 
			for(int i=0;i<links1.size()-1;i++){
				String t=links1.get(i).outerHtml();
				t=t.replace("<br />", "<br  >");
				t=t.replaceAll(" />", "/><br><br><br>");
				for(int p=0;p<3;p++)
					t=t.replaceFirst("<br>","");
				floors+=t;
				floors="<div>"+floors+"</div>";
				if (links1.get(i).getElementsByTag("img").size()>1)
					floors += "<br>";
			}
			
			
			//将图片的相对路径改为绝对路径并清楚部分属性标
			floors=Jsoup.clean(floors, "http://bbs.fzu.edu.cn/", Whitelist.relaxed());
			html="<html>"
					+ "<head>"
					+ "<style>"
					+ "img{float:left}"
					+ " </style>"
					+ "</head><body>"+floors
					+"</body></html>";
			
			html=html.replace("li", "p");

			Log.e("",html.replace("\n", ""));
			return html;
		}
		return html;

	}

	
	/*
	 * return cookie;
	 */
	private static String getCookie(Context context) {
		String cookie=null;
		String sessionTime=null;
		SharedPreferences preferences = context.getSharedPreferences(
				"MarketCookie", Context.MODE_PRIVATE);
	
		//从本地取cookie
		if(preferences!=null){
			cookie = preferences.getString("cookie", null);
			sessionTime = preferences.getString("sessionTime", null);
			if(cookie!=null&&sessionTime!=null
					   &&new Date(sessionTime).getTime()>new Date().getTime()){
				return cookie;
			}
			
		}
		
		//从网络取cookie保存到本地
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response;
		String url = "http://bbs.fzu.edu.cn/member.php?mod=logging&action=login&loginsubmit=yes&handlekey=login&loginhash=LdbnB";
		HttpPost httppost = new HttpPost(url);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username", "a120308720"));

			nameValuePairs.add(new BasicNameValuePair("password", "a357159"));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


			response = httpClient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获得响应
				
				List<Cookie> cookies = ((AbstractHttpClient) httpClient)
						.getCookieStore().getCookies();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < cookies.size(); i++) {
					Cookie cookie1 = cookies.get(i);
					String cookieName = cookie1.getName();
					String cookieValue = cookie1.getValue();
					if (!TextUtils.isEmpty(cookieName)
							&& !TextUtils.isEmpty(cookieValue)) {
						sb.append(cookieName + "=");
						sb.append(cookieValue + ";");
					}
				}
				sessionTime = cookies.get(0).getExpiryDate().toGMTString();
				//保存cookie
				preferences = context.getSharedPreferences(
						"MarketCookie", Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString("cookie", sb.toString());
				editor.putString("sessionTime", sessionTime);
				editor.commit();
				
				return sb.toString();

			}

		} catch (ClientProtocolException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
			httppost.abort();
			httpClient.getConnectionManager().shutdown();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	
	private static String getData(Context context, String url) {
	


		String cookie=getCookie(context);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		InputStream is = null;
		HttpGet get = null;
		BufferedReader reader = null;
		try {
			get = new HttpGet(url);
			
			get.setHeader("Cookie",cookie);
			
			
			HttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// 获得响应的字符集编码信息
				String charset = EntityUtils.getContentCharSet(entity);
				if (charset == null) {
					charset = "GB2312";
				}
				is = entity.getContent();
				StringBuffer result = new StringBuffer();
				String line = null;
				reader = new BufferedReader(new InputStreamReader(is, charset));
				while ((line = reader.readLine()) != null) {
					result.append(line);
				}
				return result.toString().trim();
				// return IOUtils.toString(is, charset);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				reader.close();
				is.close();
				get.abort();
				httpClient.getConnectionManager().shutdown();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;

	}

}
