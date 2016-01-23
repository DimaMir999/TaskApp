package org.dimamir999.testapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.dimamir999.testapp.model.PhotoWithGeoTag;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dimamir999 on 18.01.16.
 */
public class PhotoWithGeoTagDAO {

    //should  be one object of database
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;

    public PhotoWithGeoTagDAO(Context context) {
        this.dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void add(PhotoWithGeoTag photoObject){
        ContentValues contentValues = new ContentValues();
        contentValues.put("path", photoObject.getPath());
        contentValues.put("date", photoObject.getDate().getTime());
        contentValues.put("latitude", photoObject.getLatitude());
        contentValues.put("longitude", photoObject.getLongitude());
        // вставляем запись и получаем ее ID
        long rowID = database.insert("photos", null, contentValues);
        Log.d("dimamir999", "1 row inserted to photos table");
    }

    public void delete(long id){
        database.delete("photos", "id = " + id, null);
        Log.d("dimamir999", "1 row deleted");
    }


    public ArrayList<PhotoWithGeoTag> getBetweenDates(Date startDate, Date endDate){

        ArrayList<PhotoWithGeoTag> result = new ArrayList<PhotoWithGeoTag>();
        String selectionString = "date > ? AND date < ?";
        String[] selectionArgs = new String[]{String.valueOf(startDate.getTime()), String.valueOf(endDate.getTime())};
        Cursor cursor = database.query("photos", null,selectionString , selectionArgs, null, null, null);

        Log.d("dimamir999", "Query with params " + startDate + " " + endDate);
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id");
            int pathColIndex = cursor.getColumnIndex("path");
            int dateColIndex = cursor.getColumnIndex("date");
            int latitudeColIndex = cursor.getColumnIndex("latitude");
            int longitudeColIndex = cursor.getColumnIndex("longitude");
            do {
                PhotoWithGeoTag photoObject = new PhotoWithGeoTag(cursor.getLong(idColIndex),
                        cursor.getString(pathColIndex), cursor.getDouble(longitudeColIndex),
                        cursor.getDouble(latitudeColIndex), new Date(cursor.getLong(dateColIndex)));
                result.add(photoObject);
            } while (cursor.moveToNext());
        } else
            Log.d("dimamir999", "0 rows");

        return  result;
    }

}
