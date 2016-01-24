package org.dimamir999.testapp.activities.presenters;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.dimamir999.testapp.activities.views.IMapView;
import org.dimamir999.testapp.model.PhotoWithGeoTag;

import java.util.ArrayList;

/**
 * Created by dimamir999 on 22.01.16.
 */
public class MapPresenter {

    private IMapView view;

    public MapPresenter(IMapView view) {
        this.view = view;
    }

    public ArrayList<MarkerOptions> makeMarkerOptionsFromList(ArrayList<PhotoWithGeoTag> photoObjects){
        ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();
        for(PhotoWithGeoTag userPhoto : photoObjects){
            markers.add(new MarkerOptions().position(new LatLng(userPhoto.getLatitude(), userPhoto.getLongitude()))
                    .title(userPhoto.getDate().toString()));
        }
        return markers;
    }

}
