package ru.hookahlocator.hooklocator;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

import ru.hookahlocator.hooklocator.data.entities.BaseObject;
import ru.hookahlocator.hooklocator.data.entities.Place;
import ru.hookahlocator.hooklocator.data.entities.PlaceFullData;
import ru.hookahlocator.hooklocator.net.API;
import ru.hookahlocator.hooklocator.util.DateUtils;
import ru.hookahlocator.hooklocator.util.LocationUtils;
import ru.hookahlocator.hooklocator.util.NavigatorUtils;

public class PlaceActivity extends BaseLoadingActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    final static String TAG = "PlaceActivity";

    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;

    final static int POPUP_YANDEX_MAP = 1;
    final static int POPUP_YANDEX_NAVIGATOR = 2;

    int placeId;
    String cityAbbr;
    Place place;
    PlaceFullData placeData;
    ImageButton btnTrack;
    ImageButton btnGallery;
    Button btnCall;
    Button btnFavorite;
    TextView tvDistance;
    boolean favorite;

    private GoogleApiClient googleApiClient;
    LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        boolean gotData = getDataFromIntentOrState(savedInstanceState);
        if (!gotData) {
            Log.e(TAG, "No data can be found!");
            finish();
        }
        int statusBarHeight = getStatusBarHeight();
        Log.v(TAG, "Status bar height: " + statusBarHeight);
        View header = findViewById(R.id.place_header_layout);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) header.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + statusBarHeight,
                layoutParams.rightMargin, layoutParams.bottomMargin);
        header.setLayoutParams(layoutParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        place = dataProvider.getPlaceById(placeId);
        favorite = dataProvider.isPlaceFavorite(placeId);
        Button btnHookah = (Button) findViewById(R.id.place_button_hookah);
        btnHookah.setOnClickListener(this);
        Button btnComments = (Button) findViewById(R.id.place_button_comments);
        btnComments.setOnClickListener(this);
        ImageButton btnBack = (ImageButton) findViewById(R.id.place_button_home);
        btnBack.setOnClickListener(this);
        btnTrack = (ImageButton) findViewById(R.id.place_button_track);
        btnTrack.setOnClickListener(this);
        btnGallery = (ImageButton) findViewById(R.id.place_button_gallery);
        btnGallery.setOnClickListener(this);
        btnCall = (Button) findViewById(R.id.place_button_call);
        btnCall.setOnClickListener(this);
        btnFavorite = (Button) findViewById(R.id.place_button_favorite);
        btnFavorite.setOnClickListener(this);
        updateFavoriteButtonState();
        tvDistance = (TextView) findViewById(R.id.place_place_distance);

        buildGoogleApiClient();
        googleApiClient.connect();

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(getString(R.string.place_title));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private boolean getDataFromIntentOrState(Bundle savedInstanceState) {
        if (getIntent().hasExtra(BUNDLE_CITY_ABBR)) {
            cityAbbr = getIntent().getStringExtra(BUNDLE_CITY_ABBR);
            if (getIntent().hasExtra(BUNDLE_PLACE_ID)) {
                placeId = getIntent().getIntExtra(BUNDLE_PLACE_ID, 0);
                return true;
            }
        }else if (savedInstanceState.containsKey(BUNDLE_CITY_ABBR)) {
            cityAbbr = savedInstanceState.getString(BUNDLE_CITY_ABBR);
            if (savedInstanceState.containsKey(BUNDLE_PLACE_ID)) {
                placeId = savedInstanceState.getInt(BUNDLE_PLACE_ID, 0);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(BUNDLE_PLACE_ID, placeId);
        outState.putString(BUNDLE_CITY_ABBR, cityAbbr);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void updateFavoriteButtonState() {
        if (favorite) {
            btnFavorite.setBackgroundResource(R.drawable.but_favorite_bg_favorited);
            btnFavorite.setText(getString(R.string.place_btn_unfavorite));
        }else {
            btnFavorite.setBackgroundResource(R.drawable.but_favorite_bg);
            btnFavorite.setText(getString(R.string.place_btn_favorite));
        }
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
        if (userLocation != null) {
            updateDistance();
        }
        TextView tvName = (TextView) findViewById(R.id.place_name);
        tvName.setText(placeData.info.name);
        TextView tvPlaceName = (TextView) findViewById(R.id.place_place_name);
        tvPlaceName.setText(placeData.info.name);
        TextView tvAddress = (TextView) findViewById(R.id.place_place_address);
        tvAddress.setText(placeData.info.address);
        TextView tvOpened = (TextView) findViewById(R.id.place_place_opened);
        tvOpened.setText(R.string.place_opened_today);
        TextView tvWorkTime = (TextView) findViewById(R.id.place_place_opened_time);
        if (DateUtils.isWeekend(Calendar.getInstance())) {
            tvWorkTime.setText(placeData.info.timeWeekendWorking);
        } else {
            tvWorkTime.setText(placeData.info.timeWorking);
        }
        TextView tvCostHookah = (TextView) findViewById(R.id.place_cost_hookah);
        tvCostHookah.setText(placeData.info.costHookah + " " + place.currency);
        TextView tvCostTea = (TextView) findViewById(R.id.place_cost_tea);
        tvCostTea.setText(placeData.info.costTea + " " + place.currency);
        TextView tvRateHook = (TextView) findViewById(R.id.place_rate_hookah);
        tvRateHook.setText(placeData.info.rateHookah);
        TextView tvRateAtmosphere = (TextView) findViewById(R.id.place_rate_atmosphere);
        tvRateAtmosphere.setText(placeData.info.rateAtmosphere);
        TextView tvRateService = (TextView) findViewById(R.id.place_rate_service);
        tvRateService.setText(placeData.info.rateService);
        TextView tvRateAll = (TextView) findViewById(R.id.place_rate_all);
        tvRateAll.setText(placeData.info.rate);
        TextView tvFood = (TextView) findViewById(R.id.place_food);
        if (placeData.info.food.length() > 1) { //May be «0», that means «no»
            tvFood.setText("да");
        } else {
            tvFood.setText("нет");
        }
        TextView tvAlcohol = (TextView) findViewById(R.id.place_alcohol);
        if (placeData.info.drinks.length() > 1) {
            tvAlcohol.setText("да");
        } else {
            tvAlcohol.setText("нет");
        }
        if ((placeData.comments != null)&&(placeData.comments.size()>0)) {
            TextView tvCommentNick = (TextView) findViewById(R.id.it_comment_name);
            tvCommentNick.setText(placeData.comments.get(0).nickname);
            TextView tvCommentDate = (TextView) findViewById(R.id.it_comment_date);
            tvCommentDate.setText(placeData.comments.get(0).date);
            TextView tvCommentText = (TextView) findViewById(R.id.it_comment_text);
            tvCommentText.setText(placeData.comments.get(0).text);
        } else {
            findViewById(R.id.place_comments_layout).setVisibility(View.GONE);
        }

        ImageView ivMain = (ImageView) findViewById(R.id.place_image);
        ivMain.setOnClickListener(this);
        imageLoader.displayImage(API.URL + placeData.info.mainImage, ivMain);
    }

    private void updateDistance() {
        float distanceKm = LocationUtils.getDistanceBetweenInMeters(userLocation, place.location)/1000;
        tvDistance.setText(String.format("%.1f км", distanceKm));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //case R.id.place_image:
            case R.id.place_button_gallery:
                if ((placeData.info.photos != null) && (placeData.info.photos.size() > 0)) {
                    Intent intentPhoto = new Intent(getApplicationContext(), PhotoActivity.class);
                    intentPhoto.putExtra(BUNDLE_PLACE_ID, place.id);
                    startActivity(intentPhoto);
                } else {
                    //Toast.makeText(getApplicationContext(), R.string.place_no_photos, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.place_button_hookah:
                Intent intentHookah = new Intent(getApplicationContext(), HookahActivity.class);
                intentHookah.putExtra(BUNDLE_PLACE_ID, place.id);
                startActivity(intentHookah);
                break;
            case R.id.place_button_comments:
                Intent intentComments = new Intent(getApplicationContext(), CommentsActivity.class);
                intentComments.putExtra(BUNDLE_PLACE_ID, place.id);
                startActivity(intentComments);
                break;
            case R.id.place_button_track:
                showPopup(btnTrack);
                break;
            case R.id.place_button_home:
                Intent intentHome = new Intent(getApplicationContext(), SelectCityActivity.class);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentHome);
                break;
            case R.id.place_button_call:
                if (placeData != null) {
                    String phone = placeData.info.phone.replaceAll("[^0-9|^+]", "");
                    String url = "tel:" + phone;
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                    startActivity(intent);
                }
                break;
            case R.id.place_button_favorite:
                favorite = !favorite;
                if (favorite) {
                    dataProvider.favoritePlace(place);
                }else {
                    dataProvider.unfavoritePlace(place);
                }
                updateFavoriteButtonState();
                break;
            default:
                Log.e(TAG, "Unhandled click!");
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case POPUP_YANDEX_MAP:
                NavigatorUtils.navigateByYandexMaps(this, userLocation, place.location);
                break;
            case POPUP_YANDEX_NAVIGATOR:
                NavigatorUtils.navigateByYandexNavigator(this, place.location);
                break;
            default:
                return false;
        }
        return false;
    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        StringBuilder yandexMap = new StringBuilder(getString(R.string.place_popup_yandex_map));
        if (!NavigatorUtils.isYandexMapsInstalled(this)) {
            yandexMap.append(" ")
                    .append(getString(R.string.place_popup_not_installed));
        }
        popup.getMenu().add(0, POPUP_YANDEX_MAP, 0, yandexMap);
        StringBuilder yandexNavigator = new StringBuilder(getString(R.string.place_popup_yandex_nav));
        if (!NavigatorUtils.isYandexNavigatorInstalled(this)) {
            yandexNavigator.append(" ")
                    .append(getString(R.string.place_popup_not_installed));
        }
        popup.getMenu().add(0, POPUP_YANDEX_NAVIGATOR, 1, yandexNavigator);
        popup.setOnMenuItemClickListener(this);
        popup.show();
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
                            if (place != null) {
                                updateDistance();
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

}
