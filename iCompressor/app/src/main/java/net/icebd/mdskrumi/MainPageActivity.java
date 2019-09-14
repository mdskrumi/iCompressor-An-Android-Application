package net.icebd.mdskrumi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MainPageActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 777;
    private RelativeLayout imageButton  , videoButton;
    private RelativeLayout relativeLayout;
    private AnimationDrawable animationDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        setBackGroundAnimation();


        imageButton = findViewById(R.id.imageButtonInMainPage);
        videoButton = findViewById(R.id.videoButtonInMainPage);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkPermissions()){
                    Toast.makeText(MainPageActivity.this, "Don't have the required permissions", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainPageActivity.this, "Loading Your Images, Please Wait", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainPageActivity.this,ImagePageActivity.class));
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkPermissions()){
                    Toast.makeText(MainPageActivity.this, "Don't have the required permissions", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainPageActivity.this, "Loading Your Videos, Please Wait", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainPageActivity.this,VideoPageActivity.class));
            }
        });




    }
    private boolean checkPermissions() {
        if(ContextCompat.checkSelfPermission(this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this ,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE} , STORAGE_PERMISSION_CODE );
            return false;
        }
    return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    private void setBackGroundAnimation(){
        relativeLayout = findViewById(R.id.mainPageBgLayout);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3500);
        animationDrawable.setExitFadeDuration(3500);
        animationDrawable.start();
    }
}
