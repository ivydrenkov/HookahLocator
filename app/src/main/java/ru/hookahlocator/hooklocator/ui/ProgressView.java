package ru.hookahlocator.hooklocator.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import ru.hookahlocator.hooklocator.R;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class ProgressView extends FrameLayout {

    Animation showAnimation;
    Animation hideAnimation;
    private boolean hideAlreadyRequested = false;

    public ProgressView(Context context) {
        super(context);
        init(context);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(getContext(), R.layout.view_progress, this);
        showAnimation = AnimationUtils.loadAnimation(context, R.anim.progress_show);
        hideAnimation = AnimationUtils.loadAnimation(context, R.anim.progress_hide);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
                hideAlreadyRequested = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (hideAlreadyRequested) {
                    startAnimation(hideAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void show() {
        startAnimation(showAnimation);
        setVisibility(VISIBLE);
    }

    public void hide() {
        if (showAnimation.hasEnded()) {
            startAnimation(hideAnimation);
        } else {
            hideAlreadyRequested = true;
        }
    }
}
