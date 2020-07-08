package com.example.jjarvis2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class fixinfo extends Activity {
    DatabaseReference db;
    String userUid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    EditText changed_height, changed_age, changed_kg;
    Button btn_ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseDatabase.getInstance().getReference();
        userUid = user.getUid();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fixinfo_activity);


        changed_height= (EditText)findViewById(R.id.changed_height);
        changed_age= (EditText)findViewById(R.id.changed_age);
        changed_kg= (EditText)findViewById(R.id.changed_kg);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data1 = changed_height.getText().toString();
                String data2 = changed_age.getText().toString();
                String data3 = changed_kg.getText().toString();
                Intent intent1 = new Intent();
                Intent intent2 = new Intent();
                Intent intent3 = new Intent();
                intent1.putExtra("DATA1", data1);
                intent2.putExtra("DATA2", data2);
                intent3.putExtra("DATA3", data3);
                setResult(RESULT_OK, intent1);
                setResult(RESULT_OK, intent2);
                setResult(RESULT_OK, intent3);
                finish();
            }
        });


    }
}