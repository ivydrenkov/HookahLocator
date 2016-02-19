package ru.hookahlocator.hooklocator.dagger;

import android.app.Application;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class Injector {

    private static DataProviderComponent dataProviderComponent;
    private static ViewsComponent viewsComponent;

    public static void init(Application app) {
        viewsComponent = DaggerViewsComponent.builder()
                .appModule(new AppModule(app))
                .dataModule(new DataModule(app.getApplicationContext()))
                .build();
        dataProviderComponent = DaggerDataProviderComponent.builder()
                .netModule(new NetModule())
                .dataModule(new DataModule(app.getApplicationContext()))
                .build();
    }

    public static DataProviderComponent getDataProviderComponent() {
        return dataProviderComponent;
    }

    public static ViewsComponent getViewsComponent() {
        return viewsComponent;
    }

}
