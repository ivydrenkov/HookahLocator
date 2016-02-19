package ru.hookahlocator.hooklocator.dagger;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.hookahlocator.hooklocator.data.DataBase;
import ru.hookahlocator.hooklocator.data.DataProvider;
import ru.hookahlocator.hooklocator.data.JSONParser;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
@Module
public class DataModule {
    Context context;

    public DataModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    DataProvider provideDataProvider() {
        return new DataProvider(context);
    }

    @Provides
    @Singleton
    DataBase provideDataBase() {
        return new DataBase(context);
    }

    @Provides
    @Singleton
    ImageLoader provideImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(new ColorDrawable(Color.TRANSPARENT))
                .showImageOnFail(new ColorDrawable(Color.TRANSPARENT))
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config);
        return ImageLoader.getInstance();
    }

    @Provides
    @Singleton
    JSONParser provideParser() {
        return new JSONParser();
    }

}
