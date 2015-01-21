package com.west2.service;

import com.west2.domain.BootStartDemo;
import com.west2.main.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationService {
	public static void showMessage(Context context,int id,String content){
		String ns = context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(ns);
        //定义通知栏展现的内容信息
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "来自福大助手的消息";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
         
        //定义下拉通知栏时要展现的内容信息
        CharSequence contentTitle = content;
        Intent notificationIntent = new Intent();
        notificationIntent.setClass(context, BootStartDemo.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, "",
                contentIntent);
         
        //用mNotificationManager的notify方法通知用户生成标题栏消息通知
        mNotificationManager.notify(id, notification);
	}
}
