package ru.hookahlocator.hooklocator.dagger;

import javax.inject.Singleton;

import dagger.Component;
import ru.hookahlocator.hooklocator.data.DataProvider;
import ru.hookahlocator.hooklocator.data.entities.PlaceFullData;
import ru.hookahlocator.hooklocator.ui.adapters.MapInfoWindowAdapter;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
@Singleton
@Component(modules={DataModule.class, NetModule.class})
public interface DataProviderComponent {
    void inject(DataProvider dataProvider);
    void inject(PlaceFullData placeFullData);
    void inject(MapInfoWindowAdapter adapter);
}
