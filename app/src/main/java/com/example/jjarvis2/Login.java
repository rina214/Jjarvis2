package com.example.jjarvis2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private Button button;
    private EditText height,weight;
    private Spinner spinner_age;
    private RadioGroup gender;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        button = (Button)findViewById(R.id.bt_submit);
        ArrayList<String> age = new ArrayList<String>();
        age.add("선택");
        for (int i = 10; i <= 61; i++) {
            age.add(Integer.toString(i)+"세");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, age);

        Spinner spinYear = (Spinner)findViewById(R.id.age);
        spinYear.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner_age = findViewById(R.id.age);
                height = findViewById(R.id.height);
                weight = findViewById(R.id.weight);
                gender = findViewById(R.id.radioGroup);

                String userUid = user.getUid();;
                String str_Age = spinner_age.getSelectedItem().toString();
                String userName = user.getDisplayName();
                int userAge = Integer.parseInt(str_Age.substring(0,2));
                double userHeight = Double.parseDouble(height.getText().toString());
                double userWeight = Double.parseDouble(weight.getText().toString());

                RadioButton Gender = (RadioButton)findViewById(gender.getCheckedRadioButtonId());
                String str_Gender = Gender.getText().toString();
                boolean userGender = str_Gender == "Man" ? true : false;
                DatabaseReference data = mDatabase.child("users");

                writeNewUser(userUid,userAge,userName,userHeight,userWeight,userGender);
//
//                writeNewUser("asd147asd147",21,"최원준",170.0, 60.0, "zxc258zxc258", true);
//                writeNewUser("computer7214",22,"김리나",160.0, 40.0, "1234", false);
//                writeNewUser("sundaeluv98",23,"이지윤",170.0, 45.0, "7777", false);
                Intent intent = new Intent(getApplication(), SubActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void writeNewUser(String userUid, int userAge, String userName, double userHeight, double userWeight, boolean userGender) {
        double userBMI = userWeight / (userHeight * userHeight / 10000);
        double userBMR = userGender ? 66 + (13.8 * userWeight) + (5 * userHeight) - (6.8 * userAge) : 655 + (9.6 * userWeight) + (1.8 * userHeight) - (4.7 * userAge);
        double userDRC = userBMR * 1.5;
        USER user = new USER(userUid, userAge, userName, userHeight, userWeight, userBMI, userBMR, userDRC, userGender);
        mDatabase.child("users").child(userUid).setValue(user);

        Map<String,Integer> test = new HashMap<>();
        test.put("lunge",0);
        test.put("squat",0);
        specification spec = new specification(20200512,1000,500,500,1000,test);
        mDatabase.child("userdata").child(userUid).child(String.valueOf(spec.year())).child(String.valueOf(spec.week())).child(String.valueOf(spec.getDate())).setValue(spec);
        //mDatabase.child("userdata").child(userID).child(String.valueOf(spec.year())).child(String.valueOf(spec.week())).updateChildren(spec.week_goal());
    }
}