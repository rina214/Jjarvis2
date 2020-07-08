package com.example.jjarvis2;

import androidx.fragment.app.FragmentManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Stack;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SubActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FragmentManager fm;
    androidx.fragment.app.FragmentTransaction tran;


    Frag1 frag1;
    Frag2 frag2;
    Frag3 frag3;
    Frag4 frag4;
    Frag5 frag5;
    Frag77 frag77;
    Frag78 frag78;
    Frag79 frag79;
    Frag6 frag6;
    Frag7 frag7;
    Frag8 frag8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sub);
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_ACTIVITY_SAMPLES)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "OnSuccess()");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "OnFailure()", e);
                    }
                });
        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        frag1 = new Frag1();
        frag2 = new Frag2();
        frag3 = new Frag3();
        frag4 = new Frag4();
        frag5 = new Frag5(); //프래그먼트 객채셍성 (1~5, main)
        frag77 = new Frag77();
        frag78 = new Frag78(); //1의 장바구니, 마이페이지
        frag79 = new Frag79(); //장바구니에서 스케줄 생성페이로 이동
        frag6 = new Frag6();
        frag7 = new Frag7();
        frag8 = new Frag8(); //1-1,1-2,1-3


        setFrag(1); //첫 프레그먼트 지정
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener()); //Bot_navigation bar obj 형성
    }


    /*@Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.bt1:
                setFrag(0);
                break;
            case R.id.bt2:
                setFrag(1);
                break;
            case R.id.bt3:
                setFrag(2);
                break;
            case R.id.bt4:
                setFrag(3);
                break;
            case R.id.bt5:
                setFrag(4);
                break;
        }
    }*/

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            fm = getSupportFragmentManager();
            tran = fm.beginTransaction();

            switch (menuItem.getItemId()) {
                case R.id.bt1:
                    setFrag(1);
                    break;
                case R.id.bt2:
                    setFrag(2);
                    break;
                case R.id.bt3:
                    setFrag(3);
                    break;
                case R.id.bt4:
                    setFrag(4);
                    break;
                case R.id.bt5:
                    setFrag(5);
                    break;
            }
            return true;
        }
    }
    public void setFrag(int n){    //프래그먼트를 교체하는 작업을 하는 메소드를 만들었습니다
        fm = getSupportFragmentManager();
        tran = fm.beginTransaction();
        switch (n){ //replace의 매개변수는 (프래그먼트를 담을 영역 id, 프래그먼트 객체) 입니다.
            case 1:
                tran.replace(R.id.main_frame, frag1).commitAllowingStateLoss();
                //tran.commit();
                break;
            case 2:
                tran.replace(R.id.main_frame, frag2).commitAllowingStateLoss();
                break;
            case 3:
                tran.replace(R.id.main_frame, frag3).commitAllowingStateLoss();
                break;
            case 4:
                tran.replace(R.id.main_frame, frag4).commitAllowingStateLoss();
                break;
            case 5:
                tran.replace(R.id.main_frame, frag5).commitAllowingStateLoss(); //replace (1~5)
                break;
            case 77:
                tran.replace(R.id.main_frame, frag77);
                tran.commit(); // 1's Mycart
                break;
            case 78:
                tran.replace(R.id.main_frame, frag78);
                tran.commit(); // 1's Mylist
                break;
            case 79:
                tran.replace(R.id.main_frame, frag79);
                tran.commit(); // Mycart -> Scheduling
                break;
            case 11:
                tran.replace(R.id.main_frame, frag6);
                tran.commit(); // 1-1
                break;
            case 12:
                tran.replace(R.id.main_frame, frag7);
                tran.commit(); // 1-2
                break;
            case 13:
                tran.replace(R.id.main_frame, frag8);
                tran.commit(); // 1-3
                break;
        }
    }

    public void change(Fragment fragment, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

}

