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
import org.dimamir999.testapp.services.LocationControlService;
import org.dimamir999.testapp.services.PhotoScaler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListPhotosActivity extends Activity implements IListPhotoView {

    private static final int DELETE_ITEM_ID = 1;
    private static final String ATTRIBUTE_NAME_TEXT = "text";
    private static final String ATTRIBUTE_NAME_IMAGE = "image";
    private static final int LIST_PHOTO_HEIGHT = 250;
    private static final int LIST_PHOTO_WIDTH = 250;
    public static final int DISTANCE_RESPONSE = 0;
    private static final int PICK_PHOTO_REQUEST_CODE = 1;

    private ListPhotosPresenter presenter;
    private ArrayList<Map<String, Object>> data;
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

        data = new ArrayList<Map<String, Object>>();
        ArrayList<PhotoWithGeoTag> photosList = presenter.getListData();
        for (int i = 0; i < photosList.size(); i++) {
            Map<String, Object> itemMap = new HashMap<String, Object>();
            PhotoWithGeoTag photoObject = photosList.get(i);
            itemMap.put(ATTRIBUTE_NAME_TEXT, photoObject.getDate().toString());
            Bitmap photo = photoObject.getPhoto();
            Log.v("dimamir999", "photo loaded");
            if(photo.getHeight() != LIST_PHOTO_HEIGHT || photo.getWidth() != LIST_PHOTO_WIDTH) {
                Log.v("dimamir999", "photo scaled");
                photo = photoScaler.scaleForList(photo, LIST_PHOTO_HEIGHT, LIST_PHOTO_WIDTH);
            }
            itemMap.put(ATTRIBUTE_NAME_IMAGE, photo);
            data.add(itemMap);
        }
        String[] from = { ATTRIBUTE_NAME_TEXT,
                ATTRIBUTE_NAME_IMAGE };
        int[] to = { R.id.date_text_view,  R.id.photo_item_view};
        adapter = new SimpleAdapter(this, data, R.layout.photo_list_item,
                from, to);
        adapter.setViewBinder(new ScalePhotoBinder());
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
    protected void onResume() {
        setServiceButtonText();
        data.clear();
        ArrayList<PhotoWithGeoTag> photosList = presenter.getListData();
        for (int i = 0; i < photosList.size(); i++) {
            Map<String, Object> itemMap = new HashMap<String, Object>();
            PhotoWithGeoTag photoObject = photosList.get(i);
            itemMap.put(ATTRIBUTE_NAME_TEXT, photoObject.getDate().toString());
            Bitmap photo = photoObject.getPhoto();
            Log.v("dimamir999", "photo loaded");
            if(photo.getHeight() != LIST_PHOTO_HEIGHT || photo.getWidth() != LIST_PHOTO_WIDTH) {
                Log.v("dimamir999", "photo scaled");
                photo = photoScaler.scaleForList(photo, LIST_PHOTO_HEIGHT, LIST_PHOTO_WIDTH);
            }
            itemMap.put(ATTRIBUTE_NAME_IMAGE, photo);
            data.add(itemMap);
        }
        adapter.notifyDataSetChanged();
        super.onResume();
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
        if(requestCode == PICK_PHOTO_REQUEST_CODE){
            PhotoWithGeoTag photoWithGeoTag = data.getParcelableExtra(PickPhotoFragment.PHOTO_OBJECT_CODE);
            Log.v("dimamir999", "photo arrived");
        }
    }

    public Activity getContextActivity(){
        return this;
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
            data.remove(menuInfo.position);
            presenter.deletePhoto(menuInfo.position);
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
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

    private class ScalePhotoBinder implements SimpleAdapter.ViewBinder {

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
}
