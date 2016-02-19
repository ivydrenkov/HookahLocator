package ru.hookahlocator.hooklocator;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.hookahlocator.hooklocator.data.entities.BaseObject;
import ru.hookahlocator.hooklocator.data.entities.City;
import ru.hookahlocator.hooklocator.data.entities.Place;
import ru.hookahlocator.hooklocator.ui.adapters.MapInfoWindowAdapter;

public class MapActivity extends BaseLoadingActivity implements GoogleMap.OnMyLocationChangeListener, GoogleMap.OnCameraChangeListener, GoogleMap.OnInfoWindowClickListener, TextWatcher, View.OnClickListener {
    final static String TAG = "MapActivity";

    MapFragment mapFragment;
    GoogleMap map;
    private Map<Marker, Place> markersMap = new HashMap<Marker, Place>();
    ArrayList<Place> places;
    String selectedCityAbbr;
    City city;
    private EditText searchET;
    private ImageButton btnSearchClear;
    private String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        needToShowProgress = false;
        boolean gotData = getDataFromIntentOrState(savedInstanceState);
        if (!gotData) {
            Log.e(TAG, "No data can be found!");
            finish();
        }
        searchET = (EditText) findViewById(R.id.v_search_et);
        searchET.addTextChangedListener(this);
        searchET.setText(searchText);
        btnSearchClear = (ImageButton) findViewById(R.id.v_search_button_search_clear);
        btnSearchClear.setOnClickListener(this);
        ImageButton btnHome = (ImageButton) findViewById(R.id.v_search_button_left);
        btnHome.setOnClickListener(this);
        ImageButton btnList = (ImageButton) findViewById(R.id.v_search_button_right);
        btnList.setImageResource(R.mipmap.icon_list);
        btnList.setOnClickListener(this);
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map_map);
        setupMap();
        loadData();
        city = dataProvider.getCityByAbbr(selectedCityAbbr);
        if (city != null) {
            setCameraPosition(city.location);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(getString(R.string.map_title));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private boolean getDataFromIntentOrState(Bundle savedInstanceState) {
        if (getIntent().hasExtra(BUNDLE_CITY_ABBR)) {
            selectedCityAbbr = getIntent().getStringExtra(BUNDLE_CITY_ABBR);
            if (getIntent().hasExtra(BUNDLE_SEARCH_TEXT)) {
                searchText = getIntent().getStringExtra(BUNDLE_SEARCH_TEXT);
                return true;
            }
        }else if (savedInstanceState.containsKey(BUNDLE_CITY_ABBR)) {
            selectedCityAbbr = savedInstanceState.getString(BUNDLE_CITY_ABBR);
            if (savedInstanceState.containsKey(BUNDLE_SEARCH_TEXT)) {
                searchText = savedInstanceState.getString(BUNDLE_SEARCH_TEXT);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(BUNDLE_CITY_ABBR, selectedCityAbbr);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void loadData() {
        super.loadData();
        dataProvider.getPlacesAsync(this, selectedCityAbbr);
    }

    @Override
    public void onDataReady(ArrayList<? extends BaseObject> data) {
        super.onDataReady(data);
        places = new ArrayList<>();
        for (Place place: (ArrayList<Place>) data) {
            if ((searchText.length()==0) || (place.name.toLowerCase().contains(searchText))) {
                places.add(place);
            }
        }
        Log.v(TAG, "Loaded " + places.size() + " places");
        if (map != null) {
            updateMarkers();
        }
    }

    public void setCameraPosition(LatLng coordinates) {
        setupMap();
        if (map != null){
            Log.i(TAG, "Set User Coordinates");
            CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(coordinates, 12);
            map.animateCamera(cam);
            updateMarkers();
        }
    }

    private void updateMarkers() {
        map.clear();
        markersMap.clear();
        if (places != null) {
            for (Place place : places) {
                LatLng coordinates = place.location;
                if (coordinates != null) {
                    if (!hasMarkerAtCoordinates(coordinates)) {
                        MarkerOptions marker = new MarkerOptions().position(coordinates)
                                .title(String.valueOf(place.id))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_marker));
                        Marker m = map.addMarker(marker);
                        markersMap.put(m, place);
                    }else{
                        Log.v(TAG, "Doubled marker detected");
                    }
                }
            }
        }
        map.setOnInfoWindowClickListener(this);
        Log.v(TAG, "" + markersMap.size() + " markers on map");
    }

    private boolean hasMarkerAtCoordinates(LatLng coordinates) {
        for (Marker m: markersMap.keySet()) {
            LatLng markerCoords = m.getPosition();
            if (markerCoords.equals(coordinates)){
                return true;
            }
        }
        return false;
    }

    private void setupMap() {
        if (map != null) return;
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) == 0) {
            map = mapFragment.getMap();
        }
        if (map !=null){
            map.setOnMyLocationChangeListener(this);
            map.setOnCameraChangeListener(this);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.setMyLocationEnabled(true);
            map.setInfoWindowAdapter(new MapInfoWindowAdapter(getApplicationContext()));
        }else{
            Log.e(TAG, "Can't get Map!");
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
        //setCameraPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        map.setOnMyLocationChangeListener(null);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.v(TAG, "On camera change");
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Place place = markersMap.get(marker);
        Intent intent = new Intent(getApplicationContext(), PlaceActivity.class);
        intent.putExtra("place_id", place.id);
        intent.putExtra("city_abbr", selectedCityAbbr);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.v_search_button_left:
                Intent intentHome = new Intent(getApplicationContext(), SelectCityActivity.class);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentHome);
                break;
            case R.id.v_search_button_right:
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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        searchText = editable.toString().toLowerCase();
        loadData();
    }

}
