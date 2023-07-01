package com.example.zenaparty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zenaparty.R;
import com.example.zenaparty.fragments.AddEventFragment;
import com.example.zenaparty.fragments.HomeFragment;
import com.example.zenaparty.fragments.MapsFragment;
import com.example.zenaparty.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{
    FirebaseAuth auth;
    FirebaseUser user;

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    MapsFragment mapFragment = new MapsFragment();
    AddEventFragment addEventFragment = new AddEventFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //display actionbar
        Objects.requireNonNull(getSupportActionBar()).show();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_foreground);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), LogActivity.class);
            startActivity(intent);
            finish();
        }
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);



    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {

        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, homeFragment)
                        .commit();
                return true;

            case R.id.map:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, mapFragment)
                        .commit();
                return true;

            case R.id.addEvent:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, addEventFragment)
                        .commit();
                return true;

            case R.id.profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, profileFragment)
                        .commit();
                return true;
        }
        return false;
    }
}