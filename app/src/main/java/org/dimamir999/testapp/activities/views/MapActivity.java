package org.dimamir999.testapp.activities.views;

import android.app.Activity;
import android.app.IntentService;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.dimamir999.testapp.R;
import org.dimamir999.testapp.activities.presenters.ListPhotosPresenter;
import org.dimamir999.testapp.activities.presenters.MapPresenter;
import org.dimamir999.testapp.model.PhotoWithGeoTag;
import org.dimamir999.testapp.model.VisitedPoint;
import org.dimamir999.testapp.services.CurrentDateService;

import java.util.ArrayList;
import java.util.Date;


public class MapActivity extends Activity implements OnMapReadyCallback, IMapView {

    private final static int ROUTE_LOADER_ID = 0;

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

        CurrentDateService dateService = new CurrentDateService();
        Date startDate = dateService.getCurrentDateStart();
        Date endDate = dateService.getCurrentDateEnd();
        ArrayList<VisitedPoint> visitedPoints = presenter.getVisitedPoints(this,startDate, endDate);
        map.addPolyline(presenter.getPathFromPoints(visitedPoints));

        if(photoObjects.size() != 0) {
            int size = getResources().getDisplayMetrics().widthPixels;
            LatLngBounds latLngBounds = presenter.getBoundsOfPath().build();
            CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds,size, size, 35);
            map.moveCamera(track);
        }
    }

    private void drawPath(GoogleMap map){
        ArrayList<VisitedPoint> visitedPoints = new ArrayList<VisitedPoint>();
        visitedPoints.add(new VisitedPoint(12, 24, 10));
        visitedPoints.add(new VisitedPoint(42, 34, 11));
        visitedPoints.add(new VisitedPoint(28, 33, 12));
        visitedPoints.add(new VisitedPoint(86, 9, 13));
        visitedPoints.add(new VisitedPoint(13, 102, 14));
        PolylineOptions line = new PolylineOptions();
        line = line.width(6f).color(Color.argb(200, 232, 79, 79));
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        for (VisitedPoint vp : visitedPoints) {
            LatLng location = new LatLng(vp.getLatitude(), vp.getLongitude());
            line.add(location);
            latLngBuilder.include(location);
        }
        map.addPolyline(line);
        int size = getResources().getDisplayMetrics().widthPixels;
        LatLngBounds latLngBounds = latLngBuilder.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds,size, size, 35);
        map.moveCamera(track);
    }
}
