package org.dimamir999.testapp.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.dimamir999.testapp.R;

import java.util.Date;

/**
 * Created by dimamir999 on 17.01.16.
 */
public class PhotoWithGeoTag {

    private long id;
    private Bitmap photo;
    private double longitude;
    private double latitude;
    private Date date;
    private String path;

    public PhotoWithGeoTag(String path, double longitude, double latitude, Date date) {
        this.path = path;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
    }

    public PhotoWithGeoTag(long id, String path, double longitude, double latitude, Date date) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
        this.path = path;
    }

    public MarkerOptions makeMarkerOption(Activity activity){
        ImageView imageView = (ImageView) activity.findViewById(R.id.photo);
        imageView.setImageBitmap(photo);
        return new MarkerOptions().position(new LatLng(latitude, longitude)).icon(
                BitmapDescriptorFactory.fromBitmap(photo));
    }

    public Bitmap getPhoto() {
        if(photo == null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap == null) {
                Log.v("dimamir999", "incorrect format of photo");
            } else {
                Log.v("dimamir999", "photo loaded");
                return bitmap;
            }
        }
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "PhotoWithGeoTag{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", date=" + date +
                ", path='" + path + '\'' +
                '}';
    }
}
