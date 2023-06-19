package com.example.loginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    FirebaseAuth auth;
    Button logoutButton;
    TextView userTextView;
    FirebaseUser user;

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    MapsFragment mapFragment = new MapsFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //display actionbar
        getSupportActionBar().show();
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
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
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

            case R.id.settings:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, settingsFragment)
                        .commit();
                return true;
        }
        return false;
    }
}