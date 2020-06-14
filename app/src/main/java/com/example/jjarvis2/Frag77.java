package com.example.jjarvis2;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Objects;

public class Frag77 extends Fragment {
    View view;
    Button addButton;
    ImageButton deleteButton;
    ImageButton selectAllButton;
    Button SchedulingButton;
    ArrayList<String> items;
    ListView listview;
    ArrayAdapter<String> adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag77, container, false);

        //String[] items = {"6/19 : 상체 운동 ", "6/25 스쿼트& 런지 조지기", "7/12 여름까지 복근 만들기"};

        items = new ArrayList<String>() ;
        // ArrayAdapter 생성. 아이템 View를 선택(multiple choice)가능하도록 만듦.*/

        // listview 생성 및 adapter 지정.
        listview = (ListView) view.findViewById(R.id.listview) ;
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item_multiple_choice, items) ;

        listview.setAdapter(adapter) ;


        // add button에 대한 이벤트 처리.
        addButton = (Button)view.findViewById(R.id.add) ;
        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count;
                count = adapter.getCount();

                // 아이템 추가.
                items.add("LIST" + Integer.toString(count + 1));

                // listview 갱신
                adapter.notifyDataSetChanged();
            }
        }) ;


        // delete button에 대한 이벤트 처리.
        deleteButton = (ImageButton)view.findViewById(R.id.button78) ;
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                SparseBooleanArray checkedItems = listview.getCheckedItemPositions();
                int count = adapter.getCount() ;

                for (int i = count-1; i >= 0; i--) {
                    if (checkedItems.get(i)) {
                        items.remove(i) ;
                    }
                }
                // 모든 선택 상태 초기화.
                listview.clearChoices() ;

                adapter.notifyDataSetChanged();
            }
        }) ;


        // selectAll button에 대한 이벤트 처리.

        selectAllButton = (ImageButton)view.findViewById(R.id.button77) ;
        selectAllButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count = 0 ;
                count = adapter.getCount() ;

                for (int i=0; i<count; i++) {
                    listview.setItemChecked(i, true) ;
                }
            }
        }) ;

        SchedulingButton = (Button)view.findViewById(R.id.schedule_set_button) ;
        SchedulingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Log.d("SchedulingButton", String.valueOf(listview.getCheckedItemCount()));

                if(listview.getCheckedItemCount() == 0) {
                    Toast toast = Toast.makeText(getActivity(), "일정에 등록할 운동을 선택해주십시오.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 330);
                    toast.show();
                }
                else {
                    ArrayList<String> tmpList = new ArrayList<>();
                    for(int i = 0; i < adapter.getCount(); i++) {
                        if(listview.getCheckedItemPositions().get(i)) {
                            tmpList.add(items.get(i));
                        }
                    }

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Frag79 frag79 = Frag79.newInstance(tmpList);
                    fragmentTransaction.replace(R.id.main_frame, frag79);
                    fragmentTransaction.commit();

//                    ((SubActivity) getActivity()).setFrag(79);

                }
            }
        });


        return view;
    }
}

