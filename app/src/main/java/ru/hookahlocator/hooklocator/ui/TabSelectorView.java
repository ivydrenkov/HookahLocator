package ru.hookahlocator.hooklocator.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ru.hookahlocator.hooklocator.R;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class TabSelectorView extends FrameLayout implements View.OnClickListener {

    public final static int TAB_RATE = R.id.vtab_button_rate;
    public final static int TAB_DISTANCE = R.id.vtab_button_distance;
    public final static int TAB_FAVORITES = R.id.vtab_button_favorites;

    private final static int TAG_ID_NORMAL = R.string.id_for_tab_selector_icon_normal;
    private final static int TAG_ID_SELECTED = R.string.id_for_tab_selector_icon_selected;

    private ITabChangedListener tabSelectedListener;

    private int selectedTabId = 0;

    public TabSelectorView(Context context) {
        super(context);
        init(context);
    }

    public TabSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TabSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(getContext(), R.layout.view_tab_selector, this);
        View vRate = findViewById(TAB_RATE);
        vRate.setTag(TAG_ID_NORMAL, R.mipmap.btn_star);
        vRate.setTag(TAG_ID_SELECTED, R.mipmap.btn_star_selected);
        vRate.setOnClickListener(this);
        View vDistance = findViewById(TAB_DISTANCE);
        vDistance.setTag(TAG_ID_NORMAL, R.mipmap.btn_pin);
        vDistance.setTag(TAG_ID_SELECTED, R.mipmap.btn_pin_selected);
        vDistance.setOnClickListener(this);
        View vFavorites = findViewById(TAB_FAVORITES);
        vFavorites.setTag(TAG_ID_NORMAL, R.mipmap.btn_heart);
        vFavorites.setTag(TAG_ID_SELECTED, R.mipmap.btn_heart_selected);
        vFavorites.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        selectTab(view.getId());
    }

    public void selectTab(int tabId) {
        if (selectedTabId != tabId) {
            if (selectedTabId != 0) { // 0 on first selection
                ViewGroup oldTab = (ViewGroup) findViewById(selectedTabId);
                ImageView imageOld = (ImageView) oldTab.getChildAt(0);
                imageOld.setImageResource((Integer) oldTab.getTag(TAG_ID_NORMAL));
            }
            ViewGroup newTab = (ViewGroup) findViewById(tabId);
            ImageView imageNew = (ImageView) newTab.getChildAt(0);
            imageNew.setImageResource((Integer) newTab.getTag(TAG_ID_SELECTED));
            selectedTabId = tabId;
            if (tabSelectedListener != null) {
                tabSelectedListener.OnTabSelected(tabId);
            }
        }
    }

    public int getSelectedTabId() {
        return selectedTabId;
    }

    public void setTabSelectedListener(ITabChangedListener listener) {
        tabSelectedListener = listener;
    }

    public interface ITabChangedListener {
        void OnTabSelected(int tabId);
    }
}
