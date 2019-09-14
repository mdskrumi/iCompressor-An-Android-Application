package net.icebd.mdskrumi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.Toast;
import net.icebd.mdskrumi.Fragment.StartFragment;


public class StartPageActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private AnimationDrawable animationDrawable;

    private ConnectivityReceiver connectivityReceiver;

    private static int USER_NET_STATE = 0;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private int leave;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        init();         // Initializing background animation and layout attributes
        getSupportFragmentManager().beginTransaction().replace(R.id.startPageContainer ,new StartFragment()).commit();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        leave = 0;

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
            leave = 0;
        }
        else if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            leave = 0;
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        else {
            if(leave == 0){
                leave = 1;
                Toast.makeText(this, "Press Back Button Again To Exit", Toast.LENGTH_SHORT).show();
            }else
                super.onBackPressed();
        }
    }

    void init(){
        relativeLayout = findViewById(R.id.startPageBgLayout);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3500);
        animationDrawable.setExitFadeDuration(3500);
        animationDrawable.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        );
        leave = 0;
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }
    public class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if(info != null && info.isConnected()){
               // Toast.makeText(StartPageActivity.this, "You'r Online", Toast.LENGTH_SHORT).show();
                USER_NET_STATE = 1;
            }else{
              //  Toast.makeText(StartPageActivity.this, "You'r Offline", Toast.LENGTH_SHORT).show();
                USER_NET_STATE = 0;
            }
        }
    }


}
