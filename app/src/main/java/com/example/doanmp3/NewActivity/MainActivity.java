package com.example.doanmp3.NewActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.doanmp3.Fragment.MainFragment.HomeFragment;
import com.example.doanmp3.Fragment.MainFragment.NewsFragment;
import com.example.doanmp3.Fragment.MainFragment.UserFragment;
import com.example.doanmp3.NewAdapter.ViewPagerAdapter;
import com.example.doanmp3.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    LinearLayout searchLayout;
    CircleImageView userThumbnail;
    TextInputEditText edtSearch;
    ImageView btnOptions;

    //Fragments
    UserFragment userFragment;
    HomeFragment homeFragment;
    NewsFragment newsFragment;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        InitControls();
        InitFragment();
        SetUpBottomNavigation();
        SetUpViewPager();
    }

    private void InitControls() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_main_activity);
        viewPager = findViewById(R.id.view_pager_main_activity);
        searchLayout = findViewById(R.id.layout_search_main_activity);
        userThumbnail = findViewById(R.id.thumbnail_user);
        edtSearch = findViewById(R.id.edt_search_main_activity);
        btnOptions = findViewById(R.id.btn_options_main_activity);
    }

    private void InitFragment() {
        userFragment = new UserFragment();
        homeFragment = new HomeFragment();
        newsFragment = new NewsFragment();
    }

    @SuppressLint("NonConstantResourceId")
    private void SetUpBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.newsFragment:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.userFragment:
                    viewPager.setCurrentItem(2);
                    break;
                default:
                    viewPager.setCurrentItem(0);
            }
            return true;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void SetUpViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(homeFragment);
        fragments.add(newsFragment);
        fragments.add(userFragment);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int id;
                switch (position) {
                    case 1:
                        id = R.id.newsFragment;
                        break;
                    case 2:
                        id = R.id.userFragment;
                        break;
                    default:
                        id = R.id.homeFragment;
                }
                bottomNavigationView.getMenu().findItem(id).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}