package org.dimamir999.testapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.dimamir999.testapp.model.PhotoWithGeoTag;
import org.dimamir999.testapp.model.VisitedPoint;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dimamir999 on 18.01.16.
 */
public class PhotoWithGeoTagDAO {

    private SQLiteOpenHelper connectionManager;

    public PhotoWithGeoTagDAO(Context context) {
        this.connectionManager = DBConnectionManager.getInstance(context);
    }

    public void add(PhotoWithGeoTag photoObject){
        SQLiteDatabase database = connectionManager.getWritableDatabase();
        ContentValues locationValues = new ContentValues();
        locationValues.put("date", photoObject.getDate().getTime());
        locationValues.put("latitude", photoObject.getLatitude());
        locationValues.put("longitude", photoObject.getLongitude());
        long locationId = database.insert("visited_points", null, locationValues);
        ContentValues photoValues = new ContentValues();
        photoValues.put("path", photoObject.getPath());
        photoValues.put("location_id", locationId);
        database.insert("photos", null, photoValues);
        Log.d("dimamir999", "1 row inserted to photos table");
    }

    public void delete(long id){
        SQLiteDatabase database = connectionManager.getWritableDatabase();
        database.delete("photos", "id = " + id, null);
        Log.d("dimamir999", "1 row deleted");
    }


    public ArrayList<PhotoWithGeoTag> getBetweenDates(Date startDate, Date endDate){
        SQLiteDatabase database = connectionManager.getReadableDatabase();
        ArrayList<PhotoWithGeoTag> result = new ArrayList<PhotoWithGeoTag>();
        String query = "SELECT p.id id_photo, p.path path, l.id id_loc, l.longitude longitude, " +
                "l.latitude latitude, l.date date " +
                "FROM photos p JOIN visited_points l " +
                "ON p.location_id = l.id AND l.date > ? AND l.date < ?;";
        String[] selectionArgs = new String[]{String.valueOf(startDate.getTime()), String.valueOf(endDate.getTime())};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        Log.d("dimamir999", "Query with params " + startDate + " " + endDate);
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id_photo");
            int idLocIndex = cursor.getColumnIndex("id_loc");
            int pathColIndex = cursor.getColumnIndex("path");
            int dateColIndex = cursor.getColumnIndex("date");
            int latitudeColIndex = cursor.getColumnIndex("latitude");
            int longitudeColIndex = cursor.getColumnIndex("longitude");
            do {
                VisitedPoint visitedPoint = new VisitedPoint(cursor.getLong(idLocIndex), cursor.getDouble(longitudeColIndex),
                        cursor.getDouble(latitudeColIndex), new Date(cursor.getLong(dateColIndex)));
                PhotoWithGeoTag photoObject = new PhotoWithGeoTag(cursor.getLong(idColIndex),
                        cursor.getString(pathColIndex), visitedPoint);
                result.add(photoObject);
            } while (cursor.moveToNext());
        } else
            Log.d("dimamir999", "0 rows");
        cursor.close();
        return  result;
    }

    public void closeConnection(){
        connectionManager.close();
    }

}
