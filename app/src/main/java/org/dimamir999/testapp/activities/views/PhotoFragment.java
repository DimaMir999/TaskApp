package org.dimamir999.testapp.activities.views;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.dimamir999.testapp.R;


public class PhotoFragment extends Fragment{

    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        imageView = (ImageView)view.findViewById(R.id.photoView);
        if(savedInstanceState != null)
            imageView.setImageBitmap(savedInstanceState.<Bitmap>getParcelable("photo"));
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(imageView.getDrawable() != null)
            outState.putParcelable("photo", ((BitmapDrawable)imageView.getDrawable()).getBitmap());
    }
}
