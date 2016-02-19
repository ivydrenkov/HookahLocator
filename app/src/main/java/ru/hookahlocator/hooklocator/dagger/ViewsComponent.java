package ru.hookahlocator.hooklocator.dagger;

import javax.inject.Singleton;

import dagger.Component;
import ru.hookahlocator.hooklocator.BaseActivity;
import ru.hookahlocator.hooklocator.ui.adapters.MapInfoWindowAdapter;
import ru.hookahlocator.hooklocator.ui.adapters.PhotosViewPagerAdapter;
import ru.hookahlocator.hooklocator.ui.adapters.PlacesRecyclerAdapter;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
@Singleton
@Component(modules={AppModule.class, DataModule.class})
public interface ViewsComponent {
    void inject(BaseActivity activity);
    void inject(PlacesRecyclerAdapter adapter);
    void inject(PhotosViewPagerAdapter adapter);
    void inject(MapInfoWindowAdapter adapter);
}
