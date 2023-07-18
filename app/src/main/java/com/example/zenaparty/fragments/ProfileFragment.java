package com.example.zenaparty.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zenaparty.R;
import com.example.zenaparty.activities.LogActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    Button logoutButton;
    TextView userTextView;
    FirebaseUser user;
    FirebaseAuth auth;

    ImageView settingImageView;

    TextView eventsTV;

    TextView preferTV;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);


    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        userTextView = view.findViewById(R.id.userTextView);
        settingImageView = view.findViewById(R.id.settingsiv);
        eventsTV = view.findViewById(R.id.eventstv);
        preferTV = view.findViewById(R.id.prefertv);
        logoutButton = view.findViewById(R.id.logoutButton);
        user = auth.getCurrentUser();
        userTextView.setText(user.getEmail());
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LogActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        settingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SettingsFragment SettingsFragment = new SettingsFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flFragment, SettingsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        eventsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyEventsFragment myEventsFragment = new MyEventsFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flFragment, myEventsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        preferTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PreferitiFragment preferitiFragment = new PreferitiFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flFragment, preferitiFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}