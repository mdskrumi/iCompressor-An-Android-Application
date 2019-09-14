package net.icebd.mdskrumi;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.didar.extendedrecyclerview.Callback;
import com.didar.extendedrecyclerview.EasyAdapter;

import net.icebd.mdskrumi.ModelClass.MyImage;

import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ImagePageActivity extends AppCompatActivity  {

    private RelativeLayout relativeLayout;
    private AnimationDrawable animationDrawable;
    private LinearLayout dataIndicator;
    private RecyclerView imageRecyclerView;
    private EasyAdapter<MyImage> easyAdapter;
    private Button compressButton;

    private SearchView searchView;
    private ImageView menuImage;
    private List<MyImage> filteredImages;
    private TextView toolbarText;


    private List<String> imagesPaths;
    private boolean[] selectedImages;
    private List<MyImage> images;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_page);
        setBackGroundAnimation();

        imagesPaths = new ArrayList<>();
        filteredImages = new ArrayList<>();
        images = getImageFilePaths();
        selectedImages = new boolean[images.size()];

        searchView = findViewById(R.id.searchViewImagePage);
        toolbarText = findViewById(R.id.imagePageToolBarTextView);
        menuImage = findViewById(R.id.menuForImagePage);
        dataIndicator = findViewById(R.id.imageDataIndicator);
        imageRecyclerView = findViewById(R.id.imageRecyclerViewInImagePage);
        compressButton = findViewById(R.id.doneSelectingImageButton);


        imageRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        easyAdapter = new EasyAdapter.Builder<MyImage>()
                .setLayout(R.layout.row_image_layout)
                .setData(images)
                .setCallback(new Callback<MyImage>() {
                    @Override
                    public void onBindViewHolder(@NotNull EasyAdapter.Companion.ViewHolder viewHolder, @NotNull View view, final int i, final MyImage s) {

                        final ImageView imageView = view.findViewById(R.id.rowImageView);
                        final ImageView checkerImageView = view.findViewById(R.id.rowCheckerImageView);
                        TextView takendate = view.findViewById(R.id.imageDateInRow);
                        TextView size = view.findViewById(R.id.imageSizeInRow);

                        takendate.setText(s.getTakenDate());
                        size.setText(getSizeAsString((double)s.getSize()));
                        Glide.with(ImagePageActivity.this).load(Uri.fromFile(new File(s.getData())))
                                .thumbnail(0.5f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);
                        if(imagesPaths.contains(s.getData())){
                            checkerImageView.setVisibility(View.VISIBLE);
                            selectedImages[i] = true;
                        }else{
                            checkerImageView.setVisibility(View.GONE);
                            selectedImages[i] = false;
                        }

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!imagesPaths.contains(s.getData())){
                                    imagesPaths.add(s.getData());
                                    checkerImageView.setVisibility(View.VISIBLE);
                                    selectedImages[i] = true;
                                }else{
                                    imagesPaths.remove(s.getData());
                                    checkerImageView.setVisibility(View.GONE);
                                    selectedImages[i] = false;
                                }
                                checkCompressButtonVisibility();
                            }
                        });

                    }
                })
                .build();
        imageRecyclerView.setAdapter(easyAdapter);
        checkDataIndicator(easyAdapter.getItemCount());


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filteredImages.clear();
                for(MyImage i : images){
                    if(i.getTakenDate().contains(query)){
                        filteredImages.add(i);
                    }
                }
                selectedImages = new boolean[filteredImages.size()];
                easyAdapter.updateDataSet(filteredImages);
                checkDataIndicator(easyAdapter.getItemCount());
                easyAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredImages.clear();
                for(MyImage i : images){
                    if(i.getTakenDate().contains(newText)){
                        filteredImages.add(i);
                    }
                }
                selectedImages = new boolean[filteredImages.size()];
                easyAdapter.updateDataSet(filteredImages);
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
                selectedImages = new boolean[images.size()];
                easyAdapter.updateDataSet(images);
                checkDataIndicator(easyAdapter.getItemCount());
                easyAdapter.notifyDataSetChanged();
                return true;
            }
        });

        menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ImagePageActivity.this, menuImage);
                popup.getMenuInflater().inflate(R.menu.selectmenu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.seleteAllMenu){
                            selectAll();
                            checkCompressButtonVisibility();
                        }
                        if(item.getItemId() == R.id.unseleteAllMenu){
                            unselectAll();
                            checkCompressButtonVisibility();
                        }
                        easyAdapter.notifyDataSetChanged();
                        return true;
                    }
                });
                popup.show();
            }
        });


        compressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ImagePageActivity.this,CompressionPreFinalActivity.class)
                        .putExtra("myImages", (Serializable) imagesPaths)
                        .putExtra("konda",1));


            }
        });
    }


    private void selectAll(){
        if(easyAdapter.getItemCount()==0)return;
        if(filteredImages.isEmpty()){
            for(int i = 0 ; i < images.size() ; i++){
                selectedImages[i] = true;
                if(!imagesPaths.contains(images.get(i).getData())){
                    imagesPaths.add(images.get(i).getData());
                }
            }
        }else{
            for(int i = 0; i < filteredImages.size() ; i++){
                selectedImages[i] = true;
                if(!imagesPaths.contains(filteredImages.get(i).getData())){
                    imagesPaths.add(filteredImages.get(i).getData());
                }
            }
        }
    }
    private void unselectAll(){
        if(easyAdapter.getItemCount()==0)return;
        if(filteredImages.isEmpty()){
            for(int i = 0 ; i < images.size() ; i++){
                selectedImages[i] = false;
                if(imagesPaths.contains(images.get(i).getData())){
                    imagesPaths.remove(images.get(i).getData());
                }
            }
        }else{
            for(int i = 0; i < filteredImages.size() ; i++){
                selectedImages[i] = false;
                if(imagesPaths.contains(filteredImages.get(i).getData())){
                    imagesPaths.remove(filteredImages.get(i).getData());
                }
            }
        }
    }
    private void checkCompressButtonVisibility(){
        if(imagesPaths.size()>0){
            compressButton.setVisibility(View.VISIBLE);
            compressButton.setText("Next " + "(" + imagesPaths.size() + ")");
        }else{
            compressButton.setVisibility(View.GONE);
            compressButton.setText("Next " + "(" + imagesPaths.size() + ")");
        }
    }
    private void checkDataIndicator(int length){
        if(length>0){
            dataIndicator.setVisibility(View.GONE);
        }else{
            dataIndicator.setVisibility(View.VISIBLE);
        }
    }


    private void setBackGroundAnimation(){
        relativeLayout = findViewById(R.id.imagePageBackground);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3500);
        animationDrawable.setExitFadeDuration(3500);
        animationDrawable.start();
    }
    private List<MyImage> getImageFilePaths() {
        List<MyImage> myImages = new ArrayList<>();
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA , MediaStore.Images.ImageColumns.DATE_TAKEN ,MediaStore.Images.ImageColumns.SIZE};
        Cursor c = null;

        if (u != null) {
            c = managedQuery(u, projection, null, null, android.provider.MediaStore.Images.Media.DATE_TAKEN + " DESC");
        }
        if ((c != null) && (c.moveToFirst())) {
            do {
                String data = c.getString(0);
                String takenDate = getDateAsString(c.getLong(1));
                long size = c.getLong(2);
                if(size > 0 && new File(data).exists())
                    myImages.add(new MyImage(data,takenDate,size));
            }
            while (c.moveToNext());
        }


        return myImages;
    }
    private String getDateAsString(long milliSeconds) {
        SimpleDateFormat  fmt = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return fmt.format(calendar.getTime());
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
