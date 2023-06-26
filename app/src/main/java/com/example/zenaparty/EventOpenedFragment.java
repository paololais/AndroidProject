package com.example.zenaparty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class EventOpenedFragment extends Fragment {
    private ImageButton btnClose;
    private TextView eventName;
    private TextView description;
    private TextView price;
    private TextView type;
    private TextView time;
    private TextView username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_opened, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnClose = view.findViewById(R.id.btnClose);
        eventName = view.findViewById(R.id.eventName);
        description = view.findViewById(R.id.eventDescription);
        price = view.findViewById(R.id.eventPrice);
        type = view.findViewById(R.id.eventType);
        time = view.findViewById(R.id.eventTime);
        username = view.findViewById(R.id.userName);

        // Recupera i dati dell'evento dall'argomento bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            MyEvent event = bundle.getParcelable("event");
            if (event != null) {
                eventName.setText(event.getEvent_name());
                description.setText(event.getDescription());
                price.setText(String.valueOf(event.getPrice()));
                type.setText(event.getType());
                time.setText(event.getTime());
                username.setText(event.getUsername());
            }
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });
    }
}