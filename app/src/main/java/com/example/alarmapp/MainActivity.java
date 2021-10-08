package com.example.alarmapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.alarmapp.Interface.IClickAlarm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    AlarmManager alarmManager;
    RecyclerView rv_Alarm;
    TextView btnAddAlarmActivity;
    ArrayList<Alarm> listAlarm;
    ItemTouchHelper touchHelper;
    RecyclerViewAdapterAlarmCustom myAdapter;
    Calendar time;
    SharedPreferences save;
    SharedPreferences.Editor editor;
    Gson gson;
    public static int REQUEST_CORE = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepare();
        getDaTa();
        //clear();
    }
    private void clear(){
        editor.clear();
        editor.commit();
    }
    private void prepare(){
        listAlarm = new ArrayList<>();
        rv_Alarm = (RecyclerView) findViewById(R.id.rv_Alarm);
        btnAddAlarmActivity = (TextView) findViewById(R.id.btn_AddAlarmAc);
        btnAddAlarmActivity.setOnClickListener(this);
        alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        time = Calendar.getInstance();
        gson = new Gson();
        save = PreferenceManager.getDefaultSharedPreferences(this);
        editor = save.edit();
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_Alarm.setLayoutManager(layout);
        rv_Alarm.setHasFixedSize(true);
        rv_Alarm.addItemDecoration(decoration);

        touchHelper =new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if(listAlarm.get(position).isTurnOn()){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,listAlarm.get(position).getId(),listAlarm.get(position).getIntent(),0);
                    alarmManager.cancel(pendingIntent);
                }
                listAlarm.remove(position);
                saveData();
                onResume();
            }
        });
    }
    private void getDaTa(){
        Type type = new TypeToken<ArrayList<Alarm>>(){}.getType();
        if(save.getString("DATA","") != ""){
            String DATA = save.getString("DATA","");
            listAlarm = gson.fromJson(DATA,type);
        }
    }
    private void saveData(){
        String data = gson.toJson(listAlarm);
        editor.putString("DATA",data);
        editor.commit();
    }
    @Override
    public void onClick(View view){
        if(view == btnAddAlarmActivity) {
            Intent intent =new Intent(MainActivity.this,SetAlarmActivity.class);
            intent.putExtra("PASS_LIST_ALARM",listAlarm);
            startActivityForResult(intent,REQUEST_CORE);
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
            myAdapter = new RecyclerViewAdapterAlarmCustom(listAlarm, new IClickAlarm() {
                @Override
                public void deleteAlarm(Alarm alarm){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,alarm.getId(),alarm.getIntent(),0);
                    alarmManager.cancel(pendingIntent );
                    listAlarm.remove(alarm);
                }
                @Override
                public void turnOnAlarm(Alarm alarm) {
                    Intent actionIntent = new Intent(MainActivity.this,Receiver.class);
                    time.set(Calendar.HOUR_OF_DAY,alarm.getHour());
                    time.set(Calendar.MINUTE,alarm.getMinute());
                    time.set(Calendar.SECOND,0);
                    alarm.setCalendar(time);
                    alarm.setIntent(actionIntent);
                    alarm.setTurnOn(true);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,alarm.getId(),alarm.getIntent(),0);
                    if(alarm.getCalendar().getTimeInMillis() < System.currentTimeMillis()) {
                        //setRepeating :  lặp lại hằng ngày ,không chính xác tuyệt đối , tắt mở liên tục thời gian chênh lệch lớn
                        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,alarm.getCalendar().getTimeInMillis()+24*60*60*100,24*60*60*1000,pendingIntent);
                        //setExact : giờ chính xác , reo từ 0 giây
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,alarm.getCalendar().getTimeInMillis()+24*60*60*1000,pendingIntent);
                    }
                    else {
                        //setRepeating :  lặp lại hằng ngày ,không chính xác tuyệt đối , tắt mở liên tục thời gian chênh lệch lớn
                        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,alarm.getCalendar().getTimeInMillis(),24*60*60*1000,pendingIntent);
                        //setExact : giờ chính xác , reo từ 0 giây
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,alarm.getCalendar().getTimeInMillis(),pendingIntent);
                    }
                    saveData();
                }
                @Override
                public void turnOffAlarm(Alarm alarm){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,alarm.getId(),alarm.getIntent(),0);
                    alarmManager.cancel(pendingIntent);
                    alarm.setCalendar(null);
                    alarm.setTurnOn(false);
                    saveData();
                }
            },this);
            rv_Alarm.setAdapter(myAdapter);
            touchHelper.attachToRecyclerView(rv_Alarm);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CORE)
        {
            Alarm alarm = data.getExtras().getParcelable("RETURN_ALARM");
            listAlarm.add(alarm);
            saveData();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
class RecyclerViewAdapterAlarmCustom extends RecyclerView.Adapter<RecyclerViewAdapterAlarmCustom.AlarmViewHolder>{
    private ArrayList<Alarm> listAlarm;
    private IClickAlarm IClickAlarm;
    private Activity acc;
    RecyclerViewAdapterAlarmCustom(ArrayList<Alarm> list, IClickAlarm IClickAlarm,Activity acc){
        this.listAlarm = list;
        this.IClickAlarm =  IClickAlarm;
        this.acc = acc;
    }
    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item,null);
        return new AlarmViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        int i = position;
        if(listAlarm.get(i).getHour() < 10){
            holder.hour.setText("0"+String.valueOf(listAlarm.get(i).getHour()));
        }
        else holder.hour.setText(String.valueOf(listAlarm.get(position).getHour()));
        if(listAlarm.get(i).getMinute() < 10){
            holder.minute.setText("0"+String.valueOf(listAlarm.get(i).getMinute()));
        }
        else holder.minute.setText(String.valueOf(listAlarm.get(position).getMinute()));
        if(listAlarm.get(i).isTurnOn())
        {
            holder.activeAlarm.setChecked(true);
        }
        holder.activeAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.activeAlarm.isChecked()==true){
                    IClickAlarm.turnOnAlarm(listAlarm.get(i));
                }
                else{
                    IClickAlarm.turnOffAlarm(listAlarm.get(i));
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return listAlarm != null ? listAlarm.size() : 0;
    }
    class AlarmViewHolder extends RecyclerView.ViewHolder{
        TextView hour;
        TextView minute;
        Switch activeAlarm;
        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            this.hour = (TextView)itemView.findViewById(R.id.tv_hour);
            this.minute = (TextView)itemView.findViewById(R.id.tv_minute);
            this.activeAlarm = (Switch) itemView.findViewById(R.id.switch_active);
        }
    }
}


