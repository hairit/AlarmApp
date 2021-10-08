package com.example.alarmapp.Interface;
import com.example.alarmapp.Alarm;
public interface IClickAlarm {
    default void deleteAlarm(Alarm alarm){}
    default void turnOnAlarm(Alarm alarm){}
    default void turnOffAlarm(Alarm alarm){}
}
