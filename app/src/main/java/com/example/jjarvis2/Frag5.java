package com.example.jjarvis2;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

public class Frag5 extends Fragment {
    View view;
    Button btnLogout, btnRevoke;
    private FirebaseAuth mAuth ;
    private DatabaseReference mDatabase;
    FirebaseUser user;
    String userUid;
    //test
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag5, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        btnLogout = (Button)view.findViewById(R.id.logout);
        btnRevoke = (Button)view.findViewById(R.id.revoke);

        mAuth = FirebaseAuth.getInstance();
        if (user != null){
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
        }
        return view;
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

