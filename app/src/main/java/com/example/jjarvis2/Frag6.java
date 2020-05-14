package com.example.jjarvis2;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

public class Frag6 extends Fragment {
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag6, container, false);

        ImageButton cart=(ImageButton)view.findViewById(R.id.button77);
        cart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((SubActivity)getActivity()).setFrag(77);
            }
        });
        ImageButton play=(ImageButton)view.findViewById(R.id.button78);
        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((SubActivity)getActivity()).setFrag(78);
            }
        });

        return view;
    }
}

