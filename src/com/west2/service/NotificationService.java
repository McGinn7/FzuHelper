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
        //����֪ͨ��չ�ֵ�������Ϣ
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "���Ը������ֵ���Ϣ";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
         
        //��������֪ͨ��ʱҪչ�ֵ�������Ϣ
        CharSequence contentTitle = content;
        Intent notificationIntent = new Intent();
        notificationIntent.setClass(context, BootStartDemo.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, "",
                contentIntent);
         
        //��mNotificationManager��notify����֪ͨ�û����ɱ�������Ϣ֪ͨ
        mNotificationManager.notify(id, notification);
	}
}
