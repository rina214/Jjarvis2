package com.example.jjarvis2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class setwantobe extends Activity {
    DatabaseReference db;
    String userUid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    EditText wannabe;
    Button btn_ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseDatabase.getInstance().getReference();
        userUid = user.getUid();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setwant_activity);

        wannabe= (EditText)findViewById(R.id.wannabe);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = wannabe.getText().toString();
                if (data.length() == 0) {
                    Toast.makeText(getApplicationContext(), "목표 체중을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.child("userdata").child(user.getUid()).child("goal weight").setValue(data);
                Intent intent = new Intent();
                intent.putExtra("DATA", data);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }
}
