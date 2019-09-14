package net.icebd.mdskrumi.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.didar.extendedrecyclerview.Callback;
import com.didar.extendedrecyclerview.EasyAdapter;

import net.icebd.mdskrumi.ImageShowActivity;
import net.icebd.mdskrumi.ModelClass.MyVideo;
import net.icebd.mdskrumi.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFolderFragment extends Fragment {


    private RecyclerView recyclerView;
    private EasyAdapter<MyVideo>easyAdapter;
    private LinearLayout dataIndicator;
    private SearchView searchView;

    private List<MyVideo> myVideos,filteredVideos;

    private boolean[] selectedVideos;
    private List<String> videoPaths;

    private Button deleteVideo;

    public VideoFolderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_folder, container, false);

        recyclerView = view.findViewById(R.id.videoRecyclerViewInVideoFolder);
        dataIndicator = view.findViewById(R.id.videoDataIndicatorVideoFolder);
        searchView = view.findViewById(R.id.searchViewVideoFolder);
        deleteVideo = view.findViewById(R.id.deleteVideoButtonInVideoFolder);

        getVideoPaths();
        videoPaths = new ArrayList<>();
        selectedVideos = new boolean[myVideos.size()];
        filteredVideos = new ArrayList<>();

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        easyAdapter = new EasyAdapter.Builder<MyVideo>()
                .setData(myVideos)
                .setLayout(R.layout.row_image_layout)
                .setCallback(new Callback<MyVideo>() {
                    @Override
                    public void onBindViewHolder(@NotNull EasyAdapter.Companion.ViewHolder viewHolder, @NotNull final View view, final int i, final MyVideo s) {

                        final ImageView imageView = view.findViewById(R.id.rowImageView);
                        final ImageView checkerImageView = view.findViewById(R.id.rowCheckerImageView);
                        Glide.with(getActivity()).load(Uri.fromFile(new File(s.getData())))
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
                        view.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if(!videoPaths.contains(s.getData())){
                                    videoPaths.add(s.getData());
                                    checkerImageView.setVisibility(View.VISIBLE);
                                    selectedVideos[i] = true;
                                }else{
                                    videoPaths.remove(s.getData());
                                    checkerImageView.setVisibility(View.GONE);
                                    selectedVideos[i] = false;
                                }
                                checkDeleteButtonVisibility();
                                return true;
                            }
                        });

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(videoPaths.size() > 0){
                                    if(!videoPaths.contains(s.getData())){
                                        videoPaths.add(s.getData());
                                        checkerImageView.setVisibility(View.VISIBLE);
                                        selectedVideos[i] = true;
                                    }else{
                                        videoPaths.remove(s.getData());
                                        checkerImageView.setVisibility(View.GONE);
                                        selectedVideos[i] = false;
                                    }
                                    checkDeleteButtonVisibility();
                                }else {
                                    startActivity(new Intent(getActivity(), ImageShowActivity.class)
                                            .putExtra("videoPath",s.getData())
                                            .putExtra("konda",2));
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
                for(MyVideo i : myVideos){
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
                for(MyVideo i : myVideos){
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

        deleteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String s : videoPaths){
                            new File(s).delete();
                        }
                        getVideoPaths();
                        selectedVideos = new boolean[myVideos.size()];
                        videoPaths = new ArrayList<>();
                        checkDeleteButtonVisibility();
                        easyAdapter.updateDataSet(myVideos);
                        checkDataIndicator(easyAdapter.getItemCount());
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setTitle("Delete Video").setMessage("Are You sure?").show();
            }
        });


        return view;
    }
    private void getVideoPaths(){
        myVideos = new ArrayList<>();
        File imageDirectory = new File(Environment.getExternalStorageDirectory()+File.separator +"iCompressor/Videos");
        File[] videoList = imageDirectory.listFiles();
        if(videoList == null){
            return;
        }
        for (File videoPath : videoList) {
            try {
                if ( videoPath.getName().contains(".mp4")|| videoPath.getName().contains(".MP4")){
                    String title = videoPath.getName();
                    String data = videoPath.getAbsolutePath();

                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(getActivity(), Uri.fromFile(videoPath));
                    long duration = Long.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    long size = videoPath.length();

                    if(duration > 0 && size > 0 && videoPath.exists()){
                        myVideos.add(new MyVideo(title, data, getDurationAsString(duration), size));
                    }

                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
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
    private void checkDataIndicator(int length){
        if(length>0){
            dataIndicator.setVisibility(View.GONE);
        }else{
            dataIndicator.setVisibility(View.VISIBLE);
        }
    }

    private void checkDeleteButtonVisibility(){
        if(videoPaths.size()>0){
            deleteVideo.setVisibility(View.VISIBLE);
            deleteVideo.setText("Delete " + "(" + videoPaths.size() + ")");
        }else{
            deleteVideo.setVisibility(View.GONE);
            deleteVideo.setText("Delete " + "(" + videoPaths.size() + ")");
        }
    }
}
