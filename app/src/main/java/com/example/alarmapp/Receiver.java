package com.example.alarmapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
public class Receiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Receiver", "Log Receiver");
        Intent actionIntent = new Intent(context, AlarmService.class);// chuyển báo thức đến AlarmService để hiển thị notification
        actionIntent.putExtra("ACTION_ALARM","START");
        context.startForegroundService(actionIntent);
    }
}
