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


        changed_height= (EditText)findViewById(R.id.wannabe);
        changed_age= (EditText)findViewById(R.id.wannabe);
        changed_kg= (EditText)findViewById(R.id.wannabe);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = changed_height.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("DATA", data);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }
}
