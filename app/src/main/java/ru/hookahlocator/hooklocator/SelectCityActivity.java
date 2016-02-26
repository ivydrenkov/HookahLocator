package ru.hookahlocator.hooklocator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;

import ru.hookahlocator.hooklocator.data.entities.BaseObject;
import ru.hookahlocator.hooklocator.data.entities.City;
import ru.hookahlocator.hooklocator.ui.adapters.CitiesAdapter;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class SelectCityActivity extends BaseLoadingActivity implements TextWatcher, View.OnClickListener {
    final static String TAG = "SelectCityActivity";

    ImageButton btnHome;
    ImageButton btnSearchClear;
    EditText searchET;
    ListView listView;
    CitiesAdapter adapter;
    ArrayList<City> cities;
    String filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataProvider.dropCities();
        setContentView(R.layout.activity_select_city);
        searchET = (EditText) findViewById(R.id.v_search_et);
        searchET.addTextChangedListener(this);
        btnSearchClear = (ImageButton) findViewById(R.id.v_search_button_search_clear);
        btnSearchClear.setOnClickListener(this);
        findViewById(R.id.v_search_button_right).setVisibility(View.INVISIBLE);
        btnHome = (ImageButton) findViewById(R.id.v_search_button_left);
        btnHome.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.sel_city_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                City selectedCity = (City) listView.getAdapter().getItem(pos);
                gotoPlaces(selectedCity.abbr);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setNestedScrollingEnabled(listView, true);
        }
        String preSelectedCity = getIntent().getStringExtra(BUNDLE_CITY_ABBR);
        if (preSelectedCity != null) {
            gotoPlaces(preSelectedCity);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cities == null) {
            loadData();
        }
        mTracker.setScreenName(getString(R.string.sel_city_title));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void gotoPlaces(String selectedCityAbbr) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BUNDLE_CITY_ABBR, selectedCityAbbr);
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), SelectPlaceActivity.class);
        intent.putExtra(BUNDLE_CITY_ABBR, selectedCityAbbr);
        startActivity(intent);
    }

    @Override
    protected void loadData() {
        super.loadData();
        dataProvider.getCitiesAsync(this);
    }

    @Override
    public void onDataReady(ArrayList<? extends BaseObject> data) {
        super.onDataReady(data);
        cities = (ArrayList<City>) data;
        Log.v(TAG, "Loaded " + cities.size() + " cities");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateFilter();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (adapter != null) {
            filter = editable.toString().toLowerCase();
            updateFilter();
        }
    }

    private void updateFilter() {
        ArrayList<City> filtered = new ArrayList<>();
        for (City city: cities) {
            if ((filter == null) || (filter.length() == 0) || (city.name.toLowerCase().contains(filter))) {
                filtered.add(city);
            }
        }
        adapter = new CitiesAdapter(filtered);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.v_search_button_left: //BACK
                btnHome.startAnimation(AnimationUtils
                        .loadAnimation(getApplicationContext(), R.anim.button_click));
                finish();
                break;
            case R.id.v_search_button_search_clear:
                btnSearchClear.startAnimation(AnimationUtils
                        .loadAnimation(getApplicationContext(), R.anim.button_click));
                searchET.setText("");
                break;
            default:
                break;
        }
    }
}
