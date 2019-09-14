package net.icebd.mdskrumi.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.icebd.mdskrumi.MainPageActivity;
import net.icebd.mdskrumi.MediaFolderActivity;
import net.icebd.mdskrumi.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    private Button startButton;
    private Button navigationButton;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_start, container, false);
        startButton = v.findViewById(R.id.startButtonInStartPage);
        navigationButton = v.findViewById(R.id.navigationViewButton);

        drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        navigationView = getActivity().findViewById(R.id.nav_view);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.nav_feed_back){
                    drawerLayout.closeDrawers();
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.startPageContainer,new FeedBackFragment())
                            .commit();
                    return true;

                }
                if(menuItem.getItemId() == R.id.nav_mediaFolder){
                    drawerLayout.closeDrawers();
                    startActivity(new Intent(getActivity(), MediaFolderActivity.class));
                    return true;

                }
                return false;
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainPageActivity.class));
            }
        });

        return v;
    }
}
