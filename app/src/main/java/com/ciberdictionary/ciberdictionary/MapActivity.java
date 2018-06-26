package com.ciberdictionary.ciberdictionary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by inmobitec on 6/25/18.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Marker marker;
    private GoogleMap gmap;
    String latitude = "";
    String longitude = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //RECIBO PARAMETROS
        Bundle b = getIntent().getExtras();

        if(b != null){
            latitude = b.getString("latitude");
            longitude = b.getString("longitude");
        }

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        //obtengo ubicacion de data obtenida de servicio API
        LatLng ubicacion = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));


        googleMap.getUiSettings().setCompassEnabled(Boolean.FALSE);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(Boolean.FALSE);
        googleMap.getUiSettings().setMapToolbarEnabled(Boolean.FALSE);
        googleMap.getUiSettings().setMyLocationButtonEnabled(Boolean.FALSE);
        googleMap.getUiSettings().setTiltGesturesEnabled(Boolean.FALSE);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 13));

        marker = googleMap.addMarker(new MarkerOptions()
                .position(ubicacion)
                .draggable(false)
                .title("Esmirna - Turquia"));
    }
}
