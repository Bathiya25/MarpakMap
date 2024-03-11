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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private List<LatLng> markedList = new ArrayList<>();
    private List<LatLng> polylineList = new ArrayList<>();
    Polygon polygon = null;
    private Button addButton;
    private Button resetButton;

    private boolean canAddPolgen = true;
    private boolean canAddPolyline = false;

    LiveData<Boolean> isPolygenCreated = new MediatorLiveData<>();

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
        this.googleMap = googleMap;
        LatLng slMap = new LatLng(7.8731, 80.7718);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(slMap, 10.0f));

            this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    if (canAddPolgen) {
                        markedList.add(latLng);
                        createPolygen(markedList);
                    }

                    if (canAddPolyline) {
                        polylineList.add(latLng);
                        addPolyLine(polylineList);
                    }
                }
            });
    }


    private void createPolygen(List<LatLng> list) {
        PolygonOptions polygonOptions = new PolygonOptions().addAll(list).clickable(true);
        polygon = this.googleMap.addPolygon(polygonOptions);
        polygon.setFillColor(Color.rgb(95,123,177));
        polygon.setStrokeColor(Color.rgb(95,123,238));

        if (list.size() > 2) {
            addButton.setEnabled(true);
        }
    }

    private void addPolyLine(List<LatLng> list) {
        googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(1)
                .color(Color.rgb(95,123,238)).addAll(list));
    }
}