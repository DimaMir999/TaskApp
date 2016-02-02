package org.dimamir999.testapp.activities.presenters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import org.dimamir999.testapp.activities.views.IPickPhotoView;
import org.dimamir999.testapp.db.PhotoWithGeoTagDAO;
import org.dimamir999.testapp.model.PhotoWithGeoTag;
import org.dimamir999.testapp.model.VisitedPoint;
import org.dimamir999.testapp.services.GeoLocationService;
import org.dimamir999.testapp.services.PhotoSaver;
import org.dimamir999.testapp.services.PhotoScaler;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * Created by dimamir999 on 20.01.16.
 */
public class PickPhotoPresenter {

    public final static int PICK_PHOTO_REQUEST = 1;
    private final static int MAX_PIXEL_LENGTH = 1024;

    private PhotoScaler photoScaler = new PhotoScaler();
    private PhotoWithGeoTagDAO photosDAO;

    private IPickPhotoView view;

    public PickPhotoPresenter(IPickPhotoView view) {
        this.view = view;
        this.photosDAO = new PhotoWithGeoTagDAO(view.getContextActivity().getApplicationContext());
    }

    public void loadPhotoFromURL(String url) {
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.execute(url);
    }

    public void addNewPhoto(Drawable drawable) {
        Bitmap photo;
        if (drawable == null) {
            view.showErrorMessage("Please load photo");
        } else {
            photo = ((BitmapDrawable) drawable).getBitmap();
            GeoLocationService locationService = new GeoLocationService(view.getContextActivity());
            Location location = locationService.getCurrentLocation();
            Date currentDate = new Date(System.currentTimeMillis());
            if (location != null) {
                PhotoSaver saver = new PhotoSaver(view.getContextActivity());
                String path = saver.savePhoto(photo);
                if (path == null) {
                    view.showErrorMessage("Cant save photo");
                } else {
                    PhotoWithGeoTag userPhoto = new PhotoWithGeoTag(path, new VisitedPoint(location.getLongitude(), location.getLatitude(),
                            currentDate.getTime()));
                    photosDAO.add(userPhoto);
                    Log.v("dimamir999", userPhoto.toString());
                    view.toListPhotosActivity(userPhoto);
                }
            } else {
                view.showErrorMessage("Turn on GPS or internet");
            }
        }
    }



    public void pickInGalery(int requestCode, int resultCode, Intent data, Activity activity){
        Bitmap photo = null;
        if (requestCode == PICK_PHOTO_REQUEST && resultCode == activity.RESULT_OK) {
            Uri selectedPhoto = data.getData();
            try {
                // sometimes out of memory exeption maybe make some limit for size of photo
                photo = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resultCode == activity.RESULT_CANCELED) {
            // Operation failed or cancelled. Handle in your own way.
        }
        else {
            //scale because open gl cant load photo more than 4096 px
            photo = photoScaler.scaleForBigSize(photo, MAX_PIXEL_LENGTH);
            view.setPhotoToView(photo);
        }
    }

    public class ImageLoader extends AsyncTask<String, Void, Bitmap> {

        private static final int OK = 0;
        private static final int BIG_FILE = 1;
        private static final int INCORRECT_PHOTO_FORMAT = 2;
        private static final int INCORRECT_URL = 3;
        private static final int MAX_SIZE = 20 * 1024;

        private int answerCode;

        @Override
        protected void onPreExecute() {
            view.startProgressBar();
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                Log.v("dimamir999", urls[0]);
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                if(connection.getContentLength() > MAX_SIZE){
                    answerCode = BIG_FILE;
                    return null;
                } else {
                    Bitmap photo = BitmapFactory.decodeStream(connection.getInputStream());
                    if(photo == null)
                        answerCode = INCORRECT_PHOTO_FORMAT;
                    else {
                        answerCode = OK;
                    }
                    return photo;
                }
            } catch (IOException e) {
                answerCode = INCORRECT_URL;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            view.stopProgressBar();
            switch (answerCode){
                case OK:
                    result = photoScaler.scaleForBigSize(result, MAX_PIXEL_LENGTH);
                    view.setPhotoToView(result);
                    break;
                case BIG_FILE:
                    view.showErrorMessage("File size more than max size " + MAX_SIZE + "bytes");
                    break;
                case INCORRECT_PHOTO_FORMAT:
                    view.showErrorMessage("Incorrect file format");
                    break;
                case INCORRECT_URL:
                    view.showErrorMessage("Incorrect url or bad internet connection");
                    break;
            }
        }
    }
}
