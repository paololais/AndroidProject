package com.example.zenaparty.models;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.zenaparty.adapters.EventListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

// NOTE: With firebase we have to do a network request --> We need to add the permission in the AndroidManifest.xml
//      -> ref: https://developer.android.com/training/basics/network-ops/connecting

// Firebase auth - https://firebase.google.com/docs/auth/android/start?hl=en#java
// Firebase db - https://firebase.google.com/docs/database/android/start?hl=en

// 1) Create a new project from - https://firebase.google.com/ (console: https://console.firebase.google.com/u/0/)
// 2) Enable authentication: Build > Authentication > Get started , then enable Email/password (or other auth types)
// 3a) In Android Studio: Tools > Firebase > Authentication (or Realtime Database or the thing that you need!)
//      ( Then follow the instructions )
// 3b) Alternative you can connect firebase to your Android app - https://firebase.google.com/docs/android/setup?hl=en#register-app

public class FirebaseWrapper {
    public static class Callback {
        private final static String TAG = Callback.class.getCanonicalName();
        private final Method method;
        private final Object thiz;

        public Callback(Method method, Object thiz) {
            this.method = method;
            this.thiz = thiz;
        }

        public static Callback newInstance(Object thiz, String name, Class<?>... prms) {
            Class<?> clazz = thiz.getClass();
            try {
                return new Callback(clazz.getMethod(name, prms), thiz);
            } catch (NoSuchMethodException e) {
                Log.w(TAG, "Cannot find method " + name + " in class " + clazz.getCanonicalName());

                // TODO: Better handling of the error
                throw new RuntimeException(e);
            }
        }

        public void invoke(Object... objs) {
            try {
                this.method.invoke(thiz, objs);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Log.w(TAG, "Something went wrong during the callback. Message: " + e.getMessage());

                // TODO: Better handling of such an error
                throw new RuntimeException(e);
            }
        }
    }

    // Auth with email and password: https://firebase.google.com/docs/auth/android/password-auth?hl=en
    public static class Auth {
        private final static String TAG = Auth.class.getCanonicalName();
        private final FirebaseAuth auth;

        public Auth() {
            this.auth = FirebaseAuth.getInstance();
        }

        public boolean isAuthenticated() {
            return this.auth.getCurrentUser() != null;
        }

        public FirebaseUser getUser() {
            return this.auth.getCurrentUser();
        }

        public void signOut() {
            this.auth.signOut();
        }

        public void signIn(String email, String password, ProgressBar progressBar, Callback callback) {
            progressBar.setVisibility(View.VISIBLE);
            this.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            callback.invoke(task.isSuccessful());
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }

        public void signUp(String email, String password, String username, ProgressBar progressBar, Callback callback) {
            progressBar.setVisibility(View.VISIBLE);
            this.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    String userEmail = user.getEmail();

                                    // Salva email nel database "users"
                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                                    usersRef.child(userId).child("email").setValue(userEmail);

                                    // Salva l'username nel database degli usernames
                                    DatabaseReference usernamesRef = FirebaseDatabase.getInstance().getReference("usernames");
                                    usernamesRef.child(username).setValue(userId);

                                    // Callback con esito positivo
                                    callback.invoke(true);
                                } else {
                                    // Gestione dell'errore
                                    callback.invoke(false);
                                }
                            } else {
                                // Gestione dell'errore
                                callback.invoke(false);
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }

        public String getUid() {
            // TODO: remove this assert and better handling of non logged-in users
            assert this.isAuthenticated();
            return this.getUser().getUid();
        }
    }


    //database
    public static class Database {
        private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://pmappfirsttry-default-rtdb.europe-west1.firebasedatabase.app/").getReference("events");

        public Database() {

        }

        public static void saveEvent(MyEvent event) {

            databaseReference.orderByChild("timestamp")
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Check if there is a last event
                            if (dataSnapshot.exists()) {
                                // Get the last event
                                DataSnapshot lastEventSnapshot = dataSnapshot.getChildren().iterator().next();
                                String lastEventId = lastEventSnapshot.getKey();
                                Long lastEventIdValue = lastEventSnapshot.child("event_id").getValue(Long.class);

                                // Step 2: Increment the retrieved "event_id" value
                                long newEventIdValue;
                                if (lastEventIdValue == null) {
                                    newEventIdValue = 0L;   // If the "event_id" value is null, set it to 1
                                } else {
                                    newEventIdValue = lastEventIdValue + 1;
                                }

                                // Step 3: Insert a new event with the incremented "event_id" value
                                String newEventId = databaseReference.push().getKey();
                                event.setEvent_id(newEventIdValue);

                                // Push the new event to the events node
                                databaseReference.child(newEventId).setValue(event);

                                addToInsertedEvents(event);
                                Log.w("FirebaseWrapper", "New event inserted with ID: " + newEventId);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("FirebaseWrapper", "Failed to read value.", error.toException());
                        }
                    });
        }

        public static void getCurrentUserFavorites(ArrayList<MyEvent> list, EventListAdapter myAdapter, ProgressBar progressBar, TextView tvNoEvents) {
            // Mostra il progresso di caricamento
            progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() != null) {
                String currentUserId = auth.getCurrentUser().getUid();
                DatabaseReference usersReference = FirebaseDatabase.getInstance("https://pmappfirsttry-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("users");

                Query query = usersReference.child(currentUserId).child("preferiti").orderByKey();

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.getValue()==null) continue;
                            if (!(boolean)dataSnapshot.getValue()) continue;

                            // Ottieni l'ID dell'evento preferito dall'utente
                            String eventId = dataSnapshot.getKey();

                            // Cerca l'evento corrispondente nell'elenco degli eventi
                            DatabaseReference eventsReference = FirebaseDatabase.getInstance("https://pmappfirsttry-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference("events");
                            Query eventQuery = eventsReference.orderByChild("event_id").equalTo(Integer.parseInt(eventId));
                            eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot eventDataSnapshot : snapshot.getChildren()) {
                                        MyEvent event = eventDataSnapshot.getValue(MyEvent.class);
                                        list.add(event);
                                    }

                                    // Aggiorna l'adattatore e nascondi il progresso di caricamento
                                    myAdapter.setEventList(list);
                                    myAdapter.notifyDataSetChanged();

                                    progressBar.setVisibility(View.GONE);

                                    if (list.isEmpty()) {
                                        tvNoEvents.setVisibility(View.VISIBLE);
                                    } else {
                                        tvNoEvents.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressBar.setVisibility(View.GONE);

                                    if (list.isEmpty()) {
                                        tvNoEvents.setVisibility(View.VISIBLE);
                                    } else {
                                        tvNoEvents.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }

                        progressBar.setVisibility(View.GONE);
                        if (list.isEmpty()) {
                            tvNoEvents.setVisibility(View.VISIBLE);
                        } else {
                            tvNoEvents.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }

        public static void getUserEventsInserted(ArrayList<MyEvent> list, EventListAdapter myAdapter, ProgressBar progressBar, TextView tvNoEvents) {
            // Mostra il progresso di caricamento
            progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() != null) {
                String currentUserId = auth.getCurrentUser().getUid();
                DatabaseReference usersReference = FirebaseDatabase.getInstance("https://pmappfirsttry-default-rtdb.europe-west1.firebasedatabase.app/")
                        .getReference("users");

                Query query = usersReference.child(currentUserId).child("inserted_events").orderByKey();

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            // Ottieni l'ID dell'evento preferito dall'utente
                            String eventId = dataSnapshot.getKey();

                            // Cerca l'evento corrispondente nell'elenco degli eventi
                            DatabaseReference eventsReference = FirebaseDatabase.getInstance("https://pmappfirsttry-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference("events");
                            Query eventQuery = eventsReference.orderByChild("event_id").equalTo(Integer.parseInt(eventId));
                            eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot eventDataSnapshot : snapshot.getChildren()) {
                                        MyEvent event = eventDataSnapshot.getValue(MyEvent.class);
                                        list.add(event);
                                    }

                                    // Aggiorna l'adattatore e nascondi il progresso di caricamento
                                    myAdapter.setEventList(list);
                                    myAdapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);

                                    if (list.isEmpty()) {
                                        tvNoEvents.setVisibility(View.VISIBLE);
                                    } else {
                                        tvNoEvents.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressBar.setVisibility(View.GONE);

                                    if (list.isEmpty()) {
                                        tvNoEvents.setVisibility(View.VISIBLE);
                                    } else {
                                        tvNoEvents.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        progressBar.setVisibility(View.GONE);

                        if (list.isEmpty()) {
                            tvNoEvents.setVisibility(View.VISIBLE);
                        } else {
                            tvNoEvents.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);

                        if (list.isEmpty()) {
                            tvNoEvents.setVisibility(View.VISIBLE);
                        } else {
                            tvNoEvents.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }

        public static void addToInsertedEvents(MyEvent myEvent){

            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() != null) {
                String userId = auth.getCurrentUser().getUid();

                DatabaseReference insertedEventsRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(userId)
                        .child("inserted_events");
                String eventId = String.valueOf(myEvent.getEvent_id());

                insertedEventsRef.child(eventId).setValue(true);
            }
        }

        public static void removeFromInsertedEvents(EventListInterface listInterface, MyEvent myEvent, int position){

            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() != null) {
                String userId = auth.getCurrentUser().getUid();

                DatabaseReference insertedEventsRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(userId)
                        .child("inserted_events");
                String eventId = String.valueOf(myEvent.getEvent_id());

                insertedEventsRef.child(eventId).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error ==null){
                            DatabaseReference eventsRef = FirebaseDatabase.getInstance()
                                    .getReference("events")
                                    .child(eventId);
                            eventsRef.removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if(error == null){
                                        listInterface.onEventRemoved(true, position);
                                        Log.d("firebase wrapper", "removed from inserted events");
                                    }
                                    else {
                                        listInterface.onEventRemoved(false, position);
                                        Log.d("firebase wrapper", "error removed from inserted events");
                                    }
                                }
                            });
                        } else {
                            listInterface.onEventRemoved(false, position);
                            Log.d("firebase wrapper", " error removed from inserted events");
                        }
                    }
                });
            }
        }

    }
}
