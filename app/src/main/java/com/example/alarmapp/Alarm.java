package com.example.alarmapp;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
public class Alarm implements Parcelable {
    private int id;
    private int hour, minute;
    private boolean turnOn;
    private Calendar calendar;
    private Intent intent;

    public Alarm(int id, int hour, int minute) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.turnOn = false;
    }

    protected Alarm(Parcel in) {
        id = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        turnOn = in.readByte() != 0;
        intent = in.readParcelable(Intent.class.getClassLoader());
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(hour);
        parcel.writeInt(minute);
        parcel.writeByte((byte) (turnOn ? 1 : 0));
        parcel.writeParcelable(intent, i);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isTurnOn() {
        return turnOn;
    }

    public void setTurnOn(boolean turnOn) {
        this.turnOn = turnOn;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
