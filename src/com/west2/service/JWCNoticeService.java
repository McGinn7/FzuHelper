package com.west2.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Context;

import com.west2.domain.JWCNotice;
import com.west2.main.R;
import com.west2.utils.HttpUtils;

public class JWCNoticeService {
	
	public static List<JWCNotice> getJWCNotices(Context context,int page){
		String url = context.getResources().getString(R.string.url_jwc_notice)
				+"page="+page;
		String res = HttpUtils.getData(url);
		List<JWCNotice> data = new ArrayList<JWCNotice>();
		if (res != null) {
			try {
				JSONArray jsonArray = new JSONArray(res);
				if (jsonArray != null) {
					
					for(int i = 0;i<jsonArray.length();i++){
						JSONObject jsonObject = jsonArray.optJSONObject(i);
						JWCNotice notice = new JWCNotice();
						
						notice.setTitle(jsonObject.getString("title"));
						notice.setDate(jsonObject.getString("date"));
						notice.setUrl(jsonObject.getString("url"));
					
						String isRed = jsonObject.getString("isRed");
						if("true".equals(isRed))
							notice.setRed(true);
						else
							notice.setRed(false);
						data.add(notice);
			
					}		
					return data;
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return data;  
			}
		}
		
		return data;
	}
	
	public static String getDetailFromUrl(String url){
		String html = HttpUtils.getData(url);
		if(html==null)  return null;
		Document doc = Jsoup.parse(html);
		if(doc==null) return null;
		String title,artbody;
		Elements links = doc.select("[id=artbody]");
		if(links==null) return null;
		artbody=links.outerHtml();
		String shortContent = links.text();
		links = doc.select("h1");
		title=links.outerHtml();

		return "<html><meta charset=\"UTF-8\">"+title+artbody+"</html>";
	}
	
	public static void getContent(List<JWCNotice> data,int s){
		if(data==null)
			return ;
		
		for(int i=s*3;i<(s*3+3)&&i<data.size();i++){
			JWCNotice notice = data.get(i);
			String url = notice.getUrl();
			String html = HttpUtils.getData(url);
			
			//Ê¹ÓÃjsoup½âÎöÍøÒ³
			try {
				String title;
				String artbody;
				Document doc = Jsoup.parse(html);
				Elements links = doc.select("[id=artbody]");
				artbody=links.outerHtml();
				String shortContent = links.text();
				links = doc.select("h1");
				title=links.outerHtml();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
}
