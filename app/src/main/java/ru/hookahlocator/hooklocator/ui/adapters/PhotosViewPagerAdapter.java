package ru.hookahlocator.hooklocator.ui.adapters;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import javax.inject.Inject;

import ru.hookahlocator.hooklocator.dagger.Injector;
import ru.hookahlocator.hooklocator.net.API;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Декабрь 2015
 */
public class PhotosViewPagerAdapter extends PagerAdapter {
    final static String TAG = "PhotosViewPagerAdapter";

    @Inject ImageLoader imageLoader;
    ArrayList<String> photos;

    public PhotosViewPagerAdapter (ArrayList<String> list) {
        Injector.getViewsComponent().inject(this);
        photos = list;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position){
        Log.v(TAG, "Instantiate item: " + position);
        ImageView imageView = new ImageView(collection.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        String photo = photos.get(position);
        imageLoader.displayImage(API.URL + photo, imageView);
        ((ViewPager) collection).addView(imageView, layoutParams);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        Log.v(TAG, "Destroy item: " + position);
        ((ViewPager) collection).removeView((View) view);
    }
}
