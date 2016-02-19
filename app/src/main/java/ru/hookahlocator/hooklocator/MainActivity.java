package ru.hookahlocator.hooklocator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.google.android.gms.analytics.HitBuilders;

public class MainActivity extends BaseActivity {
    final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageButton btnSelectCity = (ImageButton) findViewById(R.id.main_btn_select_city);
        btnSelectCity.setAlpha(1.0f);
        btnSelectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSelectCity.startAnimation(AnimationUtils
                        .loadAnimation(getApplicationContext(), R.anim.button_click));
                Intent intent = new Intent(getApplicationContext(), SelectCityActivity.class);
                //Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                //intent.putExtra(BUNDLE_PLACE_ID, 344);
                startActivity(intent);
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String selectedCityAbbr = sharedPreferences.getString(BUNDLE_CITY_ABBR, NO_CITY_SELECTED);
        if (!selectedCityAbbr.equals(NO_CITY_SELECTED)) {
            Log.v(TAG, "Selected city: " + selectedCityAbbr);
            Intent intent = new Intent(getApplicationContext(), SelectCityActivity.class);
            intent.putExtra(BUNDLE_CITY_ABBR, selectedCityAbbr);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(getString(R.string.main_screen_title));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
