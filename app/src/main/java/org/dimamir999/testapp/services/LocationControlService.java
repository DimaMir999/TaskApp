package org.dimamir999.testapp.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.dimamir999.testapp.activities.presenters.ListPhotosPresenter;
import org.dimamir999.testapp.activities.views.ListPhotosActivity;

public class LocationControlService extends Service {

    public static final String DISTANCE_CODE = "passed way";
    public static boolean isRunning = false;

    private MyLocationListener locationListener;
    private LocationManager locationManager;
    private Location lastLocation;
    private double passedWay;
    private PendingIntent pendingIntent;

    @Override
    public void onCreate()
    {
        super.onCreate();
        isRunning = true;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        //try to check location every 2 min and notify if location changed if delta more than 100 meters
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20 * 1000, 20,
//                this.locationListener);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 120, 100,
//                this.locationListener);
//        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000 * 120, 100,
//                this.locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                this.locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                this.locationListener);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0,
                this.locationListener);
        Log.v("dimamir999", "LocationControlService started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            pendingIntent = intent.getParcelableExtra(ListPhotosPresenter.PENDING_INTENT_CODE);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private final class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location location) {
            if(lastLocation != null)
                //paased way in km
                passedWay += location.distanceTo(lastLocation) * 0.001;
            lastLocation = location;
            if(pendingIntent != null) {
                Intent intent = new Intent().putExtra(DISTANCE_CODE, passedWay);
                try {
                    pendingIntent.send(LocationControlService.this, ListPhotosActivity.DISTANCE_RESPONSE, intent);
                    Log.v("dimamir999", "send successful");
                } catch (PendingIntent.CanceledException e) {
                    Log.v("dimamir999", "send of way canceled");
                    e.printStackTrace();
                }
            }
            Log.v("dimamir999", "Current location " + location.getLatitude() + " " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.v("dimamir999", "Provider " + provider + " change status to " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.v("dimamir999", "Provider " + provider + " enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.v("dimamir999", "Provider " + provider + " disabled");
        }
    }

    @Override
    public void onDestroy() {
        Log.v("dimamir999", "LocationControlService stoped");
        isRunning = false;
        locationManager.removeUpdates(this.locationListener);
    }
}
