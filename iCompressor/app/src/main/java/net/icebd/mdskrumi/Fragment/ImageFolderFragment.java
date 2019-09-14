package net.icebd.mdskrumi.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import net.icebd.mdskrumi.ModelClass.MyImage;
import net.icebd.mdskrumi.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFolderFragment extends Fragment {



    private RecyclerView recyclerView;
    private EasyAdapter<MyImage> easyAdapter;
    private SearchView searchView;
    private LinearLayout dataIndicator;

    private List<MyImage> myImages , filteredImages;

    private List<String> imagesPaths;
    private boolean[] selectedImages;

    private Button deleteImage;

    public ImageFolderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_folder, container, false);

        getImagePaths();
        imagesPaths = new ArrayList<>();
        selectedImages = new boolean[myImages.size()];

        filteredImages = new ArrayList<>();
        recyclerView = view.findViewById(R.id.imageRecyclerViewInImageFolder);
        searchView = view.findViewById(R.id.searchViewImageFolder);
        dataIndicator = view.findViewById(R.id.imageDataIndicatorImageFolder);
        deleteImage = view.findViewById(R.id.deleteSelectingImageButton);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        easyAdapter = new EasyAdapter.Builder<MyImage>()
                .setLayout(R.layout.row_image_layout)
                .setData(myImages)
                .setCallback(new Callback<MyImage>() {
                    @Override
                    public void onBindViewHolder(@NotNull EasyAdapter.Companion.ViewHolder viewHolder, @NotNull View view, final int i, final MyImage s) {

                        ImageView imageView = view.findViewById(R.id.rowImageView);
                        final ImageView checkerImageView = view.findViewById(R.id.rowCheckerImageView);
                        TextView takendate = view.findViewById(R.id.imageDateInRow);
                        TextView size = view.findViewById(R.id.imageSizeInRow);
                        takendate.setText(s.getTakenDate());
                        size.setText(getSizeAsString((double)s.getSize()));
                        Glide.with(getActivity()).load(Uri.fromFile(new File(s.getData())))
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
                        checkDeleteButtonVisibility();

                        view.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if(!imagesPaths.contains(s.getData())){
                                    imagesPaths.add(s.getData());
                                    checkerImageView.setVisibility(View.VISIBLE);
                                    selectedImages[i] = true;
                                }else{
                                    imagesPaths.remove(s.getData());
                                    checkerImageView.setVisibility(View.GONE);
                                    selectedImages[i] = false;
                                }
                                checkDeleteButtonVisibility();
                                return true;
                            }
                        });

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(imagesPaths.size() > 0){
                                    if(!imagesPaths.contains(s.getData())){
                                        imagesPaths.add(s.getData());
                                        checkerImageView.setVisibility(View.VISIBLE);
                                        selectedImages[i] = true;
                                    }else{
                                        imagesPaths.remove(s.getData());
                                        checkerImageView.setVisibility(View.GONE);
                                        selectedImages[i] = false;
                                    }
                                    checkDeleteButtonVisibility();
                                }else {
                                    startActivity(new Intent(getActivity(), ImageShowActivity.class)
                                            .putExtra("imagePath",s.getData())
                                            .putExtra("konda",1));
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
                filteredImages.clear();
                for(MyImage i : myImages){
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
                for(MyImage i : myImages){
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

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String s : imagesPaths){
                            new File(s).delete();
                        }
                        getImagePaths();
                        selectedImages = new boolean[myImages.size()];
                        imagesPaths = new ArrayList<>();
                        checkDeleteButtonVisibility();
                        easyAdapter.updateDataSet(myImages);
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
                }).setTitle("Delete Images").setMessage("Are You sure?").show();
            }
        });

        return view;
    }


    private void getImagePaths(){
        myImages = new ArrayList<>();
        File imageDirectory = new File(Environment.getExternalStorageDirectory()+File.separator +"iCompressor/Images");
        File[] imageList = imageDirectory.listFiles();
        if(imageList == null){
            return;
        }
        for (File imagePath : imageList) {
            try {
                if ( imagePath.getName().contains(".jpg")|| imagePath.getName().contains(".JPG")
                        || imagePath.getName().contains(".jpeg")|| imagePath.getName().contains(".JPEG")
                ){
                    String data = imagePath.getAbsolutePath();
                    String takenDate= getDateAsString(imagePath.lastModified());
                    long size = imagePath.length();
                    if(size > 0 && new File(data).exists())
                         myImages.add(new MyImage(data,takenDate,size));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }
    private String getDateAsString(long milliSeconds) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
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
    private void checkDataIndicator(int length){
        if(length>0){
            dataIndicator.setVisibility(View.GONE);
        }else{
            dataIndicator.setVisibility(View.VISIBLE);
        }
    }
    private void checkDeleteButtonVisibility(){
        if(imagesPaths.size()>0){
            deleteImage.setVisibility(View.VISIBLE);
            deleteImage.setText("Delete " + "(" + imagesPaths.size() + ")");
        }else{
            deleteImage.setVisibility(View.GONE);
            deleteImage.setText("Delete " + "(" + imagesPaths.size() + ")");
        }
    }

}
