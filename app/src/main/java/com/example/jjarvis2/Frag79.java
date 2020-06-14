package com.example.jjarvis2;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;

import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Frag79 extends Fragment {
    View view;
    ArrayList<String> list;
    Button set_button;

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

        Log.d("Frag79", list.get(0));

        CalendarView mCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        mCalendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);
        List<Calendar> days = mCalendarView.getSelectedDates();

        String result="";
        for( int i=0; i<days.size(); i++)
        {
            Calendar calendar = days.get(i);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            final int month = calendar.get(Calendar.MONTH);
            final int year = calendar.get(Calendar.YEAR);
            String week = new SimpleDateFormat("EE").format(calendar.getTime());
            String day_full = year + "년 "+ (month+1)  + "월 " + day + "일 " + week + "요일";
            result += (day_full + "\n");
        }
        Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
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
                ((SubActivity)getActivity()).setFrag(78);
            }
        });

        return view;
    }
}