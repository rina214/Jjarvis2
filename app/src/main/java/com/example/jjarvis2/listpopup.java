package com.example.jjarvis2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class listpopup extends Activity {
    DatabaseReference db;
    String userUid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    TextView popup_content, popup_title;
    Button btn_delete, btn_start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        db = FirebaseDatabase.getInstance().getReference();
        userUid = user.getUid();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.listpopup_activity);

        Intent prev = getIntent();
        Bundle bundle = prev.getExtras();
        String title = bundle.getString("TITLE");
        ArrayList<String> execdata = bundle.getStringArrayList("EXECDATA");

        String contentMSG = "";
        for(String s : execdata) {
            contentMSG += s;
            contentMSG += '\n';
        }
        //UI 객체생성
        popup_content = (TextView) findViewById(R.id.popup_content);
        popup_content.setText(contentMSG);
        popup_title = (TextView) findViewById(R.id.popup_title);
        popup_title.setText(title);

        //삭제 버튼 구현
        btn_delete= (Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(title);
            }
        });
        //운동 시작 버튼 구현
        btn_start= (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] temp = popup_content.getText().toString().split("\n");
                for(int i = 0; i < temp.length; i++) {
                    if (temp[i].equals("스쿼트")) {
                        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivityForResult(intent, 214);
                    }
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 214) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Count: " + data.getStringExtra("count"), Toast.LENGTH_SHORT).show();
            } else {   // RESULT_CANCEL
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
//              } else if (requestCode == REQUEST_ANOTHER) {
//                    ...
        }
    }

    private void deleteItem(String title) {
        db.child("userdata").child(userUid).child("MyList").child(title).removeValue();
        Intent intent = new Intent();
        intent.putExtra("result",1);
        setResult(RESULT_OK,intent);
        finish();
    }
}
