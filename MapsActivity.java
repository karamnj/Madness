package com.sirius.madness.ui.activities;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.sirius.madness.R;
import com.sirius.madness.beans.EventBean;
import com.sirius.madness.provider.IgniteContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends BaseActivity {//implements OnMapReadyCallback {

    private static final String CLASS_NAME = MapsActivity.class.getSimpleName();
    private Double latitude,longitude;

    private final String[] mProjection = {
            IgniteContract.Events.EVENT_LATITUDE,
            IgniteContract.Events.EVENT_LONGITUDE,
            IgniteContract.ImageBinary.IMAGE_DATA,
            IgniteContract.ImageMetaData.IMAGE_HEIGHT,
            IgniteContract.ImageMetaData.IMAGE_WIDTH,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        isFirstLevelActivity = true;

        final ImageView map = (ImageView) findViewById(R.id.mapImage);

        Cursor cursor = getContentResolver().query(IgniteContract.Events.CONTENT_URI, mProjection, null, null, null);
        cursor.moveToFirst();
        final EventBean event = new EventBean();
        event.setImage(cursor.getBlob(cursor.getColumnIndex(IgniteContract.ImageBinary.IMAGE_DATA)));
        event.setImageHeight(cursor.getInt(cursor.getColumnIndex(IgniteContract.ImageMetaData.IMAGE_HEIGHT)));
        event.setImageWidth(cursor.getInt(cursor.getColumnIndex(IgniteContract.ImageMetaData.IMAGE_WIDTH)));

        /*ViewTreeObserver vto = map.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                map.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalWidth = map.getMeasuredWidth();
                map.getLayoutParams().height = (event.getImageHeight()*finalWidth/event.getImageWidth());
                return true;
            }
        });*/
        Bitmap bMap = BitmapFactory.decodeByteArray(event.getImage(), 0, event.getImage().length);
        Drawable image = new BitmapDrawable(getResources(),bMap);
        map.setImageDrawable(image);

        /*Cursor cursor = getContentResolver().query(IgniteContract.Events.CONTENT_URI, mProjection, null, null, null);
        cursor.moveToFirst();
        latitude = Double.parseDouble(cursor.getString(1));
        longitude = Double.parseDouble(cursor.getString(0));

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
    }

    @Override
    protected int getSelfMenuBarItem() {
        //return the menu bar item that this activity represents
        return MENU_BAR_ITEM_MAPS;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    /*@Override
    public void onMapReady(GoogleMap map) {
        Log.d("Maps:","onMapReady");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitude,longitude), 17));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude,longitude))
                .title("Venue"));
    }*/
}
