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
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Frag7 extends Fragment {
    View view;
    LinearLayout cate;
    int width;
    int height;
    int top,right,bottom;
    private DatabaseReference mDatabase;
    private static final int DYNAMICIMAGEVIEW_ID = 100;
    private static final int DYNAMICBUTTON_ID = 10;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    int count = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag7, container, false);
        cate = (LinearLayout)view.findViewById(R.id.category);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("exercise").child("part").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    DynamicCate(snapshot);
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

    public void SetListener() {
        View.OnClickListener Listener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Button tempbt = (Button)view.findViewById(v.getId());
                mDatabase.child("exercise").child("part").child(String.valueOf(tempbt.getText())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() == 0){
                            Map<String,Object> cart = new HashMap<String,Object>();
                            cart.put((String) tempbt.getText(),0);
                            mDatabase.child("userdata").child(user.getUid()).child("cart").updateChildren(cart);
                            Toast.makeText(getActivity(), tempbt.getText() + " 운동을 담았습니다.", Toast.LENGTH_LONG).show();
                        }else{
                            remove_category();
                            count = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                DynamicCate(snapshot);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        };
        for(int i=0; i < count; i++){
            Button tempbt = (Button)view.findViewById(DYNAMICBUTTON_ID+i);
            tempbt.setOnClickListener(Listener);
        }
    }

    void remove_category(){
        for(int i=0; i < count; i++){
            cate.removeViewInLayout(view.findViewById(i));
        }
    }
    void DynamicCate(DataSnapshot ds){
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
        bt.setId(DYNAMICBUTTON_ID + count);
        bt.setText(ds.getKey());
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
        iv.setId(DYNAMICIMAGEVIEW_ID + count);
        width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,getResources().getDisplayMetrics());
        LinearLayout.LayoutParams iparam = new LinearLayout.LayoutParams(width,LinearLayout.LayoutParams.WRAP_CONTENT);
        right = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,300,getResources().getDisplayMetrics());
        iparam.setMargins(0,0,right,0);
        iv.setImageResource(R.drawable.ex1);
        iv.setLayoutParams(iparam);
        childlayout.addView(iv);

        cate.addView(childlayout);
        count++;
        SetListener();
    }
}

