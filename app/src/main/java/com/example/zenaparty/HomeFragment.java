package com.example.zenaparty;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment implements EventListInterface{
    private TextView tvSelectDate;
    private TextView tvNoEvents;
    private Button btnIncreaseDay;
    private Button btnDecreaseDay;
    private Spinner spinnerFilter;
    RecyclerView recyclerView;
    DatabaseReference database;
    EventListAdapter myAdapter;
    ArrayList<MyEvent> list;
    String formattedDate;
    String newFormattedDate;
    private SharedPreferences sharedPreferences;
    private boolean isNewlyCreated = true;
    final Calendar calendar = Calendar.getInstance();
    final int year = calendar.get(Calendar.YEAR);
    final int month = calendar.get(Calendar.MONTH);
    final int day = calendar.get(Calendar.DAY_OF_MONTH);

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
        spinnerFilter = view.findViewById(R.id.spinnerFilter);

        // Crea un ArrayAdapter con le opzioni del filtro
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Imposta l'adapter per il Spinner
        spinnerFilter.setAdapter(adapter);

        sharedPreferences = getActivity().getSharedPreferences("SavedValues", Context.MODE_PRIVATE);

        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        database = FirebaseDatabase.getInstance("https://pmappfirsttry-default-rtdb.europe-west1.firebasedatabase.app/").getReference("events");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Controlla se ci sono dati di stato salvati
        if (isNewlyCreated) {
            // L'app è stata appena avviata, impostare i valori di default

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d MMMM", Locale.getDefault());
            formattedDate = dateFormat.format(calendar.getTime());
            tvSelectDate.setText(formattedDate);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            newFormattedDate = newFormat.format(calendar.getTime());
            list = new ArrayList<>();
            myAdapter = new EventListAdapter(getContext(),list, this);
            recyclerView.setAdapter(myAdapter);
            readDatabase(database);
        } else {
            // L'app è stata ripresa dalla modalità di background, recuperare lo stato salvato
            formattedDate = sharedPreferences.getString("date", "");
            tvSelectDate.setText(formattedDate);
            newFormattedDate = sharedPreferences.getString("newFormattedDate", "");

            Gson gson = new Gson();
            String json = sharedPreferences.getString("eventList", "");
            Type type = new TypeToken<ArrayList<MyEvent>>() {}.getType();
            list = gson.fromJson(json, type);

            if (list == null) {
                list = new ArrayList<>();
                myAdapter = new EventListAdapter(getContext(),list, this);
                recyclerView.setAdapter(myAdapter);
                readDatabase(database);
            }

            // Aggiorna l'adattatore con la lista recuperata
            myAdapter = new EventListAdapter(getContext(),list,this);
            recyclerView.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();

            filterEventsByDate(newFormattedDate);
        }


        tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d MMMM", Locale.getDefault());
                        formattedDate = dateFormat.format(calendar.getTime());
                        tvSelectDate.setText(formattedDate);

                        // metodo per filtrare gli eventi in base alla data selezionata
                        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        newFormattedDate = newFormat.format(calendar.getTime());
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
                formattedDate = dateFormat.format(calendar.getTime());
                tvSelectDate.setText(formattedDate);

                // metodo per filtrare gli eventi in base alla data selezionata
                SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                newFormattedDate = newFormat.format(calendar.getTime());
                filterEventsByDate(newFormattedDate);
            }
        });
        // Listener per il pulsante per decrementare il giorno
        btnDecreaseDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d MMMM", Locale.getDefault());
                formattedDate = dateFormat.format(calendar.getTime());
                tvSelectDate.setText(formattedDate);

                // metodo per filtrare gli eventi in base alla data selezionata
                SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                newFormattedDate = newFormat.format(calendar.getTime());
                filterEventsByDate(newFormattedDate);
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

    @Override
    public void onResume() {
        super.onResume();

        isNewlyCreated = false;
    }


    @Override
    public void onPause() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //salva text view data
        editor.putString("date", formattedDate);
        editor.putString("newFormattedDate", newFormattedDate);
        //salva lista eventi corrente
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("eventList", json);

        editor.apply();
        super.onPause();
    }
    public void readDatabase(DatabaseReference db){
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MyEvent event = dataSnapshot.getValue(MyEvent.class);
                    list.add(event);
                }
                myAdapter.setEventList(list);
                myAdapter.notifyDataSetChanged();

                filterEventsByDate(newFormattedDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        EventOpenedFragment eventOpenedFragment = new EventOpenedFragment();

        // Passiamo i dati dell'evento al fragment EventOpenedFragment utilizzando un Bundle
        MyEvent selectedEvent = myAdapter.getList().get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable("event", selectedEvent);
        eventOpenedFragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, eventOpenedFragment);
        fragmentTransaction.addToBackStack(null); // Aggiunge il fragment alla back stack, se desiderato
        fragmentTransaction.commit();
    }

}