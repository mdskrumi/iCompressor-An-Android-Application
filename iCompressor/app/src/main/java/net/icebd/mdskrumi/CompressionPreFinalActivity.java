package net.icebd.mdskrumi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import yogesh.firzen.filelister.FileListerDialog;
import yogesh.firzen.filelister.OnFileSelectedListener;

public class CompressionPreFinalActivity extends AppCompatActivity {


    private FileListerDialog fileListerDialog;
    private TextView defaultPath , qualityTV , qualityIndicator;
    private SeekBar seekBar;
    private Switch aSwitch;
    private Button compressButton;
    private LinearLayout selectDir , setQualityLayout;
    private int quality;
    private int konda;
    private int deleteFiles ;

    private List<String> imagesPaths;
    private List<String> compressedImagesPaths;

    private String myVideo;
    private String compressedVideoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compression_pre_final);


        setQualityLayout = findViewById(R.id.selectQuality);

        imagesPaths = (List<String>) getIntent().getSerializableExtra("myImages");
        myVideo =  getIntent().getStringExtra("myVideo");
        konda = getIntent().getIntExtra("konda",1);

        File directory;
        if(konda==1){
            directory  = new File(Environment.getExternalStorageDirectory()+File.separator +"iCompressor/Images");
            compressedImagesPaths = new ArrayList<>();
        }
        else{
            directory  = new File(Environment.getExternalStorageDirectory()+File.separator +"iCompressor/Videos");
            compressedVideoPath = new String();
            //setQualityLayout.setVisibility(View.GONE);
        }
        if(!directory.exists())
            directory.mkdirs();


        fileListerDialog = FileListerDialog.createFileListerDialog(this);
        fileListerDialog.setDefaultDir(directory);
        fileListerDialog.setFileFilter(FileListerDialog.FILE_FILTER.DIRECTORY_ONLY);

        selectDir = findViewById(R.id.selectDirectory);
        defaultPath = findViewById(R.id.selectedStoragePath);
        qualityTV = findViewById(R.id.qualityTextView);
        seekBar = findViewById(R.id.seekBar);
        aSwitch = findViewById(R.id.deleteOriginalFilesSwitch);
        compressButton = findViewById(R.id.compressButtonInPrefinal);
        qualityIndicator = findViewById(R.id.qualityIndicatorText);


        quality = 1;
        qualityTV.setText(String.valueOf(quality) + "%");
        defaultPath.setText(directory.getAbsolutePath());

         seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               quality = progress;
               qualityTV.setText(String.valueOf(quality) + "%");
                if(quality < 20){
                    qualityIndicator.setText("Worst");
                    qualityIndicator.setTextColor(getResources().getColor(R.color.RED));
               }else if(quality < 40){
                    qualityIndicator.setText("Bad");
                    qualityIndicator.setTextColor(getResources().getColor(R.color.Orange));
                }else if(quality < 60){
                    qualityIndicator.setText("Moderate");
                    qualityIndicator.setTextColor(getResources().getColor(R.color.Yellow));
                }else if(quality < 80){
                    qualityIndicator.setText("Good");
                    qualityIndicator.setTextColor(getResources().getColor(R.color.Semigreen));
                }else {
                    qualityIndicator.setText("Best");
                   qualityIndicator.setTextColor(getResources().getColor(R.color.Green));
               }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    deleteFiles = 1;
                    new AlertDialog.Builder(CompressionPreFinalActivity.this)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aSwitch.setChecked(false);
                            deleteFiles = 0;
                        }
                    }).setTitle("Warning")
                            .setMessage("This will delete all the original files of selected items. Do you want to continue?")
                            .show();
                }else {
                    deleteFiles = 0;
                }
            }
        });

        selectDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileListerDialog.show();
            }
        });

        fileListerDialog.setOnFileSelectedListener(new OnFileSelectedListener() {
            @Override
            public void onFileSelected(File file, String path) {
                if(!defaultPath.getText().toString().equals(path)){
                    new AlertDialog.Builder(CompressionPreFinalActivity.this)
                            .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setTitle("Warning")
                            .setMessage("iCompressor will not find compressed item if location is changed")
                            .show();
                }
                defaultPath.setText(path);
            }
        });

        compressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (konda == 1){
                    startImageCompression();
                }
                else{
                    startVidoeCompression();
                }
            }
        });

    }



    private void compressImage(String s){
        try {
            File file = new Compressor(CompressionPreFinalActivity.this)
                    .setQuality(quality)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(defaultPath.getText().toString())
                    .compressToFile(new File(s)
                    );
            compressedImagesPaths.add(file.getAbsolutePath());
        } catch (IOException e) {

        }

    }
    private void startImageCompression(){
        View view = LayoutInflater.from(CompressionPreFinalActivity.this).inflate(R.layout.image_compressing_dialog,null);
        final ImageView currentImage = view.findViewById(R.id.currentImageView);
        final ProgressBar progressBar = view.findViewById(R.id.compressingProgressBar);
        final TextView progressText = view.findViewById(R.id.progressbarText);
        Button cancelButton = view.findViewById(R.id.cancelCompressing);
        progressBar.setMax(imagesPaths.size());


        final AlertDialog alertDialog = new AlertDialog.Builder(CompressionPreFinalActivity.this)
                .setView(view)
                .show();


        progressText.setText(progressBar.getProgress() + "/" + progressBar.getMax());
        Glide.with(CompressionPreFinalActivity.this).load(Uri.fromFile(new File(imagesPaths.get(0))))
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(currentImage);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog.isShowing()){
                    alertDialog.cancel();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                try {
                    while (i < imagesPaths.size()) {
                        progressBar.setProgress(i+1);
                        final String s = imagesPaths.get(i);
                        compressImage(s);
                        i++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressText.setText(progressBar.getProgress() + "/" + progressBar.getMax());
                                Glide.with(CompressionPreFinalActivity.this).load(Uri.fromFile(new File(s)))
                                        .thumbnail(0.5f)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(currentImage);
                            }
                        });
                        if (progressBar.getProgress() == progressBar.getMax()) {
                            alertDialog.dismiss();
                            finish();
                            startActivity(new Intent(CompressionPreFinalActivity.this,CompressResultActivity.class)
                                    .putExtra("myImages", (Serializable) imagesPaths)
                                    .putExtra("myCompressedImages", (Serializable) compressedImagesPaths)
                                    .putExtra("konda",1)
                                    .putExtra("delete",deleteFiles));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void compressVideo(String s){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(s);
        String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);

        int sendWidth = Integer.valueOf(width);
        int sendHeight = Integer.valueOf(height);
        int sendQuality = ( Integer.valueOf(bitrate) * quality ) / 100;


        try {
            compressedVideoPath = SiliCompressor.with(this).compressVideo(s, defaultPath.getText().toString() , sendWidth , sendHeight , sendQuality) ;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    private void startVidoeCompression(){

        View view = LayoutInflater.from(CompressionPreFinalActivity.this).inflate(R.layout.image_compressing_dialog,null);
        final ImageView currentVideo = view.findViewById(R.id.currentImageView);
        final ProgressBar progressBar = view.findViewById(R.id.compressingProgressBar);
        Button cancelButton = view.findViewById(R.id.cancelCompressing);
        progressBar.setIndeterminate(true);

        final AlertDialog alertDialog = new AlertDialog.Builder(CompressionPreFinalActivity.this)
                .setView(view)
                .show();

        Glide.with(CompressionPreFinalActivity.this).load(Uri.fromFile(new File(myVideo)))
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(currentVideo);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        new Thread(new Runnable() {
            public void run() {
                try {
                    compressVideo(myVideo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                alertDialog.dismiss();
                finish();
                startActivity(new Intent(CompressionPreFinalActivity.this,CompressResultActivity.class)
                        .putExtra("myVideo",myVideo)
                        .putExtra("myCompressedVideo",compressedVideoPath)
                        .putExtra("konda",2)
                        .putExtra("delete",deleteFiles));
            }
        }).start();

    }

}
