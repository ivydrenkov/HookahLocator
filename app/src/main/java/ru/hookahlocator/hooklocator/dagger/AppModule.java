package ru.hookahlocator.hooklocator.dagger;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.hookahlocator.hooklocator.R;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */

@Module
public class AppModule {
    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    Tracker provideGoogleAnalytics() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(mApplication);
        analytics.setLocalDispatchPeriod(30);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        Tracker mTracker = analytics.newTracker(R.xml.global_tracker);
        return mTracker;
    }
}
