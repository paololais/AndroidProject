package com.example.loginapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button logoutButton;
    TextView userTextView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        userTextView = findViewById(R.id.userTextView);
        logoutButton = findViewById(R.id.logoutButton);
        user = auth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), LogActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            userTextView.setText(user.getEmail());
        }
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LogActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}