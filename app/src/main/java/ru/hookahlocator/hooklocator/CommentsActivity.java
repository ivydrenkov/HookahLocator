package ru.hookahlocator.hooklocator;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;

import ru.hookahlocator.hooklocator.data.entities.BaseObject;
import ru.hookahlocator.hooklocator.data.entities.PlaceFullData;
import ru.hookahlocator.hooklocator.ui.adapters.CommentsAdapter;

public class CommentsActivity extends BaseLoadingActivity {
    final static String TAG = "CommentsActivity";

    int placeId;
    PlaceFullData placeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        needToShowProgress = false;
        setContentView(R.layout.activity_comments);
        boolean gotData = getDataFromIntentOrState(savedInstanceState);
        if (!gotData) {
            Log.e(TAG, "No data can be found!");
            finish();
        }
        dataProvider.getPlaceFullInfoAsync(this, placeId);
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(getString(R.string.comments_title));
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
        placeData = (PlaceFullData) data.get(0);
        if (placeData != null) {
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
        if (placeData.comments != null) {
            Log.v(TAG, placeData.info.name + " have " + placeData.comments.size() + " comments.");
            ListView listView = (ListView) findViewById(R.id.comments_list);
            CommentsAdapter adapter = new CommentsAdapter(placeData.comments);
            listView.setAdapter(adapter);
        }
    }

}
