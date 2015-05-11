package com.artcode.toolbartests.helpers;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by nigel on 5/11/15.
 */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        Log.d("onScrolled", "callback invoked.");

        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible){

            if (firstVisibleItem != 0 ) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            }

        }
        else if(scrolledDistance < -HIDE_THRESHOLD && !controlsVisible){
            onShow();
            controlsVisible = true;
            scrolledDistance = 0;
        }

        if ( (controlsVisible && dy > 0) || (!controlsVisible && dy < 0) ){
            scrolledDistance += dy;
        }

    }

    public abstract void onHide();
    public abstract void onShow();


}
