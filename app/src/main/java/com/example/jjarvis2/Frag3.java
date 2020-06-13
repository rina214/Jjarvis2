package com.example.jjarvis2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Frag3 extends Fragment {
    View view;
    FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA,FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA,FitnessOptions.ACCESS_READ)
            .build();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3, container, false);



        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(getContext(), fitnessOptions);
        if(!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(getContext()), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    (Activity) getContext(),
                    1,
                    GoogleSignIn.getLastSignedInAccount(getContext()),
                    fitnessOptions);
        } else {
            Fitness.getRecordingClient(getContext(),
                    GoogleSignIn.getLastSignedInAccount(getContext()))
                    .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                    .addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.i(TAG, "Successfully subscribed!");
                                    } else {
                                        Log.w(TAG, "There was a problem subscribing.", task.getException());
                                    }
                                }
                            });
        }
        return view;
    }
}

