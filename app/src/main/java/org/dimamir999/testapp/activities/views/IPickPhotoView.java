package org.dimamir999.testapp.activities.views;

import android.app.Activity;
import android.graphics.Bitmap;

import org.dimamir999.testapp.model.PhotoWithGeoTag;

/**
 * Created by dimamir999 on 20.01.16.
 */
public interface IPickPhotoView {

    public void stopProgressBar();
    public void startProgressBar();
    public void setPhotoToView(Bitmap photo);
    public void showErrorMessage(String message);
    public void toListPhotosActivity(PhotoWithGeoTag photoObject);
    public Activity getContextActivity();

}
