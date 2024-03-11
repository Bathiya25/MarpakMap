package com.example.marpakmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private SupportMapFragment mapFragment;
    private List<LatLng> markedList = new ArrayList<>();
    private List<LatLng> polylineList = new ArrayList<>();
    Polygon polygon = null;
    private Button addButton;
    private Button resetButton;

    private HashMap<String, LatLng> markersList = new HashMap<>();

    private boolean canAddPolgen = true;
    private boolean canAddPolyline = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addButton = (Button) findViewById(R.id.add_polygen);
        resetButton = (Button) findViewById(R.id.reset);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        buttonObservers();
    }

    private void buttonObservers() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButton.setEnabled(false);
                resetButton.setEnabled(true);
                canAddPolgen = false;
                canAddPolyline = true;
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (polygon != null) {
                    canAddPolgen = true;
                    canAddPolyline = false;
                    markedList.clear();
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;
        LatLng slMap = new LatLng(7.8731, 80.7718);
        this.gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(slMap, 10.0f));

            this.gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    if (canAddPolgen) {
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true);
                        Marker marker = gMap.addMarker(markerOptions);
                        markersList.put(marker.getId(), latLng);
                        //markedList.add(latLng);
                        createPolygen(markersList);
                    }

                    if (canAddPolyline) {
                        polylineList.add(latLng);
                        addPolyLine(polylineList);
                    }
                }
            });

            this.gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                    markersList.replace(marker.getId(), marker.getPosition());
                    polygon.remove();
                    createPolygen(markersList);
                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {
                    //markersList.remove(marker.getId());

                }
            });
    }


    private void createPolygen(HashMap<String, LatLng> list) {
        PolygonOptions polygonOptions = new PolygonOptions().addAll(list.values()).clickable(true);
        polygon = this.gMap.addPolygon(polygonOptions);
        polygon.setFillColor(Color.rgb(95,123,177));
        polygon.setStrokeColor(Color.rgb(95,123,238));

        if (list.values().size() > 2) {
            addButton.setEnabled(true);
        }
    }

    private void addPolyLine(List<LatLng> list) {
        this.gMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(1)
                .color(Color.rgb(95,123,238)).addAll(list));
    }
}