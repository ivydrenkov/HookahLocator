package ru.hookahlocator.hooklocator;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.hookahlocator.hooklocator.data.entities.BaseObject;
import ru.hookahlocator.hooklocator.data.entities.PlaceFullData;
import ru.hookahlocator.hooklocator.data.entities.Tag;

public class HookahActivity extends BaseLoadingActivity {
    final static String TAG = "HookahActivity";

    int placeId;
    PlaceFullData placeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        needToShowProgress = false;
        setContentView(R.layout.activity_hookah);
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
        mTracker.setScreenName(getString(R.string.hookah_title));
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
        Log.v(TAG, placeData.info.name + " have " + placeData.tags.size() + " tags.");
        Map<String, ArrayList<String>> rubricsMap = new HashMap<>();
        for (Tag tag: placeData.tags) {
            ArrayList<String> items = rubricsMap.get(tag.rubric);
            if (items == null) {
                items = new ArrayList<>();
                items.add(tag.name);
                rubricsMap.put(tag.rubric, items);
            } else {
                items.add(tag.name);
            }
        }
        LinearLayout textLayout = (LinearLayout) findViewById(R.id.hookah_layout);
        textLayout.removeAllViews();
        for (String rubric: rubricsMap.keySet()) {
            addRubricTextView(textLayout, rubric + ":");
            for (String name: rubricsMap.get(rubric)) {
                addItemTextView(textLayout, name);
            }
        }
    }

    private void addRubricTextView(LinearLayout linearLayout, String text) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(text);
        textView.setTextAppearance(getApplicationContext(), R.style.SmallText);
        textView.setTextColor(getResources().getColor(R.color.lightBlue));
        linearLayout.addView(textView);
    }

    private void addItemTextView(LinearLayout linearLayout, String text) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(text);
        textView.setTextAppearance(getApplicationContext(), R.style.SmallText);
        textView.setTextColor(getResources().getColor(R.color.yellow));
        textView.setPadding(getResources().getDimensionPixelSize(R.dimen.base_padding), 0, 0, 0);
        linearLayout.addView(textView);
    }

}
