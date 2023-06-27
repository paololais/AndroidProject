package com.example.zenaparty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EventOpenedFragment extends Fragment {

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

        ImageButton btnClose = view.findViewById(R.id.btnClose);
        TextView eventName = view.findViewById(R.id.eventName);
        TextView description = view.findViewById(R.id.eventDescription);
        TextView price = view.findViewById(R.id.eventPrice);
        TextView type = view.findViewById(R.id.eventType);
        TextView date = view.findViewById(R.id.eventDate);
        TextView time = view.findViewById(R.id.eventTime);
        TextView username = view.findViewById(R.id.userName);

        // Recupera i dati dell'evento dall'argomento bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            MyEvent event = bundle.getParcelable("event");
            if (event != null) {
                eventName.setText(event.getEvent_name());
                description.setText(event.getDescription());
                price.setText(String.valueOf(event.getPrice()));
                type.setText(event.getType());
                date.setText(event.getDate());
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