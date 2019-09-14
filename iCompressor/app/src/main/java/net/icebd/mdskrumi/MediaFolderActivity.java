package net.icebd.mdskrumi;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.icebd.mdskrumi.Fragment.ImageFolderFragment;
import net.icebd.mdskrumi.Fragment.VideoFolderFragment;

public class MediaFolderActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_folder);

        tabLayout = findViewById(R.id.mediaPageTabLayout);
        viewPager = findViewById(R.id.mediaPageViewPager);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.noimage).setText("Image"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.novideo).setText("Video"));

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.setTabIconTintResource(R.color.WHITE);
        tabLayout.setTabTextColors(getResources().getColor(R.color.SILVER),getResources().getColor(R.color.WHITE));


        MyPageAdapter adapter = new MyPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private class MyPageAdapter extends FragmentPagerAdapter{

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0: return new ImageFolderFragment();
                case 1: return new VideoFolderFragment();
            }
            return null;

        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}













