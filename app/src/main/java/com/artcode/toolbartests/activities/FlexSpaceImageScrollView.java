package com.artcode.toolbartests.activities;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artcode.toolbartests.R;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class FlexSpaceImageScrollView extends ActionBarActivity implements ObservableScrollViewCallbacks {

    public static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    public static final boolean TOOLBAR_IS_STICKY = false;

    private View mToolbar;
    private View mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private View mFab;

    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mToolbarColor;
    private int mFabMargin;
    private boolean mFabIsShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flex_space_image_scroll_view);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setTitle(R.string.title_activity_flex_image);

        final TypedArray styledAttributes = this.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        mActionBarSize = (int) styledAttributes.getDimension(0, 0);


        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        Log.d("Flex Image Height",""+mFlexibleSpaceImageHeight);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        //mActionBarSize = R.attr.actionBarSize;
        Log.d("Action Bar Size", ""+mActionBarSize);
        mToolbarColor = getResources().getColor(R.color.colorPrimary);

        mToolbar = findViewById(R.id.toolbar);

        Log.d("Toolbar height", ""+mToolbar.getHeight());

        if (!TOOLBAR_IS_STICKY){
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }

        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(getSupportActionBar().getTitle());
        getSupportActionBar().setTitle(null);
        mFab = findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FlexSpaceImageScrollView.this, "FAB is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);

        ViewHelper.setScaleX(mFab,0);
        ViewHelper.setScaleY(mFab, 0);



        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0,mFlexibleSpaceImageHeight - mActionBarSize);
                //the following line makes it more intuitive to the user
                mScrollView.scrollTo(0,0);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flex_space_image_scroll_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

        Log.d("Scroll Y", ""+scrollY);
        Log.d("First scroll?", ""+firstScroll);
        Log.d("Dragging?",""+dragging);

        Log.d("mActionBarSize",""+mActionBarSize);
        Log.d("mOverLayView height",""+mOverlayView.getHeight());

        //Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY,minOverlayTransitionY,0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY/2,minOverlayTransitionY,0));

        //change alpha of the overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float)scrollY/flexibleRange,0,1));

        //scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY)/flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mTitleView,0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView,scale);

        //translate title text
        int maxTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTranslationY - scrollY;
        if (TOOLBAR_IS_STICKY){
            titleTranslationY = Math.max(0,titleTranslationY);
        }
        ViewHelper.setTranslationY(mTitleView,titleTranslationY);

        //translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight()/2;
        float fabTranslationY = ScrollUtils.getFloat(
            -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight()/2,
            mActionBarSize - mFab.getHeight()/2,
            maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
        }
        else{
            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
            ViewHelper.setTranslationY(mFab, fabTranslationY);
        }

        if (fabTranslationY < mFlexibleSpaceShowFabOffset){
            hideFab();
        }
        else{
            showFab();
        }

        if (TOOLBAR_IS_STICKY){
            //change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize){
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(1,mToolbarColor));
            }
            else{
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0,mToolbarColor));
            }
        }
        else{
            //translate toolbar
            if (scrollY < mFlexibleSpaceImageHeight){
                ViewHelper.setTranslationY(mToolbar,0);
            }
            else{
                ViewHelper.setTranslationY(mToolbar, -scrollY);
            }

        }

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }
}
