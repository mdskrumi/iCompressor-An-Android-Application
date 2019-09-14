package net.icebd.mdskrumi;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.didar.extendedrecyclerview.Callback;
import com.didar.extendedrecyclerview.EasyAdapter;
import net.icebd.mdskrumi.ModelClass.MyVideo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoPageActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private AnimationDrawable animationDrawable;

    private RecyclerView recyclerView;
    private Button compressButton;
    private EasyAdapter<MyVideo> easyAdapter;



    private List<MyVideo> videos;
    private boolean[] selectedVideos;
    private List<String> videoPaths;

    private SearchView searchView;
    private List<MyVideo>filteredVideos;
    private TextView toolbarText;



    private LinearLayout dataIndicator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_page);
        setBackGroundAnimation();

        videoPaths = new ArrayList<>();
        videos = getVideoFilePaths();
        selectedVideos = new boolean[videos.size()];
        filteredVideos = new ArrayList<>();


        searchView = findViewById(R.id.searchViewVideoPage);
        toolbarText = findViewById(R.id.videoPageToolBarTextView);

        recyclerView = findViewById(R.id.videoRecyclerViewInVideoPage);
        compressButton = findViewById(R.id.compressVideoButton);

        dataIndicator = findViewById(R.id.videoDataIndicator);

        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        easyAdapter = new EasyAdapter.Builder<MyVideo>()
                .setData(videos)
                .setLayout(R.layout.row_image_layout)
                .setCallback(new Callback<MyVideo>() {
                    @Override
                    public void onBindViewHolder(@NotNull EasyAdapter.Companion.ViewHolder viewHolder, @NotNull final View view, final int i, final MyVideo s) {

                        final ImageView imageView = view.findViewById(R.id.rowImageView);
                        final ImageView checkerImageView = view.findViewById(R.id.rowCheckerImageView);

                        Glide.with(VideoPageActivity.this).load(Uri.fromFile(new File(s.getData())))
                                .thumbnail(0.5f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);


                        TextView duration = view.findViewById(R.id.imageDateInRow);
                        TextView size = view.findViewById(R.id.imageSizeInRow);

                        size.setText(getSizeAsString((double) s.getSize()));
                        duration.setText(s.getDuration());

                        if(videoPaths.contains(s.getData())){
                            checkerImageView.setVisibility(View.VISIBLE);
                            selectedVideos[i] = true;
                        }else{
                            checkerImageView.setVisibility(View.GONE);
                            selectedVideos[i] = false;
                        }

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(videoPaths.contains(s.getData())){
                                    videoPaths.remove(s.getData());
                                    selectedVideos[i] = false;
                                    checkerImageView.setVisibility(View.GONE);
                                    compressButton.setVisibility(View.GONE);
                                }else if(!videoPaths.contains(s.getData())){
                                    if(videoPaths.size()>0){
                                        Toast.makeText(VideoPageActivity.this, "Multiple Selection of Video isn't Allowed", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    selectedVideos[i] = true;
                                    videoPaths.add(s.getData());
                                    //showMe(s.getData());
                                    checkerImageView.setVisibility(View.VISIBLE);
                                    compressButton.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }
                })
                .build();
        recyclerView.setAdapter(easyAdapter);
        checkDataIndicator(easyAdapter.getItemCount());


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filteredVideos.clear();
                for(MyVideo i : videos){
                    if(i.getTitle().contains(query)){
                        filteredVideos.add(i);
                    }
                }
                selectedVideos = new boolean[filteredVideos.size()];
                easyAdapter.updateDataSet(filteredVideos);
                checkDataIndicator(easyAdapter.getItemCount());
                easyAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredVideos.clear();
                for(MyVideo i : videos){
                    if(i.getTitle().contains(newText)){
                        filteredVideos.add(i);
                    }
                }
                selectedVideos = new boolean[filteredVideos.size()];
                easyAdapter.updateDataSet(filteredVideos);
                checkDataIndicator(easyAdapter.getItemCount());
                easyAdapter.notifyDataSetChanged();
                return true;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarText.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                selectedVideos= new boolean[videos.size()];
                easyAdapter.updateDataSet(videos);
                checkDataIndicator(easyAdapter.getItemCount());
                easyAdapter.notifyDataSetChanged();
                return true;
            }
        });

        compressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(VideoPageActivity.this,CompressionPreFinalActivity.class)
                        .putExtra("myVideo",videoPaths.get(0))
                        .putExtra("konda",2));

            }
        });
    }

    private void checkDataIndicator(int length){
        if(length>0){
            dataIndicator.setVisibility(View.GONE);
        }else{
            dataIndicator.setVisibility(View.VISIBLE);
        }
    }
    private void setBackGroundAnimation(){
        relativeLayout = findViewById(R.id.videoPageBackground);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3500);
        animationDrawable.setExitFadeDuration(3500);
        animationDrawable.start();
    }
    private List<MyVideo> getVideoFilePaths() {
        List <MyVideo> myVideos = new ArrayList<>();
        Uri u = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.VideoColumns.TITLE ,MediaStore.Video.VideoColumns.DATA , MediaStore.Video.VideoColumns.DURATION , MediaStore.Video.VideoColumns.SIZE };
        Cursor c = null;

        if (u != null) {
            c = managedQuery(u, projection, null, null, android.provider.MediaStore.Video.Media.DATE_TAKEN + " DESC");
        }
        if ((c != null) && (c.moveToFirst())) {
            do {
                String tilte = c.getString(0);
                String data = c.getString(1);
                long duration = c.getLong(2);
                long size = c.getLong(3);
                if(size > 0 && new File(data).exists() && duration > 0)
                    myVideos.add(new MyVideo(tilte,data, getDurationAsString(duration),size));
            }
            while (c.moveToNext());
        }
        return myVideos;
    }
    void showMe(String Path){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(Path);
        String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);

        Toast.makeText(this, " " + width + " " + height + " " + bitrate, Toast.LENGTH_SHORT).show();

    }
    private String getSizeAsString(double size){

        size/=1024;
        if(size < 1024){
            return Double.parseDouble(String.format("%.0f", size)) + "Kb";
        }
        size/=1024;
        if(size < 1024){
            return Double.parseDouble(String.format("%.1f", size)) + "Mb";
        }
        size/=1024;
        return Double.parseDouble(String.format("%.1f", size)) + "Gb";

    }
    private String getDurationAsString(long milliSeconds) {
        milliSeconds /= 1000;
        long seconds = milliSeconds;
        if(seconds<60){
            return seconds+"s";
        }
        long minutes = seconds/60;
        seconds %= 60;
        if(minutes < 60){
            return minutes + ":" +seconds+"s";
        }
        long hour = minutes/60;
        minutes %= 60;
        return hour+":"+minutes + ":" +seconds+"s";
    }

}
