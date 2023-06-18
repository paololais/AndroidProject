package com.example.loginapp;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;


public class RegisterFragment extends LogFragment {
   @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       this.initArguments();
   }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // See: https://developer.android.com/reference/android/view/LayoutInflater#inflate(org.xmlpull.v1.XmlPullParser,%20android.view.ViewGroup,%20boolean)
        View externalView = inflater.inflate(R.layout.fragment_register, container, false);

        TextView link = externalView.findViewById(R.id.switchRegisterToLoginLabel);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LogActivity) RegisterFragment.this.requireActivity()).renderFragment(true);
            }
        });

        Button button = externalView.findViewById(R.id.buttonRegister);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = externalView.findViewById(R.id.etEmail);
                EditText password = externalView.findViewById(R.id.etPassword1);
                EditText password2 = externalView.findViewById(R.id.etPassword2);

                if (email.getText().toString().isEmpty() ||
                        password.getText().toString().isEmpty() ||
                        password2.getText().toString().isEmpty()) {
                    // TODO: Better error handling + remove this hardcoded strings
                    email.setError("Email is required");
                    password.setError("Password is required");
                    password2.setError("Password is required");
                    return;
                }

                if (!password.getText().toString().equals(password2.getText().toString())) {
                    // TODO: Better error handling + remove this hardcoded strings
                    Toast
                            .makeText(RegisterFragment.this.requireActivity(), "Passwords are different", Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                // Perform SignIn
                FirebaseWrapper.Auth auth = new FirebaseWrapper.Auth();
                auth.signUp(
                        email.getText().toString(),
                        password.getText().toString(),
                        FirebaseWrapper.Callback
                                .newInstance(RegisterFragment.this.requireActivity(),
                                        RegisterFragment.this.callbackName,
                                        RegisterFragment.this.callbackPrms)
                );
            }
        });

        return externalView;
    }
}