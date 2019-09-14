package net.icebd.mdskrumi;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

public class ImageShowActivity extends AppCompatActivity {

    private ImageView imageView;
    private VideoView videoView;
    private FrameLayout frameLayout;
    private int konda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);

        imageView = findViewById(R.id.showImageImageView);
        videoView = findViewById(R.id.showVideoVideoView);
        frameLayout = findViewById(R.id.videoFrameLayout);


        konda = getIntent().getIntExtra("konda",0);

        if(konda==0){
            onBackPressed();
        }
        else if(konda == 1){
            imageView.setVisibility(View.VISIBLE);
            String s = getIntent().getStringExtra("imagePath");
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            Glide.with(this).load(Uri.fromFile(new File(s)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }else{
            videoView.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.VISIBLE);
            String s = getIntent().getStringExtra("videoPath");
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            videoView.setVideoPath(s);
            videoView.setMediaController(new MediaController(this));
            videoView.start();
        }

    }
}
