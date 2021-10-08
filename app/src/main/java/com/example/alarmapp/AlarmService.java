package com.example.alarmapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {
    MediaPlayer player;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Service :" , "Service được tạo");
        player = MediaPlayer.create(this,R.raw.nhacchuong);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getExtras().getString("ACTION_ALARM");
        if(action.equals("START")){
            player.start();
            Log.e("Service :" , "Service start");
            displayNotification();
        }
        if(action.equals("SHUTDOWN")){
            player.pause();
            onDestroy();
        }
        return START_NOT_STICKY;
    }
    private void displayNotification() {
        Intent intent1 = new Intent(this,MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(this,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews view_notification = new RemoteViews(getPackageName(),R.layout.notification_alarm);
        view_notification.setTextViewText(R.id.tv_title_notification,".....");
        view_notification.setImageViewResource(R.id.image_alarm_notification,R.drawable.ic_action_logo_noti);
        view_notification.setOnClickPendingIntent(R.id.tv_stop, getPendingIntent());

        Notification notification = new NotificationCompat.Builder(this,NotificationChanel.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_action_logo_item)
                .setContentIntent(pending)
                .setSound(null)
                .setCustomContentView(view_notification)
                .build();
        startForeground( 1, notification);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, ReceiverShutdownAlarm.class);
        intent.putExtra("ACTION","SHUTDOWN");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent , PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
        stopForeground(true);
        stopSelf();
        Log.e("Service :" , "Service bị hủy");
    }
}
