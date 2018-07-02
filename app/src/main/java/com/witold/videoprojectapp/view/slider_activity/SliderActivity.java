package com.witold.videoprojectapp.view.slider_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;

import com.witold.videoprojectapp.R;
import com.witold.videoprojectapp.view.main_activity.MainActivity;
import com.witold.videoprojectapp.view.slider_activity.view_pager.CustomPagerAdapter;
import com.witold.videoprojectapp.view.slider_activity.view_pager.SliderElement;

import java.util.Arrays;

public class SliderActivity extends AppCompatActivity {
    private Button buttonShowMainActivity;
    private ViewPager viewPager;
    private ConstraintLayout constraintLayout;
    public CustomPagerAdapter mAdapter;
    public final static int PAGES = 5;
    public final static int FIRST_PAGE = 0  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        initializeComponents();
        initializeButtonShowMainActivity();
        initializeViewPager();
    }

    private void initializeComponents(){
        this.buttonShowMainActivity = findViewById(R.id.button_show_main_activity);
        this.viewPager = findViewById(R.id.view_pager_slider);
        this.constraintLayout = findViewById(R.id.constraint_layout_slider_activity);
    }

    private void initializeViewPager(){

        mAdapter = new CustomPagerAdapter(this, this.getSupportFragmentManager(), Arrays.asList(
                new SliderElement(R.drawable.make_videos_and_photos, "Rób zdjęcia i nagrywaj filmy!"),
                new SliderElement(R.drawable.handle_videos, "Odtwarzaj nagrane filmy i zarządzaj nimi"),
                new SliderElement(R.drawable.handle_photos, "Zarządzaj zrobionymi zdjęciami"),
                new SliderElement(R.drawable.sort_data, "Sortuj dane i wyświetlaj je jak chcesz"),
                new SliderElement(R.drawable.change_settings, "Zmieniaj ustawienia i dostosuj aplikację do siebie!")
                ));
        viewPager.setAdapter(mAdapter);
        viewPager.setPageTransformer(false, mAdapter);
        viewPager.setCurrentItem(FIRST_PAGE);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageMargin(-300);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == mAdapter.getCount() - 1){
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    buttonShowMainActivity.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initializeButtonShowMainActivity(){
        this.buttonShowMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMainActivity();
            }
        });
    }

    private void showMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
