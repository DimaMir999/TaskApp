package org.dimamir999.testapp.activities.presenters;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.dimamir999.testapp.activities.views.IMapView;
import org.dimamir999.testapp.db.VisitedPointDAO;
import org.dimamir999.testapp.model.PhotoWithGeoTag;
import org.dimamir999.testapp.model.VisitedPoint;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dimamir999 on 22.01.16.
 */
public class MapPresenter {

    private IMapView view;

    public MapPresenter(IMapView view) {
        this.view = view;
    }

    public ArrayList<VisitedPoint> getVisitedPoints(Context context, Date startDate, Date endDate){
        VisitedPointDAO visitedPointDAO = new VisitedPointDAO(context);
        return  visitedPointDAO.getBetweenDates(startDate, endDate);
    }

    public ArrayList<MarkerOptions> makeMarkerOptionsFromList(ArrayList<PhotoWithGeoTag> photoObjects){
        ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();
        for(PhotoWithGeoTag userPhoto : photoObjects){
            markers.add(new MarkerOptions().position(new LatLng(userPhoto.getLatitude(), userPhoto.getLongitude()))
                    .title(userPhoto.getDate().toString()));
        }
        return markers;
    }

    public PolylineOptions getPathFromPoints(ArrayList<VisitedPoint> visitedPoints){
        PolylineOptions line = new PolylineOptions();
        line = line.width(6f).color(Color.argb(200, 232, 79, 79));
        latLngBuilder = new LatLngBounds.Builder();
        for (VisitedPoint vp : visitedPoints) {
            LatLng location = new LatLng(vp.getLatitude(), vp.getLongitude());
            line.add(location);
            latLngBuilder.include(location);
        }
        return line;
    }

    private LatLngBounds.Builder latLngBuilder;

    //invoke this method after getPathFromPoints
    public LatLngBounds.Builder getBoundsOfPath(){
        return latLngBuilder;
    }
}
