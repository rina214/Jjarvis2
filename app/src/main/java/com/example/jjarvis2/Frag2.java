package com.example.jjarvis2;

import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Frag2 extends Fragment {
    int count;
    int width, height;
    int top,right,left,bottom;
    LinearLayout list_item;

    View view;
    DatabaseReference db;
    String userUid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag2, container, false);
        list_item = (LinearLayout)view.findViewById(R.id.list_item);
        CalendarView mCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        final TextView whenDate = (TextView) view.findViewById(R.id.whenDate);
        db = FirebaseDatabase.getInstance().getReference();
        userUid = user.getUid();
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() // 날짜 선택 이벤트
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {

                remove_category();
                count=0;
                db.child("userdata").child(userUid).child("MyList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d("snapshot", String.valueOf(snapshot.getValue()));
                            String starttime = null, endtime = null;
                            for (DataSnapshot sn : snapshot.getChildren()){
                                    if (sn.getKey().equals("START")){
                                    String[] temptime = sn.getValue(String.class).split(" ");
                                    starttime = temptime[0].replace("년", "");
                                    if(temptime[1].length() == 2) {
                                        starttime += "0";
                                        starttime += temptime[1].replace("월", "");
                                    } else
                                        starttime += temptime[1].replace("월", "");
                                    if(temptime[2].length() == 2) {
                                        starttime += "0";
                                        starttime += temptime[2].replace("일", "");
                                    } else
                                        starttime += temptime[2].replace("일", "");
                                }
                                else if(sn.getKey().equals("END")){
                                    String[] temptime = sn.getValue(String.class).split(" ");
                                    endtime = temptime[0].replace("년", "");
                                    if(temptime[1].length() == 2) {
                                        endtime += "0";
                                        endtime += temptime[1].replace("월", "");
                                    } else
                                        endtime += temptime[1].replace("월", "");
                                    if(temptime[2].length() == 2) {
                                        endtime += "0";
                                        endtime += temptime[2].replace("일", "");
                                    } else
                                        endtime += temptime[2].replace("일", ""); //형식을 맞춰준다.
                                }
                                else{

                                }
                            }

                            String result_month=" ", result_day= " ";
                            String clickTime = String.valueOf(year);
                            if (month + 1 < 10) {
                                clickTime += "0";
                                clickTime += (month + 1);
                                result_month += "0";
                                result_month += (month + 1);
                            } else{
                                clickTime += (month + 1);
                                result_month += (month + 1);
                            }
                            if (dayOfMonth < 10) {
                                clickTime += "0";
                                clickTime += dayOfMonth;
                                result_day += "0";
                                result_day +=dayOfMonth;
                            } else{
                                clickTime += dayOfMonth;
                                result_day +=dayOfMonth;
                            } //click time = 누른 곳의 날짜.


                            whenDate.setText(String.valueOf(year) + "/" + result_month + "/" +  result_day); // 선택한 날짜로 설정

                           // List<String> Array= new ArrayList<String>();
                            if ( Integer.parseInt(starttime) <=  Integer.parseInt(clickTime) &&  Integer.parseInt(clickTime) <=  Integer.parseInt(endtime) ) {
                                DynamicList(snapshot);
                            }

                            //Toast.makeText(getContext(), clickTime, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getContext(), snapshot.getKey(), Toast.LENGTH_SHORT).show();
                        }// for문 종료.

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        return view;
    }
    void remove_category(){
        for(int i=0; i < count; i++){
            list_item.removeViewInLayout(view.findViewById(i));
        }
    }

    private void DynamicList(DataSnapshot snapshot) {
        RelativeLayout childlayout = new RelativeLayout(getContext());
        childlayout.setId(count);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics());
        left = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics());
        bottom = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics());
        layoutParams.setMargins(0,0,0,bottom);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        childlayout.setPadding(left,top,0,0);
        childlayout.setBackgroundColor(0xCCCCCCCC);
        childlayout.setLayoutParams(layoutParams);

        TextView tv = new TextView(getContext());

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        String tvtext = "";
        String addcontents = "";
        tvtext = "[" + snapshot.getKey() + "]" + "\n";
        for (DataSnapshot sn : snapshot.getChildren()){
            if (!(sn.getKey().equals("START")) && !(sn.getKey().equals("END"))){
                addcontents += " " + sn.getKey(); // 어떤 운동이 있는지 담아 옴 (형식수정 필요)
            }
            else{

            }
        }
        tvtext += ":" + addcontents + "\n";
        tv.setText(tvtext);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
        tv.setTextColor(Color.BLACK);


        tv.setLayoutParams(param);

        childlayout.addView(tv);

        list_item.addView(childlayout);
        count++;
    }


}
