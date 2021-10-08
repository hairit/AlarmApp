package com.example.alarmapp;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
public class SetAlarmActivity extends AppCompatActivity implements View.OnClickListener{
    TextView btn_AddAlarm,btn_CancelAlarm,tv_hour , tv_minute;
    Random random;
    ArrayList<Alarm> listAlarm;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        listAlarm = getIntent().getExtras().getParcelableArrayList("PASS_LIST_ALARM");
        prepare();
    }
    private void prepare() {
        tv_hour = (TextView)findViewById(R.id.tv_hour);
        tv_minute = (TextView)findViewById(R.id.tv_minute);
        btn_AddAlarm=(TextView) findViewById(R.id.btn_AddAlarm);
        btn_CancelAlarm=(TextView) findViewById(R.id.btn_cancel);
        btn_CancelAlarm.setOnClickListener(this);
        btn_AddAlarm.setOnClickListener(this);
        random = new Random();
    }
    @Override
    public void onClick(View view){
        if(view == btn_AddAlarm){
            try {
                int hour = Integer.parseInt(tv_hour.getText().toString());
                int minute = Integer.parseInt(tv_minute.getText().toString());
                if ((0 <= hour && hour <= 23) && (0 <= minute && minute <= 59)) {
                    Alarm alarm = new Alarm(random.nextInt(), hour, minute);
                    if (checkExist(alarm)) {
                        Toast.makeText(this, "Trùng giờ", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent back = new Intent();
                        back.putExtra("RETURN_ALARM", alarm);
                        setResult(Activity.RESULT_OK, back);
                        finish();
                    }
                } else Toast.makeText(this, "Không hợp lệ", Toast.LENGTH_SHORT).show();
            }catch (Exception e) {
                Toast.makeText(this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
            }
        }
        if(view == btn_CancelAlarm){
            finish();
        }
    }
    public boolean checkExist(Alarm a){
        if(listAlarm.size()==0) return false;
        boolean result = false;
        for (Alarm item:
             listAlarm) {
            if(item.getHour() == a.getHour() && item.getMinute()==a.getMinute()) result= true;
        }
        return result;
    }
}