package com.example.alarmapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverShutdownAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent actionIntent = new Intent(context,AlarmService.class);
        actionIntent.putExtra("ACTION_ALARM","SHUTDOWN");
        context.startService(actionIntent);
    }
}
