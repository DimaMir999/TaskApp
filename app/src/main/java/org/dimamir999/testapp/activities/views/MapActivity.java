package org.dimamir999.testapp.activities.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;

import org.dimamir999.testapp.R;
import org.dimamir999.testapp.activities.presenters.MapPresenter;
import org.dimamir999.testapp.services.LocationControlService;

import java.util.ArrayList;


public class MapActivity extends Activity implements OnMapReadyCallback {

    private GoogleMap map;
    private MapPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        presenter = new MapPresenter();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        startLocationControlService();
        Log.v("dimamir999", "MapActivity created");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        ArrayList<MarkerOptions> markers = getIntent().getParcelableArrayListExtra("markers");
        for(MarkerOptions marker : markers){
            map.addMarker(marker);
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(markers.get(markers.size() - 1).getPosition())
                .zoom(13).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private void startLocationControlService(){
        Thread serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(MapActivity.this, LocationControlService.class));
                Log.v("dimair999", "start service from map activity");
            }
        });
        serviceThread.start();
    }
}
