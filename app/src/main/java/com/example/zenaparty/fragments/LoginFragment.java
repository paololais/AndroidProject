package com.example.zenaparty.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.zenaparty.models.FirebaseWrapper;
import com.example.zenaparty.R;
import com.example.zenaparty.activities.LogActivity;


public class LoginFragment extends LogFragment {
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
            View externalView = inflater.inflate(R.layout.fragment_login, container, false);

            TextView link = externalView.findViewById(R.id.switchLoginToRegisterLabel);
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((LogActivity)LoginFragment.this.requireActivity()).renderFragment(false);
                }
            });

            Button button = externalView.findViewById(R.id.buttonLogin);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText email = externalView.findViewById(R.id.etEmail);
                    EditText password = externalView.findViewById(R.id.etPassword);

                    if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                        // TODO: Better error handling + remove this hardcoded strings
                        email.setError("Email is required");
                        password.setError("Password is required");
                        return;
                    }

                    // Perform SignIn
                    FirebaseWrapper.Auth auth = new FirebaseWrapper.Auth();
                    auth.signIn(
                            email.getText().toString(),
                            password.getText().toString(),
                            FirebaseWrapper.Callback
                                    .newInstance(LoginFragment.this.requireActivity(),
                                            LoginFragment.this.callbackName,
                                            LoginFragment.this.callbackPrms)
                    );
                }
            });

            return externalView;
    }
}