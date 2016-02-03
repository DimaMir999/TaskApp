package org.dimamir999.testapp.activities.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.dimamir999.testapp.R;
import org.dimamir999.testapp.activities.presenters.ListPhotosPresenter;
import org.dimamir999.testapp.model.PhotoWithGeoTag;
import org.dimamir999.testapp.services.CurrentDateService;
import org.dimamir999.testapp.services.LocationControlService;
import org.dimamir999.testapp.services.PhotoScaler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListPhotosActivity extends Activity implements IListPhotoView {

    private static final int DELETE_ITEM_ID = 1;
    private static final String ATTRIBUTE_NAME_DATE = "date";
    private static final String ATTRIBUTE_NAME_IMAGE = "image";
    private static final int LIST_PHOTO_HEIGHT = 250;
    private static final int LIST_PHOTO_WIDTH = 250;
    public static final int DISTANCE_RESPONSE = 2;
    private static final int PICK_PHOTO_REQUEST_CODE = 1;

    private ListPhotosPresenter presenter;
    private ArrayList<Map<String, Object>> listData;
    private SimpleAdapter adapter;
    private PhotoScaler photoScaler = new PhotoScaler();

    private ListView photosListView;
    private TextView dictanceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_photos);
        presenter = new ListPhotosPresenter(this);
        photosListView = (ListView) findViewById(R.id.photos_list);
        dictanceView = (TextView) findViewById(R.id.distance_view);

        if(LocationControlService.isRunning)
            presenter.startLocationControlService();

        if(savedInstanceState == null) {
            listData = new ArrayList<Map<String, Object>>();
            ArrayList<PhotoWithGeoTag> photosList = presenter.getListData();
            for (int i = 0; i < photosList.size(); i++) {
                Map<String, Object> itemData = new HashMap<String, Object>();
                PhotoWithGeoTag photoObject = photosList.get(i);
                itemData.put(ATTRIBUTE_NAME_DATE, photoObject.getDate().toString());
                Bitmap photo = photoObject.getPhoto();
                Log.v("dimamir999", "photo loaded");
                if (photo.getHeight() != LIST_PHOTO_HEIGHT || photo.getWidth() != LIST_PHOTO_WIDTH) {
                    Log.v("dimamir999", "photo scaled");
                    photo = photoScaler.scaleForList(photo, LIST_PHOTO_HEIGHT, LIST_PHOTO_WIDTH);
                }
                itemData.put(ATTRIBUTE_NAME_IMAGE, photo);
                listData.add(itemData);
            }
        } else {
            ArrayList<String> dates = savedInstanceState.getStringArrayList(ATTRIBUTE_NAME_DATE);
            ArrayList<Bitmap> scaledPhotos = savedInstanceState.getParcelableArrayList(ATTRIBUTE_NAME_IMAGE);
            ArrayList<Map<String, Object>> restoredData = new ArrayList<>();
            for(int i = 0;i < dates.size();i++){
                Map<String, Object> itemData = new HashMap<>();
                itemData.put(ATTRIBUTE_NAME_DATE, dates.get(i));
                itemData.put(ATTRIBUTE_NAME_IMAGE, scaledPhotos.get(i));
                restoredData.add(itemData);
            }
            listData = restoredData;
            Log.v("dimamir999", "restore list data");
        }

        String[] from = {ATTRIBUTE_NAME_DATE,
                ATTRIBUTE_NAME_IMAGE };
        int[] to = { R.id.date_text_view,  R.id.photo_item_view};
        adapter = new SimpleAdapter(this, listData, R.layout.photo_list_item,
                from, to);
        adapter.setViewBinder(new PhotoBinder());
        photosListView.setAdapter(adapter);
        registerForContextMenu(photosListView);
    }

    private void setServiceButtonText(){
        if(LocationControlService.isRunning) {
            ((TextView) findViewById(R.id.turn_service_button)).setText(R.string.passive_service);
        } else {
            ((TextView) findViewById(R.id.turn_service_button)).setText(R.string.active_service);
        }
    }

    @Override
    protected void onStart() {
        setServiceButtonText();
        super.onStart();
    }

    public void toPickPhotoActivity(View view){
        Intent intent = new Intent(this, PhotoPickActivity.class);
        startActivityForResult(intent, PICK_PHOTO_REQUEST_CODE);
    }

    public void toMapActivity(View view){
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(ListPhotosPresenter.PHOTO_OBJECTS, presenter.getListData());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == DISTANCE_RESPONSE){
            double newDistance = data.getDoubleExtra(LocationControlService.DISTANCE_CODE, -1);
            if(newDistance != -1) {
                dictanceView.setText("Total distance: " + String.format("%.3f", newDistance)
                        + " km");
            }
            Log.v("dimamir999", "successful recieve of the way");
        }
        else if(requestCode == PICK_PHOTO_REQUEST_CODE && resultCode == RESULT_OK){
            Log.v("dimamir999", "photo arrived");
            PhotoWithGeoTag photoObject = data.getParcelableExtra(PickPhotoFragment.PHOTO_OBJECT_CODE);
            Bitmap photo = photoObject.getPhoto();
            Log.v("dimamir999", "photo loaded");
            presenter.addNewItem(photoObject);
            if(photo.getHeight() != LIST_PHOTO_HEIGHT || photo.getWidth() != LIST_PHOTO_WIDTH) {
                Log.v("dimamir999", "photo scaled");
                photo = photoScaler.scaleForList(photo, LIST_PHOTO_HEIGHT, LIST_PHOTO_WIDTH);
            }
            Map<String, Object> itemMap = new HashMap<String, Object>();
            itemMap.put(ATTRIBUTE_NAME_DATE, photoObject.getDate().toString());
            itemMap.put(ATTRIBUTE_NAME_IMAGE, photo);
            this.listData.add(itemMap);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ITEM_ID, 0, R.string.delete_item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == DELETE_ITEM_ID) {
            AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            listData.remove(menuInfo.position);
            presenter.deletePhoto(menuInfo.position);
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> dates = new ArrayList<String>();
        ArrayList<Bitmap> scaledPhotos = new ArrayList<Bitmap>();
        for(Map<String, Object> itemData :listData){
            dates.add((String) itemData.get(ATTRIBUTE_NAME_DATE));
            scaledPhotos.add((Bitmap) itemData.get(ATTRIBUTE_NAME_IMAGE));
        }
        outState.putStringArrayList(ATTRIBUTE_NAME_DATE, dates);
        outState.putParcelableArrayList(ATTRIBUTE_NAME_IMAGE, scaledPhotos);
        Log.v("dimamir999", "save list data");
    }

    public void changeGeoLocationServiceStatus(View view){
        if(LocationControlService.isRunning) {
            stopService(new Intent(this, LocationControlService.class));
            ((TextView) view).setText(R.string.active_service);
        } else {
            presenter.startLocationControlService();
            ((TextView) view).setText(R.string.passive_service);
            Log.v("dimamir999", "start service from ListPhotosActivity");
        }
    }



    private class PhotoBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            int i = 0;
            if (view.getId() == R.id.photo_item_view) {
                ((ImageView) view).setImageBitmap((Bitmap) data);
                return true;
            }
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.сloseDBconnection();
    }

    public Activity getContextActivity(){
        return this;
    }
}
