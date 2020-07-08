package com.example.jjarvis2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

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
import java.util.Set;

import static android.app.Activity.RESULT_OK;

public class Frag78 extends Fragment {
    View view;
    private DatabaseReference mDatabase;
    int count;
    int width, height;
    int top,right,left,bottom;
    int REQUEST_TEST = 1;
    Map<String,Object> metadata = new HashMap<String,Object>();
    ArrayList<String> execdata =new ArrayList<>();
    LinearLayout list_item;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag78, container, false);
        list_item = (LinearLayout)view.findViewById(R.id.list_item);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        refresh();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_TEST) {
            if (resultCode == RESULT_OK) {
                remove_category();
                refresh();
            }
        }

    }

    private void refresh() {
        mDatabase.child("userdata").child(user.getUid()).child("MyList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    DynamicList(snapshot);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void SetListener() {
        View.OnClickListener Listener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView tempchildlayout = (TextView)view.findViewById(v.getId());
                mDatabase.child("userdata").child(user.getUid()).child("MyList").addListenerForSingleValueEvent(new ValueEventListener() {
                    Intent intent = new Intent(getActivity(), listpopup.class);
                    Bundle bundle = new Bundle();

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String tvtext = "";
                            String starttime = "", endtime = "";
                            tvtext = snapshot.getKey() + "\n";
                            metadata.clear();
                            metadata.put("LISTNAME",snapshot.getKey());
                            for (DataSnapshot sn : snapshot.getChildren()){
                                if (sn.getKey().equals("START")){
                                    starttime = sn.getValue(String.class);
                                    metadata.put(sn.getKey(),sn.getValue());
                                }
                                else if(sn.getKey().equals("END")){
                                    endtime = sn.getValue(String.class);
                                    metadata.put(sn.getKey(),sn.getValue());
                                }
                            }
                            tvtext += starttime +"~ "+ endtime + "\n";
                            if(tvtext.equals(tempchildlayout.getText())){
                                execdata.clear();
                                for (DataSnapshot sn : snapshot.getChildren()) {
                                    if (sn.getKey().equals("START") || sn.getKey().equals("END") ){
                                    }
                                    else{
                                        execdata.add(sn.getKey());
                                    }
                                }
                                System.out.println(metadata.get("START"));
                                System.out.println(metadata.get("END"));
                                for(String s : execdata){
                                    System.out.println(s);
                                }
                                bundle.putStringArrayList("EXECDATA", execdata);
                                bundle.putString("TITLE", (String) metadata.put("LISTNAME",snapshot.getKey()));
                                bundle.putString("START", (String) metadata.put("START",snapshot.getKey()));
                                bundle.putString("END", (String) metadata.put("END",snapshot.getKey()));
                                intent.putExtras(bundle);
                                startActivityForResult(intent,REQUEST_TEST);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        };
        for(int i=0; i < count; i++){
            TextView tempchildlayout = (TextView)view.findViewById(i);
            tempchildlayout.setOnClickListener(Listener);
        }
    }
    void remove_category(){
        for(int i=0; i < count; i++){
            list_item.removeViewInLayout(view.findViewById(i+10000));
        }
    }
    private void DynamicList(DataSnapshot snapshot) {
        RelativeLayout childlayout = new RelativeLayout(getContext());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        top = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics());
        left = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics());
        bottom = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics());
        layoutParams.setMargins(0,0,0,bottom);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        childlayout.setId(count+10000);
        childlayout.setPadding(left,top,0,0);
        childlayout.setBackgroundColor(0xCCCCCCCC);
        childlayout.setLayoutParams(layoutParams);

        TextView tv = new TextView(getContext());
        tv.setId(count);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        String tvtext = "";
        String starttime = "", endtime = "";
        tvtext = snapshot.getKey() + "\n";
        for (DataSnapshot sn : snapshot.getChildren()){
            if (sn.getKey().equals("START")){
                starttime = sn.getValue(String.class);
            }
            else if(sn.getKey().equals("END")){
                endtime = sn.getValue(String.class);
            }
        }
        tvtext += starttime +"~ "+ endtime + "\n";
        tv.setText(tvtext);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
        tv.setTextColor(Color.BLACK);


        tv.setLayoutParams(param);

        childlayout.addView(tv);

        list_item.addView(childlayout);
        count++;
        SetListener();
    }
}



