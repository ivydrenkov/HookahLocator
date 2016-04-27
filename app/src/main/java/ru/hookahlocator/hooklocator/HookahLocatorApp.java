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

        YandexMetrica.activate(getApplicationContext(), "99cdb08c-6000-4b98-8a61-578384a8e117");
        // Отслеживание активности пользователей
        YandexMetrica.enableActivityAutoTracking(this);

        Injector.init(this);
    }

}
