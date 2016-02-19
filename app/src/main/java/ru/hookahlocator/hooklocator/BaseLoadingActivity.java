package ru.hookahlocator.hooklocator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import ru.hookahlocator.hooklocator.data.IDataListener;
import ru.hookahlocator.hooklocator.data.entities.BaseObject;
import ru.hookahlocator.hooklocator.ui.ProgressView;

/**
 * Created by Igor Vydrenkov (yfrom5023@gmail.com)
 * Ноябрь 2015
 */
public class BaseLoadingActivity extends BaseActivity implements IDataListener {
    final static String TAG = "BaseLoadingActivity";

    private ProgressView __progressHolderNotForDirectUse;
    private View blockerView; //for block user interactions
    protected boolean needToShowProgress = true;

    @Override
    public void onDataReady(ArrayList<? extends BaseObject> data) {
        if (needToShowProgress) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                }
            });
        }
    }

    @Override
    public void onDataFailure() {
        Log.e(TAG, "Can't get data!");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (needToShowProgress) {
                    hideProgress();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(BaseLoadingActivity.this);
                builder.setTitle(R.string.error_title)
                        .setMessage(R.string.error_loading)
                        .setPositiveButton(R.string.error_btn_retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loadData();
                            }
                        })
                        .show();
            }
        });
    }

    protected void loadData() {
        if (needToShowProgress) {
            showProgress();
        }
    };

    void showProgress() {
        Log.v(TAG, "Show progress");
        ProgressView progressView = getProgressBar();
        if (blockerView != null) {
            blockerView.setVisibility(View.VISIBLE);
        }
        if (progressView != null) {
            progressView.show();
        }
    }

    void hideProgress() {
        Log.v(TAG, "Hide progress");
        ProgressView progressView = getProgressBar();
        if (blockerView != null) {
            blockerView.setVisibility(View.GONE);
        }
        if (progressView != null) {
            progressView.hide();
        }
    }

    private ProgressView getProgressBar() {
        if (__progressHolderNotForDirectUse != null) {
            return __progressHolderNotForDirectUse;
        }
        __progressHolderNotForDirectUse = addProgressBar();
        return __progressHolderNotForDirectUse;
    }

    private ProgressView addProgressBar() {
        try {
            ViewGroup contentFrame = (ViewGroup) findViewById(android.R.id.content);
            RelativeLayout root = (RelativeLayout) contentFrame.getChildAt(0);
            blockerView = new View(this);
            RelativeLayout.LayoutParams blockerParams =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            root.addView(blockerView, blockerParams);
            blockerView.setVisibility(View.GONE);
            ProgressView progressView = new ProgressView(this);
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            root.addView(progressView, params);
            progressView.setVisibility(View.GONE);
            return progressView;
        } catch (ClassCastException e) {
            e.printStackTrace();
            Log.e(TAG, "Root view of activity must be RelativeLayout!");
        }
        return null;
    }
}
