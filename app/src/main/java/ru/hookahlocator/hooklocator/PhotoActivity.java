package ru.hookahlocator.hooklocator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;

import ru.hookahlocator.hooklocator.data.entities.BaseObject;
import ru.hookahlocator.hooklocator.data.entities.PlaceFullData;
import ru.hookahlocator.hooklocator.ui.adapters.PhotosViewPagerAdapter;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Декабрь 2015
 */
public class PhotoActivity extends BaseLoadingActivity {
    final static String TAG = "PhotoActivity";

    int placeId;
    PlaceFullData placeFullData;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        boolean gotData = getDataFromIntentOrState(savedInstanceState);
        if (!gotData) {
            Log.e(TAG, "No data can be found!");
            finish();
        }
        viewPager = (ViewPager) findViewById(R.id.photo_pager);
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(getString(R.string.gallery_title));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private boolean getDataFromIntentOrState(Bundle savedInstanceState) {
        if (getIntent().hasExtra(BUNDLE_PLACE_ID)) {
            placeId = getIntent().getIntExtra(BUNDLE_PLACE_ID, 0);
            return true;
        }else if (savedInstanceState.containsKey(BUNDLE_PLACE_ID)) {
            placeId = savedInstanceState.getInt(BUNDLE_PLACE_ID, 0);
            return true;
        }
        return false;
    }

    @Override
    protected void loadData() {
        super.loadData();
        dataProvider.getPlaceFullInfoAsync(this, placeId);
    }

    @Override
    public void onDataReady(ArrayList<? extends BaseObject> data) {
        super.onDataReady(data);
        placeFullData = (PlaceFullData) data.get(0);
        if (placeFullData != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fillData();
                }
            });
        } else {
            Log.e(TAG, "We got no place data!!!");
        }
    }

    private void fillData() {
        TextView tvTitle = (TextView) findViewById(R.id.photo_title);
        tvTitle.setPadding(0, getStatusBarHeight(), 0, 0);
        tvTitle.setText(placeFullData.info.name);
        if (placeFullData.info.photos != null) {
            viewPager.setAdapter(new PhotosViewPagerAdapter(placeFullData.info.photos));
        }else {
            Log.e(TAG, "No photos!");
        }
    }

}
