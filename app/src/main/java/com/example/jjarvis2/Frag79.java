package com.example.jjarvis2;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;

import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frag79 extends Fragment {
    View view;
    EditText sched_name;
    ArrayList<String> list;
    Button set_button;
    private DatabaseReference mDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static Frag79 newInstance(ArrayList<String> list) {
        Frag79 frag79 = new Frag79();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("CheckedList", list);
        frag79.setArguments(bundle);

        return frag79;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            list = getArguments().getStringArrayList("CheckedList");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag79, container, false);
        sched_name = view.findViewById(R.id.schedule_set_button);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d("Frag79", list.get(0));

        CalendarView mCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        mCalendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);

        mCalendarView.setSelectionType(SelectionType.RANGE);

        /*
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() // 날짜 선택 이벤트
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                String date = year + "/" + (month + 1) + "/" + dayOfMonth;
                whenDate.setText(date); // 선택한 날짜로 설정

            }
        });*/
        set_button=(Button)view.findViewById(R.id.set_button);
        set_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    List<Calendar> days = mCalendarView.getSelectedDates();
                    if(sched_name.getText().toString().getBytes().length != 0 && days.size() >= 1){
                    String result="";
                    Calendar calendar = days.get(0);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    String week = new SimpleDateFormat("EE").format(calendar.getTime());
                    String day_full = year + "년 "+ (month+1)  + "월 " + day + "일 ";
                    result += (day_full + "~ ");
                    calendar = days.get(days.size()-1);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    month = calendar.get(Calendar.MONTH);
                    year = calendar.get(Calendar.YEAR);
                    week = new SimpleDateFormat("EE").format(calendar.getTime());
                    day_full = year + "년 "+ (month+1)  + "월 " + day + "일 ";
                    result += (day_full);
                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();

                    String userUid = user.getUid();
                    Map<String,Object> time = new HashMap<>();
                    time.put("START",days.get(0).get(Calendar.YEAR)+"년 "+(days.get(0).get(Calendar.MONTH)+1)+"월 "+days.get(0).get(Calendar.DAY_OF_MONTH)+"일 ");
                    time.put("END",days.get(days.size()-1).get(Calendar.YEAR)+"년 "+(days.get(days.size()-1).get(Calendar.MONTH)+1)+"월 "+days.get(days.size()-1).get(Calendar.DAY_OF_MONTH)+"일 ");
                    for(String i : list){
                        time.put(i,0);
                    }
                    mDatabase.child("userdata").child(userUid).child("MyList").child(String.valueOf(sched_name.getText())).setValue(time);
                    Map<String,Object> cart = new HashMap<>();
                    int i = 0;
                    for(String s : list){
                        cart.put(s,i++);
                    }
                    for(Calendar c : days){
                        exec e = new exec(c.get(Calendar.YEAR)*10000+(c.get(Calendar.MONTH)+1)*100+c.get(Calendar.DAY_OF_MONTH),cart);
                        mDatabase.child("userdata").child(userUid).child(String.valueOf(e.year())).child(String.valueOf(e.week())).child(String.valueOf(e.getDate())).child("exercise").setValue(e.getExercise());
                    }

                    Toast toast = Toast.makeText(getActivity(), "스케줄이 등록 되었습니다.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 330);
                    toast.show();
                    for (String s : list){
                        mDatabase.child("userdata").child(user.getUid()).child("cart").child(s).removeValue();
                    }

                    ((SubActivity)getActivity()).setFrag(78);
                }
                else{
                    Toast toast = Toast.makeText(getActivity(), "기간과 스케줄 이름을 확인하세요.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 330);
                    toast.show();
                }
            }
        });
        return view;
    }
}