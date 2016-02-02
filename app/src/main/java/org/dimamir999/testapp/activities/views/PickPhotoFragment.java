package org.dimamir999.testapp.activities.views;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.dimamir999.testapp.R;
import org.dimamir999.testapp.activities.presenters.PickPhotoPresenter;
import org.dimamir999.testapp.model.PhotoWithGeoTag;

/**
 * Created by dimamir999 on 19.01.16.
 */
public class PickPhotoFragment extends Fragment implements IPickPhotoView {

    public static final String PHOTO_OBJECT_CODE = "photo object";

    private PickPhotoPresenter presenter;

    private ImageView photoView;
    private ProgressBar uploadProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_picker, container, false);
        Button fromGalleryButton = (Button) view.findViewById(R.id.from_gallery_button);
        Button fromURLButton = (Button) view.findViewById(R.id.from_url_button);
        Button addPhotoButton = (Button) view.findViewById(R.id.add_photo_button);
        final TextView urlField = (TextView) view.findViewById(R.id.url_field);
        fromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("dimamir999", "handle click on from gallery button");
                loadPhotoFromGallery();
            }
        });
        fromURLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("dimamir999", "handle click on from URL button");
                presenter.loadPhotoFromURL(urlField.getText().toString());
            }
        });
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("dimamir999", "handle click on add photo button");
                presenter.addNewPhoto(photoView.getDrawable());
            }
        });
        presenter = new PickPhotoPresenter(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        photoView = (ImageView) getActivity().findViewById(R.id.photoView);
        uploadProgressBar = (ProgressBar) getActivity().findViewById(R.id.upload_progress_bar);
    }

    @Override
    public void stopProgressBar() {
        uploadProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void startProgressBar() {
        uploadProgressBar.setVisibility(ProgressBar.VISIBLE);
    }

    public void setPhotoToView(Bitmap photo){
        photoView.setImageBitmap(photo);
    }

    public void loadPhotoFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PickPhotoPresenter.PICK_PHOTO_REQUEST);
    }

    public void showErrorMessage(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    public void toListPhotosActivity(PhotoWithGeoTag photoObject){
        Intent intent = new Intent();
        intent.putExtra(PHOTO_OBJECT_CODE, photoObject);
        getActivity().setResult(Activity.RESULT_OK ,intent);
        getActivity().finish();
    }

    public Activity getContextActivity(){
        return  getActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.pickInGalery(requestCode, resultCode, data, getActivity());
    }
}
