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
public class VisitedPointDAO {

    private SQLiteOpenHelper connectionManager;

    public VisitedPointDAO(Context context) {
        this.connectionManager = DBConnectionManager.getInstance(context);
    }

    public void add(VisitedPoint visitedPoint){
        SQLiteDatabase database = connectionManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", visitedPoint.getDate().getTime());
        contentValues.put("latitude", visitedPoint.getLatitude());
        contentValues.put("longitude", visitedPoint.getLongitude());
        database.insert("visited_points", null, contentValues);
        Log.d("dimamir999", "1 row inserted to visited_points table");
    }

    public ArrayList<VisitedPoint> getBetweenDates(Date startDate, Date endDate){
        SQLiteDatabase database = connectionManager.getReadableDatabase();
        ArrayList<VisitedPoint> result = new ArrayList<VisitedPoint>();
        String selectionString = "date > ? AND date < ?";
        String[] selectionArgs = new String[]{String.valueOf(startDate.getTime()), String.valueOf(endDate.getTime())};
        Cursor cursor = database.query("visited_points", null,selectionString , selectionArgs, null, null, null);

        Log.d("dimamir999", "Query with params " + startDate + " " + endDate);
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id");
            int dateColIndex = cursor.getColumnIndex("date");
            int latitudeColIndex = cursor.getColumnIndex("latitude");
            int longitudeColIndex = cursor.getColumnIndex("longitude");
            do {
                VisitedPoint visitedPoint = new VisitedPoint(cursor.getLong(idColIndex), cursor.getDouble(longitudeColIndex),
                        cursor.getDouble(latitudeColIndex), new Date(cursor.getLong(dateColIndex)));
                result.add(visitedPoint);
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
