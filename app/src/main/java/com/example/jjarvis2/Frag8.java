package com.example.jjarvis2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Frag8 extends Fragment {
    View view;
    LinearLayout cate;
    private DatabaseReference mDatabase;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag8, container, false);
        cate = (LinearLayout)view.findViewById(R.id.category);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("exercise").child("custom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                int width;
                int height;
                int top,right,bottom;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    RelativeLayout childlayout = new RelativeLayout(getContext());
                    childlayout.setId(count);
                    width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,400,getResources().getDisplayMetrics());
                    height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,150,getResources().getDisplayMetrics());
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);

                    childlayout.setLayoutParams(layoutParams);

                    Button bt = new Button(getContext());
                    width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,200,getResources().getDisplayMetrics());
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width,LinearLayout.LayoutParams.WRAP_CONTENT);
                    bt.setText(snapshot.getKey());
                    bt.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                    bt.setTextColor(Color.WHITE);
                    bt.setBackgroundColor(0xCC6495ed);
                    top = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,getResources().getDisplayMetrics());
                    right = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,130,getResources().getDisplayMetrics());
                    bottom = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics());
                    param.setMargins(right,top,0,bottom);
                    bt.setLayoutParams(param);
                    childlayout.addView(bt);

                    ImageView iv = new ImageView(getContext());
                    width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,getResources().getDisplayMetrics());
                    LinearLayout.LayoutParams iparam = new LinearLayout.LayoutParams(width,LinearLayout.LayoutParams.WRAP_CONTENT);
                    right = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,300,getResources().getDisplayMetrics());
                    iparam.setMargins(0,0,right,0);
                    iv.setImageResource(R.drawable.ex1);
                    iv.setLayoutParams(iparam);
                    childlayout.addView(iv);


                    cate.addView(childlayout);
                    count++;
                    System.out.println(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ImageButton cart=(ImageButton)view.findViewById(R.id.button77);
        cart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((SubActivity)getActivity()).setFrag(77);
            }
        });
        ImageButton play=(ImageButton)view.findViewById(R.id.button78);
        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((SubActivity)getActivity()).setFrag(78);
            }
        });

        return view;
    }
}

