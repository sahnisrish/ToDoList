package com.example.sahni.todolist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import static com.example.sahni.todolist.Constant.*;

/**
 * Created by sahni on 16/3/18.
 */

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id=intent.getExtras().getInt(Constant.ID_KEY);
        String title = null;
        ItemOpenHelper openHelper=ItemOpenHelper.getInstance(context);
        SQLiteDatabase database=openHelper.getReadableDatabase();
        Cursor cursor=database.query(Contract.ItemList.TABLE_NAME, new String[]{Contract.ItemList.ITEM},Contract.ItemList.ID+"=?", new String[]{id + ""},null,null,null);
        if(cursor.moveToNext())
            title=cursor.getString(cursor.getColumnIndex(Contract.ItemList.ITEM));
        database.close();

        Intent notificationIntent=new Intent(context,DispalyItem.class);
        Bundle bundle=new Bundle();
        bundle.putInt(Constant.ID_KEY,id);
        bundle.putBoolean(Constant.FROM_NOTIFICATION,true);
        notificationIntent.putExtras(bundle);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,Constant.REQUEST_VIEW,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // only for gingerbread and newer versions
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"TASKS", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle("Pending Task");
        builder.setContentText(title+" is pending.");
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        Notification notification=builder.build();
        manager.notify(id,notification);

        Log.e("ID", "onReceive: "+notificationIntent.getExtras().getInt(Constant.ID_KEY)+" "+title);
    }
}
