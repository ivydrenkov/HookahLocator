package ru.hookahlocator.hooklocator;

import android.app.Application;

import ru.hookahlocator.hooklocator.dagger.Injector;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class HookahLocatorApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Injector.init(this);
    }

}
