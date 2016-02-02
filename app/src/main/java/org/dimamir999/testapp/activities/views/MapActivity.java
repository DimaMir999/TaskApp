package org.dimamir999.testapp.activities.views;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;

import org.dimamir999.testapp.R;
import org.dimamir999.testapp.activities.presenters.ListPhotosPresenter;
import org.dimamir999.testapp.activities.presenters.MapPresenter;
import org.dimamir999.testapp.model.PhotoWithGeoTag;

import java.util.ArrayList;


public class MapActivity extends Activity implements OnMapReadyCallback, IMapView {

    private GoogleMap map;
    private MapPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        presenter = new MapPresenter(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        Log.v("dimamir999", "MapActivity created");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        ArrayList<PhotoWithGeoTag> photoObjects = getIntent().getParcelableArrayListExtra(ListPhotosPresenter.PHOTO_OBJECTS);
        ArrayList<MarkerOptions> markers = presenter.makeMarkerOptionsFromList(photoObjects);
        for(MarkerOptions marker : markers){
            map.addMarker(marker);
        }
        if(photoObjects.size() != 0) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(markers.get(markers.size() - 1).getPosition())
                    .zoom(12).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}
