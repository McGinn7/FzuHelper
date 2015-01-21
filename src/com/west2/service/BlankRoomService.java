package com.west2.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.west2.domain.BlankRoom;
import com.west2.main.R;
import com.west2.utils.HttpUtils;


public class BlankRoomService {
	private Context context;
	public BlankRoomService(Context context){
		this.context = context;
	}
	public BlankRoom getBlankRoom(int timeIndex, int siteIndex) {
		String url = context.getResources().getString(R.string.url_blank)
				+ "key=" + siteIndex + "w" + timeIndex;
		Log.e("","url = " + url);
		String res = HttpUtils.getData(url);
		Log.e("", "res = " + res);
		BlankRoom blankRoom = new BlankRoom();
		if (res != null) {
			try {
				JSONObject jsonObject = new JSONObject(res);
				if (jsonObject != null) {
					blankRoom.setMsg(jsonObject.getString("roomMsg"));
					blankRoom.setSiteIndex(siteIndex);
					blankRoom.setTimeIndex(timeIndex);
					String room = jsonObject.getString("roomArr");
					if (room != null) {
						room = room.replace("[", "").replace("\"", "")
								.replace("]", "");
						String[] rooms = room.split(",");
						int length = 5;
						StringBuffer[] sbs = new StringBuffer[length];
						for (int i = 0; i < sbs.length; i++) {
							sbs[i] = new StringBuffer();
						}
						for (int i = 0; i < rooms.length; i++) {
							if (rooms[i].length() > 0
									&& Character.isDigit(rooms[i].charAt(0))) {
								int index = Integer.parseInt(rooms[i]) / 100 - 1;
								if (index < 0) {
									index = 0;
								}
								sbs[index].append(rooms[i] + "  ");
							}
						}
						blankRoom.setRooms(sbs);
						return blankRoom;
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new BlankRoom();
			}
		}
		return new BlankRoom();
	}
}
