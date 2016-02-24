package ru.hookahlocator.hooklocator;

import android.app.Application;

import com.yandex.metrica.YandexMetrica;

import ru.hookahlocator.hooklocator.dagger.Injector;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class HookahLocatorApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        YandexMetrica.activate(getApplicationContext(), "11d5368b-39cc-46ee-98e2-1ef05b2c9a49");
        // Отслеживание активности пользователей
        YandexMetrica.enableActivityAutoTracking(this);

        Injector.init(this);
    }

}
