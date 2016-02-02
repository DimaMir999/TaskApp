package org.dimamir999.testapp.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
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
public class PhotoWithGeoTag implements Parcelable{

    private long id;
    private Bitmap photo;
    private VisitedPoint location;
    private String path;

    public PhotoWithGeoTag(String path, VisitedPoint location) {
        this.path = path;
        this.location = location;
    }

    public PhotoWithGeoTag(long id, String path, VisitedPoint location) {
        this.id = id;
        this.location = location;
        this.path = path;
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
        return location.getLongitude();
    }

    public double getLatitude() {
        return location.getLatitude();
    }


    public Date getDate() {
        return location.getDate();
    }


    public String getPath() {
        return path;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "PhotoWithGeoTag{" +
                "longitude=" + getLongitude() +
                ", latitude=" + getLatitude() +
                ", date=" + getDate() +
                ", path='" + path + '\'' +
                '}';
    }

    protected PhotoWithGeoTag(Parcel in) {
        id = in.readLong();
        photo = in.readParcelable(Bitmap.class.getClassLoader());
        location = in.readParcelable(VisitedPoint.class.getClassLoader());
        path = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable(photo, flags);
        dest.writeParcelable(location, flags);
        dest.writeString(path);
    }

    public static final Creator<PhotoWithGeoTag> CREATOR = new Creator<PhotoWithGeoTag>() {
        @Override
        public PhotoWithGeoTag createFromParcel(Parcel in) {
            return new PhotoWithGeoTag(in);
        }

        @Override
        public PhotoWithGeoTag[] newArray(int size) {
            return new PhotoWithGeoTag[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
