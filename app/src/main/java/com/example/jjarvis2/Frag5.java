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
import android.widget.Toast;

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

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class Frag5 extends Fragment {
    TextView Userid_text, target_text;
    View view;
    Button btnwanttobe, btnchangeinfo, btnLogout, btnRevoke;
    ImageButton noti_button;
    private FirebaseAuth mAuth ;
    private DatabaseReference mDatabase;
    FirebaseUser user;
    String userUid;
    DatabaseReference db;
    USER u;
    double now = 0;
    String target= "0";
    int REQUEST_TEST = 1;
    //test
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag5, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference();
        db.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                u = dataSnapshot.getValue(USER.class);
                now = u.getUserWeight();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //noti_button=(ImageButton)view.findViewById(R.id.button00);
        btnwanttobe = (Button)view.findViewById(R.id.button01);
        btnwanttobe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), setwantobe.class);
                startActivityForResult(intent, 1);
            }
        });
        btnchangeinfo = (Button)view.findViewById(R.id.button02);
        btnchangeinfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), fixinfo.class);
                startActivityForResult(intent, 2);
            }
        });

        //btnchangeinfo  = (Button)view.findViewById(R.button02);
        btnLogout = (Button)view.findViewById(R.id.logout);
        btnRevoke = (Button)view.findViewById(R.id.revoke);
        setTarget();
        mAuth = FirebaseAuth.getInstance();
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
        return view;
    }

    private void setTarget(){
        userUid = user.getDisplayName();
        Userid_text=view.findViewById(R.id.Userid_text);
        target_text=view.findViewById(R.id.target_text);

        String A= "\t" + userUid + "님," + "\n" + "오늘 하루도 운동합시다.^^";
        //DB에 적용.(수정필요)
        String B=" ";
        if(Double.parseDouble(target)<now) {
            B="-> 목표까지 " + String.valueOf(now-Double.valueOf(target)) + "kg 감량이 남았습니다.";
        }
        else  {
            B="목표체중 달성완료! 헬린이에서 헬창으로 진화하셨네요!";
        }

        Userid_text.setText(A);
        target_text.setText(B);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String tmp = data.getStringExtra("DATA");
                target = tmp;
                setTarget();
            }
        }
        else if (requestCode == 2){
            if (resultCode == RESULT_OK) {
                String tmp1 = data.getStringExtra("DATA1");
                String tmp2 = data.getStringExtra("DATA2");
                String tmp3 = data.getStringExtra("DATA3");
                u.setUserWeight(Double.parseDouble(tmp1));
                u.setUserAge(Integer.parseInt(tmp2));
                u.setUserHeight(Integer.parseInt(tmp3));
                db.child("users").child(user.getUid()).setValue(u);
            }
        }
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
