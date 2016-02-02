package org.dimamir999.testapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dimamir999 on 18.01.16.
 */
public class DBConnectionManager extends SQLiteOpenHelper{

    private final static String DB_NAME = "GeoPhotos";
    private final static String PHOTOS_TABLE_CREATION = "create table photos ( id integer primary key" +
            " autoincrement, path text, location_id integer, FOREIGN KEY(location_id) REFERENCES visited_point(id) );";
    private final static String VISITED_PHOTOS_TABLE_CREATION = "create table visited_points (id integer primary key" +
            " autoincrement, longitude real, latitude real, date integer);";

    private static DBConnectionManager dbHelper;

    public static synchronized DBConnectionManager getInstance(Context context){
        if(dbHelper == null){
            dbHelper = new DBConnectionManager(context);
        }
        return dbHelper;
    }

    private DBConnectionManager(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d("dimamir999", "Creation of database");
        database.execSQL(VISITED_PHOTOS_TABLE_CREATION);
        database.execSQL(PHOTOS_TABLE_CREATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
