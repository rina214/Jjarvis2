package com.example.jjarvis2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

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



public class Frag1 extends Fragment{
    View view;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    /*@Override
    public void onBackPressed() {
        if (!mFragmentBackStack.isEmpty()) {
            mFragmentBackStack.pop().onBack();
        } else {
            backPressCloseHandler.onBackPressed();
        }
    }*/


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1, container, false);

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

        Button bt1_1=(Button)view.findViewById(R.id.button1_1);
        bt1_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((SubActivity)getActivity()).setFrag(11);
            }
        });
        Button bt1_2=(Button)view.findViewById(R.id.button1_2);
        bt1_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((SubActivity)getActivity()).setFrag(12);
            }
        });
        Button bt1_3=(Button)view.findViewById(R.id.button1_3);
        bt1_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((SubActivity)getActivity()).setFrag(13);
            }
        });

        return view;
    }


}
