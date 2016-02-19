package ru.hookahlocator.hooklocator.ui;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import ru.hookahlocator.hooklocator.R;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class GalleryButtonBehavior extends CoordinatorLayout.Behavior<ImageButton> {
    public GalleryButtonBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageButton child, View dependency) {
        return dependency instanceof AppBarLayout;
    }
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageButton child, View dependency) {

        float translationX = dependency.getBottom()/2 + parent.getResources().getDimensionPixelSize(R.dimen.base_padding)
                - parent.getResources().getDimensionPixelSize(R.dimen.place_header_size)/2;
        child.setTranslationX(translationX);

        float translationY = - (child.getHeight()/2 + parent.getResources().getDimensionPixelSize(R.dimen.base_padding));
        child.setTranslationY(translationY);
        return true;
    }
}
