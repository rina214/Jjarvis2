package com.example.jjarvis2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    Button bt_sign_in;
    Button bt_sign_up;
    EditText et_id, et_password;

    private DatabaseReference mDatabase; //파이어베이스 데이터베이스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //회원가입에서 넘어가는 버튼
        bt_sign_in=(Button)findViewById(R.id.sign_in_button);
        bt_sign_up=(Button)findViewById(R.id.sign_up_button);

        et_id = findViewById(R.id.Id);
        et_password = findViewById(R.id.password);


        bt_sign_in.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mDatabase.child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            USER user = snapshot.getValue(USER.class);
                            if(user.getUserID().equals(et_id.getText().toString())
                                    && user.getUserPW().equals(et_password.getText().toString())){ //일치하는 회원 정보가 있으면
                                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });
        bt_sign_up.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });



    }

}
