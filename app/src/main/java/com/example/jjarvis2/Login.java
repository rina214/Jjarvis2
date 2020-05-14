package com.example.jjarvis2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        button = (Button)findViewById(R.id.bt_submit);


        findViewById(R.id.bt_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewUser("asd147asd147",21,"최원준",170.0, 60.0, "zxc258zxc258", true);
                writeNewUser("computer7214",22,"김리나",160.0, 40.0, "1234", false);
                writeNewUser("sundaeluv98",23,"이지윤",170.0, 45.0, "7777", false);
                Intent intent = new Intent(Login.this, SubActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void writeNewUser(String userID, int userAge, String userName, double userHeight, double userWeight, String userPW, boolean userGender) {
        double userBMI = userWeight/(userHeight*userHeight/10000);
        double userBMR = userGender ? 66+(13.8*userWeight)+(5*userHeight)-(6.8*userAge) : 655+(9.6*userWeight)+(1.8*userHeight)-(4.7*userAge);
        double userDRC = userBMR*1.5;
        USER user = new USER(userID,userAge,userName,userHeight,userWeight,userBMI,userBMR,userDRC,userPW,userGender);
        mDatabase.child("users").child(userID).setValue(user);
    }
}