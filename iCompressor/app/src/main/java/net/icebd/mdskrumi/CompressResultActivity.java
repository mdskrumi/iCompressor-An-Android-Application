package net.icebd.mdskrumi;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CompressResultActivity extends AppCompatActivity {


    private RelativeLayout relativeLayout;
    private AnimationDrawable animationDrawable;

    private TextView previousSize, currentSize , topText , inPercent;
    private ImageView preImage , postImage ,topImage;
    private Button backToMenu , gotoResult;

    private List<String> myImages;
    private List<String> compressedImagesPaths;

    private String myVideo;
    private String compressedVideo;

    private int deleteFiles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress_result);


        myImages = new ArrayList<>();
        compressedImagesPaths = new ArrayList<>();

        init();


        previousSize = findViewById(R.id.netSizeBeforeCompressTextView);
        currentSize = findViewById(R.id.netSizeAfterCompressTextView);
        topText = findViewById(R.id.resultpageTopText);
        backToMenu = findViewById(R.id.finalPageButton);
        gotoResult = findViewById(R.id.gotoResultPage);
        inPercent = findViewById(R.id.resultInPercent);

        preImage = findViewById(R.id.preImage);
        postImage = findViewById(R.id.postImage);
        topImage = findViewById(R.id.resultpageTopImage);

        int konda = getIntent().getIntExtra("konda",1);

        myImages = (List<String>) getIntent().getSerializableExtra("myImages");

        compressedImagesPaths = (List<String>) getIntent().getSerializableExtra("myCompressedImages");

        deleteFiles = getIntent().getIntExtra("delete",0);

        myVideo =  getIntent().getStringExtra("myVideo");
        compressedVideo = getIntent().getStringExtra("myCompressedVideo");


        if(konda == 1){
            long ps =0  , cs=0;
            for(int i = 0 ; i < myImages.size() ; i++){
                File after = new File(compressedImagesPaths.get(i));
                File before = new File(myImages.get(i));
                ps += before.length();
                cs += after.length();
            }

            long getPercent = cs*100/ps;
            inPercent.setText(String.valueOf(getPercent) + "%");

            if(ps <= cs){
                preImage.setImageResource(R.drawable.facefailed);
                postImage.setImageResource(R.drawable.sad);
                topText.setText("Failed To Compress");
                topText.setTextColor(getResources().getColor(R.color.RED));
                topImage.setImageResource(R.drawable.failed);
                inPercent.setTextColor(getResources().getColor(R.color.RED));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(String s : compressedImagesPaths){
                            new File(s).delete();
                        }
                    }
                }).start();
                Toast.makeText(this, "No file is saved", Toast.LENGTH_SHORT).show();
            }else{
                if(deleteFiles == 1){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i = 0 ; i < myImages.size() ; i++){
                                new File(myImages.get(i)).delete();
                            }
                        }
                    }).start();
                }
            }

            previousSize.setText(getSizeAsString(ps));
            currentSize.setText(getSizeAsString(cs));

        }else {

            File before = new File(myVideo);
            File after = new File(compressedVideo);
            previousSize.setText(getSizeAsString(before.length()));
            currentSize.setText(getSizeAsString(after.length()));

            long ps = before.length() , cs = after.length();

            long getPercent = cs*100/ps;
            inPercent.setText(String.valueOf(getPercent) + "%");

            if(ps <= cs){
                preImage.setImageResource(R.drawable.facefailed);
                postImage.setImageResource(R.drawable.sad);
                topText.setText("Failed To Compress");
                topText.setTextColor(getResources().getColor(R.color.RED));
                topImage.setImageResource(R.drawable.failed);
                Toast.makeText(this, "No file is saved", Toast.LENGTH_SHORT).show();
                after.delete();
                inPercent.setTextColor(getResources().getColor(R.color.RED));
            }
            else {
                if(deleteFiles == 1){
                    new File(myVideo).delete();
                }
            }
        }

        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gotoResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(CompressResultActivity.this , MediaFolderActivity.class));
            }
        });

    }
    void init(){
        relativeLayout = findViewById(R.id.finalLayout);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3500);
        animationDrawable.setExitFadeDuration(3500);
        animationDrawable.start();
    }
    private String getSizeAsString(double size){
        size/=1024;
        if(size < 1024){
            return Double.parseDouble(String.format("%.1f", size)) + "Kb";
        }
        size/=1024;
        if(size < 1024){
            return Double.parseDouble(String.format("%.1f", size)) + "Mb";
        }
        size/=1024;
        return Double.parseDouble(String.format("%.1f", size)) + "Gb";

    }


}
