package ru.hookahlocator.hooklocator.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class NavigatorUtils {
    final static String TAG = "NavigatorUtils";

    /**
     * based on https://github.com/yandexmobile/yandexmapkit-android/wiki/%D0%98%D0%BD%D1%82%D0%B5%D0%B3%D1%80%D0%B0%D1%86%D0%B8%D1%8F-%D1%81-%D0%AF%D0%BD%D0%B4%D0%B5%D0%BA%D1%81.%D0%9D%D0%B0%D0%B2%D0%B8%D0%B3%D0%B0%D1%82%D0%BE%D1%80%D0%BE%D0%BC
     * @param activity
     */
    public final static void navigateByYandexNavigator(Activity activity, LatLng destination) {
        Intent intent;
        // Проверяем, установлен ли Яндекс.Навигатор
        if (isYandexNavigatorInstalled(activity)) {
            // Создаем интент для построения маршрута
            intent = new Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP");
            intent.setPackage("ru.yandex.yandexnavi");
            //intent.putExtra("lat_from", 55.751802);
            //intent.putExtra("lon_from", 37.586684);
            intent.putExtra("lat_to", (float)destination.latitude);
            intent.putExtra("lon_to", (float)destination.longitude);
        } else {
            // Если нет - будем открывать страничку Навигатора в Google Play
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=ru.yandex.yandexnavi"));
        }
        // Запускаем нужную Activity
        activity.startActivity(intent);
    }

    /**
     * based on https://github.com/yandexmobile/yandexmapkit-android/wiki/%D0%98%D0%BD%D1%82%D0%B5%D0%B3%D1%80%D0%B0%D1%86%D0%B8%D1%8F-%D1%81-%D0%9C%D0%BE%D0%B1%D0%B8%D0%BB%D1%8C%D0%BD%D1%8B%D0%BC%D0%B8-%D0%AF%D0%BD%D0%B4%D0%B5%D0%BA%D1%81.%D0%9A%D0%B0%D1%80%D1%82%D0%B0%D0%BC%D0%B8
     * @param activity
     */
    public final static void navigateByYandexMaps(Activity activity, LatLng source, LatLng destination) {
        Intent intent;
        // Проверяем, установлены ли Яндекс.Карты
        if (isYandexMapsInstalled(activity)) {
            // Создаем интент для построения маршрута
            intent = new Intent("ru.yandex.yandexmaps.action.BUILD_ROUTE_ON_MAP");
            intent.setPackage("ru.yandex.yandexmaps");
            if (source != null) {
                intent.putExtra("lat_from", (float)source.latitude);
                intent.putExtra("lon_from", (float)source.longitude);
            }
            intent.putExtra("lat_to", (float)destination.latitude);
            intent.putExtra("lon_to", (float)destination.longitude);
        } else {
            // Если нет - будем открывать страничку МЯК в Google Play
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=ru.yandex.yandexmaps"));
        }
        // Запускаем нужную Activity
        activity.startActivity(intent);
    }

    public final static boolean isYandexMapsInstalled(Activity activity) {
        Intent intent = new Intent("ru.yandex.yandexmaps.action.BUILD_ROUTE_ON_MAP");
        intent.setPackage("ru.yandex.yandexmaps");
        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        if (infos == null || infos.size() == 0) {
            return false;
        }
        return true;
    }

    public final static boolean isYandexNavigatorInstalled(Activity activity) {
        Intent intent = new Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP");
        intent.setPackage("ru.yandex.yandexnavi");
        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        if (infos == null || infos.size() == 0) {
            return false;
        }
        return true;
    }

}
