package ru.hookahlocator.hooklocator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;

import javax.inject.Inject;

import ru.hookahlocator.hooklocator.dagger.Injector;
import ru.hookahlocator.hooklocator.data.DataProvider;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class BaseActivity extends AppCompatActivity {

    @Inject DataProvider dataProvider;
    @Inject ImageLoader imageLoader;
    @Inject Tracker mTracker;

    static final String BUNDLE_CITY_ABBR = "city_abbr";
    static final String BUNDLE_PLACE_ID = "place_id";
    static final String BUNDLE_SEARCH_TEXT = "search_text";

    static final String SHARED_PREFERENCES_NAME = "HookahLocatorPreferences";
    final static String NO_CITY_SELECTED = "NO_CITY_SELECTED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getViewsComponent().inject(this);
    }

    protected int getStatusBarHeight() {
        int result = 0;
        if (isTranslucentStatusBar()) {
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    protected boolean isTranslucentStatusBar()
    {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int flags = lp.flags;
        // Here I'm comparing the binary value of Translucent Status Bar with flags in the window
        if ((flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
            return true;
        }

        return false;
    }
}
