package com.example.zenaparty;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private TextView tvSelectDate;
    private TextView tvNoEvents;
    private Button btnIncreaseDay;
    private Button btnDecreaseDay;
    private Button btnFilter;
    RecyclerView recyclerView;
    DatabaseReference database;
    EventListAdapter myAdapter;
    ArrayList<MyEvent> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvSelectDate = view.findViewById(R.id.tvSelectDate);
        tvNoEvents = view.findViewById(R.id.tvNoEvents);
        btnIncreaseDay = view.findViewById(R.id.btnIncreaseDay);
        btnDecreaseDay = view.findViewById(R.id.btnDecreaseDay);
        btnFilter = view.findViewById(R.id.btnFilter);

        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        database = FirebaseDatabase.getInstance("https://pmappfirsttry-default-rtdb.europe-west1.firebasedatabase.app/").getReference("events");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Imposta la data di oggi sul pulsante al momento della creazione del fragment
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d MMMM", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        tvSelectDate.setText(currentDate);


        tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d MMMM", Locale.getDefault());
                        String formattedDate = dateFormat.format(selectedDate.getTime());
                        tvSelectDate.setText(formattedDate);

                        // metodo per filtrare gli eventi in base alla data selezionata
                        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String newFormattedDate = newFormat.format(selectedDate.getTime());
                        filterEventsByDate(newFormattedDate);
                    }
                },year, month,day);
                dialog.show();

            }
        });
        // Listener per il pulsante per aumentare il giorno
        btnIncreaseDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d MMMM", Locale.getDefault());
                String formattedDate = dateFormat.format(calendar.getTime());
                tvSelectDate.setText(formattedDate);

                // metodo per filtrare gli eventi in base alla data selezionata
                SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String newFormattedDate = newFormat.format(calendar.getTime());
                filterEventsByDate(newFormattedDate);
            }
        });
        // Listener per il pulsante per decrementare il giorno
        btnDecreaseDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d MMMM", Locale.getDefault());
                String formattedDate = dateFormat.format(calendar.getTime());
                tvSelectDate.setText(formattedDate);

                // metodo per filtrare gli eventi in base alla data selezionata
                SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String newFormattedDate = newFormat.format(calendar.getTime());
                filterEventsByDate(newFormattedDate);
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });


        list = new ArrayList<>();
        myAdapter = new EventListAdapter(getContext(),list);
        recyclerView.setAdapter(myAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MyEvent event = dataSnapshot.getValue(MyEvent.class);
                    list.add(event);
                }
                myAdapter.setEventList(list);
                myAdapter.notifyDataSetChanged();

                filterEventsByDate("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterEventsByDate(String selectedDate) {
        ArrayList<MyEvent> filteredList = new ArrayList<>();

        for (MyEvent event : list) {
            if (event.getDate().equals(selectedDate)) {
                filteredList.add(event);
            }
        }

        myAdapter.setEventList(filteredList);
        myAdapter.notifyDataSetChanged();

        if (filteredList.isEmpty()) {
            tvNoEvents.setVisibility(View.VISIBLE);
        } else {
            tvNoEvents.setVisibility(View.GONE);
        }
    }
    private void openDialog() {
        FilterDialogFragment exampleDialog = new FilterDialogFragment();
        exampleDialog.show(getActivity().getSupportFragmentManager(), "example dialog");
    }
}