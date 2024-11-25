package com.example.projectv2.View;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectv2.Controller.topBarUtils;
import com.example.projectv2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private static final String TAG = "LocationActivity";
    private MapView mapView;
    private GoogleMap googleMap;

    private FirebaseFirestore firestore;
    private String eventId; // Event ID passed from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        topBarUtils.topBarSetup(this, "Location", View.VISIBLE);

        mapView = findViewById(R.id.mapView);
        firestore = FirebaseFirestore.getInstance();

        // Retrieve the event ID from the intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.onResume(); // For rendering the map immediately
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap map) {
                    googleMap = map;
                    fetchParticipantLocations();
                }
            });
        }
    }

    /**
     * Fetches participant locations for the specified event and displays them on the map.
     */
    private void fetchParticipantLocations() {
        firestore.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch the Waiting list from entrantList
                        List<String> participantIds = (List<String>) documentSnapshot.get("entrantList.Waiting");

                        if (participantIds != null && !participantIds.isEmpty()) {
                            for (String userId : participantIds) {
                                fetchUserLocation(userId); // Fetch and map each user's location
                            }
                        } else {
                            Toast.makeText(this, "No participants found for this event.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching participant list", e);
                    Toast.makeText(this, "Error fetching participant list.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Fetches the location of a user and maps it to a city on the organizer's map.
     *
     * @param userId The user ID of the participant.
     */
    private void fetchUserLocation(String userId) {
        firestore.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double latitude = documentSnapshot.getDouble("latitude");
                        Double longitude = documentSnapshot.getDouble("longitude");

                        if (latitude != null && longitude != null) {
                            Log.d(TAG, "Fetched location for user " + userId + ": (" + latitude + ", " + longitude + ")");
                            mapUserLocation(latitude, longitude);
                        } else {
                            Log.w(TAG, "No location data found for user: " + userId);
                        }
                    } else {
                        Log.w(TAG, "User document not found for userId: " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user location", e);
                });
    }

    /**
     * Maps the user's location to the city and adds a marker on the organizer's map.
     *
     * @param latitude  Latitude of the user's location.
     * @param longitude Longitude of the user's location.
     */
    private void mapUserLocation(double latitude, double longitude) {
        if (googleMap == null) {
            Log.e(TAG, "GoogleMap is not initialized!");
            return;
        }

        LatLng userLatLng = new LatLng(latitude, longitude);

        // Use Geocoder to get the city name
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                Log.d(TAG, "Mapping user location: " + city + " (" + latitude + ", " + longitude + ")");
                googleMap.addMarker(new MarkerOptions().position(userLatLng).title(city));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 10)); // Focus on the first location
            } else {
                Log.w(TAG, "No address found for location: (" + latitude + ", " + longitude + ")");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error mapping user location", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}
