package org.dimamir999.testapp.activities.presenters;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.dimamir999.testapp.activities.views.IListPhotoView;
import org.dimamir999.testapp.db.PhotoWithGeoTagDAO;
import org.dimamir999.testapp.model.PhotoWithGeoTag;
import org.dimamir999.testapp.services.CurrentDateService;
import org.dimamir999.testapp.services.LocationControlService;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by dimamir999 on 21.01.16.
 */
public class ListPhotosPresenter {

    public static final String PENDING_INTENT_CODE = "pending intent";
    public static final String PHOTO_OBJECTS = "photo objects";

    private IListPhotoView view;
    private PhotoWithGeoTagDAO photoWithGeoTagDAO;
    private ArrayList<PhotoWithGeoTag> viewedPhotos;



    public ListPhotosPresenter(IListPhotoView view) {
        this.view = view;
        photoWithGeoTagDAO = new PhotoWithGeoTagDAO(view.getContextActivity().getApplicationContext());
    }

    public ArrayList<PhotoWithGeoTag> getListData(){
        // get date current date(start of the day and end of the day)
        CurrentDateService dateService = new CurrentDateService();
        Date startDate = dateService.getCurrentDateStart();
        Date endDate = dateService.getCurrentDateEnd();
        viewedPhotos = photoWithGeoTagDAO.getBetweenDates(startDate, endDate);
        return viewedPhotos;
    }

    public void deletePhoto(int position){
        PhotoWithGeoTag photoObject = viewedPhotos.remove(position);
        AsyncRemover remover = new AsyncRemover();
        remover.execute(photoObject.getId());
    }

    public void addNewItem(PhotoWithGeoTag photoObject){
        viewedPhotos.add(photoObject);
    }

    private class AsyncRemover extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... ids) {
            photoWithGeoTagDAO.delete(ids[0]);
            return null;
        }
    }

    public void startLocationControlService(){
        Thread serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Activity context = view.getContextActivity();
                Intent intent = new Intent(context, LocationControlService.class);
                PendingIntent pendingIntent = context.createPendingResult(0, new Intent(), 0);;
                intent.putExtra(PENDING_INTENT_CODE, pendingIntent);
                context.startService(intent);
            }
        });
        serviceThread.start();
    }

    public void сloseDBconnection(){
        photoWithGeoTagDAO.closeConnection();
    }
}
