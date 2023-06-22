package com.example.zenaparty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment {

    private OnMapReadyCallback callback;
    private boolean isMapInitialized = false;
    private GoogleMap googleMap;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "MapPrefs";
    private static final String PREF_CAMERA_LAT = "CameraLat";
    private static final String PREF_CAMERA_LNG = "CameraLng";
    private static final String PREF_CAMERA_ZOOM = "CameraZoom";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        callback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                if (isMapInitialized) {
                    // Ripristina la vista del fragment di Google Maps
                    // Esegui qui le operazioni necessarie per ripristinare la vista precedente

                    // Ripristina la posizione della camera
                    double lat = sharedPreferences.getFloat(PREF_CAMERA_LAT, 0f);
                    double lng = sharedPreferences.getFloat(PREF_CAMERA_LNG, 0f);
                    float zoom = sharedPreferences.getFloat(PREF_CAMERA_ZOOM, 0f);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(lat, lng))
                            .zoom(zoom)
                            .build();

                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    // La vista non è stata inizializzata in precedenza
                    // Esegui le operazioni di inizializzazione normale
                    LatLng genoa = new LatLng(44.414165, 8.942184);
                    googleMap.addMarker(new MarkerOptions().position(genoa).title("Marker in Genoa"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(genoa));
                    googleMap.setMinZoomPreference(12.0f);
                    googleMap.setMaxZoomPreference(20.0f);

                    isMapInitialized = true; // Imposta la vista come inizializzata
                }
            }
        };

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleMap != null) {
            // Salva la posizione della camera nelle SharedPreferences
            CameraPosition cameraPosition = googleMap.getCameraPosition();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(PREF_CAMERA_LAT, (float) cameraPosition.target.latitude);
            editor.putFloat(PREF_CAMERA_LNG, (float) cameraPosition.target.longitude);
            editor.putFloat(PREF_CAMERA_ZOOM, cameraPosition.zoom);
            editor.apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleMap != null && isMapInitialized) {
            // Ripristina la vista del fragment di Google Maps
            // Esegui qui le operazioni necessarie per ripristinare la vista precedente

            // Ripristina la posizione della camera
            double lat = sharedPreferences.getFloat(PREF_CAMERA_LAT, 0f);
            double lng = sharedPreferences.getFloat(PREF_CAMERA_LNG, 0f);
            float zoom = sharedPreferences.getFloat(PREF_CAMERA_ZOOM, 0f);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lng))
                    .zoom(zoom)
                    .build();

            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}
