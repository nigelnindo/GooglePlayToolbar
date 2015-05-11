package com.artcode.toolbartests.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.artcode.toolbartests.R;
import com.artcode.toolbartests.adapters.MyAdapter;
import com.artcode.toolbartests.helpers.HidingScrollListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (ImageButton) findViewById(R.id.my_button);
        initToolBar();
        initRecyclerView();
    }

    private void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(this, populateItems()));
        recyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });
    }

    private List<String> populateItems(){

        List<String> myList = new ArrayList<>();
        for (int i = 1; i <= 100; i++){
            myList.add("Item" + i);
        }
        return myList;
    }

    private void hideViews(){
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(
            new AccelerateInterpolator(2));

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) button.getLayoutParams();
        int buttonBottomMargin = lp.bottomMargin;



        button.animate().translationY(button.getHeight() + buttonBottomMargin).setInterpolator(
            new AccelerateInterpolator(2)).start();
    }

    private void showViews(){
        toolbar.animate().translationY(0).setInterpolator(
            new DecelerateInterpolator(2));
        button.animate().translationY(0).setInterpolator(
            new DecelerateInterpolator(2)).start();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
