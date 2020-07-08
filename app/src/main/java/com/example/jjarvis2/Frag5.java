package com.example.jjarvis2;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Frag5 extends Fragment {
    TextView Userid_text, target_text;
    View view;
    Button btnwanttobe, btnchangeinfo, btnLogout, btnRevoke;
    ImageButton noti_button;
    private FirebaseAuth mAuth ;
    private DatabaseReference mDatabase;
    FirebaseUser user;
    String userUid;
    //test
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag5, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        //noti_button=(ImageButton)view.findViewById(R.id.button00);
        btnwanttobe = (Button)view.findViewById(R.id.button01);
        btnwanttobe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //((SubActivity)getActivity()).setFrag(11);
            }
        });

        //btnchangeinfo  = (Button)view.findViewById(R.button02);
        btnLogout = (Button)view.findViewById(R.id.logout);
        btnRevoke = (Button)view.findViewById(R.id.revoke);

        Userid_text=view.findViewById(R.id.Userid_text);
        target_text=view.findViewById(R.id.target_text);

        userUid = user.getDisplayName();
        String A= "\t" + userUid + "님," + "\n" + "오늘 하루도 운동합시다.^^";
        //DB에 적용.(수정필요)
        int target=45;
        int now=49;
        String B=" ";
        if(target<now) {
            B="-> 목표까지 " + Integer.toString(now-target) + "kg 감량이 남았습니다." + "\n";
        }
        else  {
            B="(목표체중 달성완료! 헬린이에서 헬창으로 진화하셨네요!";
        }

        Userid_text.setText(A);
        target_text.setText(B);


        mAuth = FirebaseAuth.getInstance();
        if (user != null){
            userUid = user.getUid();
            btnLogout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    signOut();
                    getActivity().finishAffinity();
                    System.runFinalization();
                    System.exit(0);
                }
            });
            btnRevoke.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    revokeAccess();
                    getActivity().finishAffinity();
                    System.runFinalization();
                    System.exit(0);
                }
            });
        }
        return view;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
    private void revokeAccess() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userUid).removeValue();
        mDatabase.child("userdata").child(userUid).removeValue();
        FirebaseAuth.getInstance().signOut();
        user.delete();

    }



}

