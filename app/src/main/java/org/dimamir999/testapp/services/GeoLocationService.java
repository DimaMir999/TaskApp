package org.dimamir999.testapp.services;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by dimamir999 on 20.01.16.
 */
public class GeoLocationService {

    private Activity context;

    public GeoLocationService(Activity context) {
        this.context = context;
    }

    public Location getCurrentLocation() {
        //get my geo location
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);
            currentLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return currentLocation;
    }
}
