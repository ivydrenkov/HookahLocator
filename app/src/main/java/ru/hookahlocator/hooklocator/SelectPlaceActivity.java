package ru.hookahlocator.hooklocator;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import ru.hookahlocator.hooklocator.data.entities.BaseObject;
import ru.hookahlocator.hooklocator.data.entities.Place;
import ru.hookahlocator.hooklocator.ui.ExtendedLinearLayoutManager;
import ru.hookahlocator.hooklocator.ui.TabSelectorView;
import ru.hookahlocator.hooklocator.ui.adapters.PlacesRecyclerAdapter;
import ru.hookahlocator.hooklocator.util.CollectionsUtils;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class SelectPlaceActivity extends BaseLoadingActivity implements View.OnClickListener, TabSelectorView.ITabChangedListener, TextWatcher, PlacesRecyclerAdapter.IOnItemClick {
    final static String TAG = "SelectPlace";

    ImageButton btnMap;
    ImageButton btnHome;
    ImageButton btnSearchClear;
    EditText searchET;
    TabSelectorView tabSelector;
    RecyclerView recyclerView;
    PlacesRecyclerAdapter adapter;
    int currentTabId = -1;

    ArrayList<Place> placesSource;
    ArrayList<Place> places;
    String selectedCityAbbr;
    private GoogleApiClient googleApiClient;
    LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataProvider.dropPlaces();
        setContentView(R.layout.activity_select_place);
        boolean gotData = getDataFromIntentOrState(savedInstanceState);
        if (!gotData) {
            Log.e(TAG, "No data can be found!");
            finish();
        }
        searchET = (EditText) findViewById(R.id.v_search_et);
        searchET.addTextChangedListener(this);
        btnSearchClear = (ImageButton) findViewById(R.id.v_search_button_search_clear);
        btnSearchClear.setOnClickListener(this);
        tabSelector = (TabSelectorView) findViewById(R.id.sel_place_tab_selector);
        tabSelector.setTabSelectedListener(this);
        tabSelector.selectTab(TabSelectorView.TAB_DISTANCE);
        btnMap = (ImageButton) findViewById(R.id.v_search_button_right);
        btnMap.setOnClickListener(this);
        btnHome = (ImageButton) findViewById(R.id.v_search_button_left);
        btnHome.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.sel_place_list);
        recyclerView.setLayoutManager(new ExtendedLinearLayoutManager(getApplicationContext()));
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setHasFixedSize(true);
        buildGoogleApiClient();
        Log.v(TAG, "Waiting for location data…");
        googleApiClient.connect();
        loadData();
    }

    private boolean getDataFromIntentOrState(Bundle savedInstanceState) {
        if (getIntent().hasExtra(BUNDLE_CITY_ABBR)) {
            selectedCityAbbr = getIntent().getStringExtra(BUNDLE_CITY_ABBR);
            return true;
        }else if (savedInstanceState.containsKey(BUNDLE_CITY_ABBR)) {
            selectedCityAbbr = savedInstanceState.getString(BUNDLE_CITY_ABBR);
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(BUNDLE_CITY_ABBR, selectedCityAbbr);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (placesSource != null) { //data already loaded
            OnTabSelected(tabSelector.getSelectedTabId());
        }
        mTracker.setScreenName(getString(R.string.sel_place_title));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.v(TAG, "Connected to location client");
                        Location lastLocation = LocationServices.FusedLocationApi
                                .getLastLocation(googleApiClient);
                        if (lastLocation != null) {
                            Log.v(TAG, "Get location: " + lastLocation.getLatitude() + ", " + lastLocation.getLongitude());
                            userLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            if (adapter != null) {
                                adapter.updateLocation(userLocation);
                            }
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.e(TAG, "Connected to location client suspended!");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.e(TAG, "No location. Google API connection failed: " + connectionResult.toString());
                    }
                })
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void loadData() {
        super.loadData();
        dataProvider.getPlacesAsync(this, selectedCityAbbr);
    }

    @Override
    public void onDataReady(ArrayList<? extends BaseObject> data) {
        super.onDataReady(data);
        placesSource = (ArrayList<Place>) data;
        Log.v(TAG, "Loaded " + placesSource.size() + " places");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OnTabSelected(tabSelector.getSelectedTabId());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.v_search_button_right: //MAP
                btnMap.startAnimation(AnimationUtils
                        .loadAnimation(getApplicationContext(), R.anim.button_click));
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra(BUNDLE_CITY_ABBR, selectedCityAbbr);
                intent.putExtra(BUNDLE_SEARCH_TEXT, searchET.getText().toString());
                startActivity(intent);
                break;
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

    @Override
    public void OnTabSelected(int tabId) {
        if ((placesSource != null) && (currentTabId != tabId)) {
            currentTabId = tabId;
            switch (tabId) {
                case TabSelectorView.TAB_DISTANCE:
                    Log.v(TAG, "Sort by distance");
                    if (userLocation == null) {
                        userLocation = dataProvider.getCityByAbbr(selectedCityAbbr).location;
                    }
                    places = CollectionsUtils.getPlacesSortedByDistanceFromLocationAsc(placesSource, userLocation);
                    break;
                case TabSelectorView.TAB_RATE:
                    Log.v(TAG, "Sort by rating");
                    places = CollectionsUtils.getPlacesSortedByRatingDesc(placesSource);
                    break;
                case TabSelectorView.TAB_FAVORITES:
                    Log.v(TAG, "Show favorites");
                    places = dataProvider.getFavoritePlacesForAllCities();
                    if (places == null) { // no favorites
                        places = new ArrayList<>(0);
                    }
                    break;
                default:
                    Log.e(TAG, "Unknown tab id!!!");
            }
            adapter = new PlacesRecyclerAdapter(places);
            adapter.setOnItemClickListener(this);
            adapter.setFilter(searchET.getEditableText().toString());
            if (userLocation != null) {
                adapter.updateLocation(userLocation);
            }
            recyclerView.setAdapter(adapter);
        }
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
            String filter = editable.toString().toLowerCase();
            adapter.setFilter(filter);
        }
    }

    @Override
    public void onItemClick(int position) {
        Place place = adapter.getItem(position);
        Intent intent = new Intent(getApplicationContext(), PlaceActivity.class);
        intent.putExtra(BUNDLE_PLACE_ID, place.id);
        intent.putExtra(BUNDLE_CITY_ABBR, selectedCityAbbr);
        startActivity(intent);
    }
}
