package org.dimamir999.testapp.model;

import java.util.Date;

/**
 * Created by dimamir999 on 02.02.16.
 */
public class VisitedPoint {

    private long id;
    private double longitude;
    private double latitude;
    private Date date;

    public VisitedPoint(double longitude, double latitude, long date) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = new Date(date);
    }

    public VisitedPoint(long id, double longitude, double latitude, Date date) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
