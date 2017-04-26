package com.project.yura.photoeditor;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.project.yura.photoeditor.Model.CurrentSession;
import com.project.yura.photoeditor.Model.CustomFilters;
import com.project.yura.photoeditor.Model.Helper;
import com.project.yura.photoeditor.Model.IFilter;
import com.project.yura.photoeditor.Model.PreferencesHelper;
import com.project.yura.photoeditor.Model.PreviewData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ApplyFilterActivity extends AppCompatActivity {
    private List<PreviewData> previews;
    private PreviewData selectedFilter;
    private Bitmap editedBitmap = null;
    private Bitmap scaledOriginalBitmap = null;
    private Bitmap realEditedBitmap = null;
    private CurrentSession currentSession;
    private ImageView imageView;
    private ImageView previewButton;
    private ImageView resizeButton;
    private ImageView likeButton;
    private SeekBar seekBar;
    private ViewGroup barToHide;
    PreviewAdapter adapter;
    RecyclerView recyclerView;
    //private LinearLayout workspaceLayout;

    private boolean displayOriginal;
    private boolean displayLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_filter);

        currentSession = CurrentSession.GetInstance();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        scaledOriginalBitmap = currentSession.currentBitmap;/*Helper.ResizeBitmap(
                currentSession.currentBitmap, displayMetrics.widthPixels, displayMetrics.heightPixels, false);*/
        editedBitmap = scaledOriginalBitmap; //currentSession.currentBitmap;
        imageView = (ImageView)findViewById(R.id.imageToEdit);
        imageView.setImageBitmap(scaledOriginalBitmap); //currentSession.currentBitmap
        previewButton = (ImageView)findViewById(R.id.preview_button);
        resizeButton = (ImageView)findViewById(R.id.resize_button);
        likeButton = (ImageView)findViewById(R.id.like_button);
        barToHide = (ViewGroup)findViewById(R.id.layout_to_hide);
        //workspaceLayout = (LinearLayout) findViewById(R.id.activity_apply_filter);
        seekBar = (SeekBar)findViewById(R.id.seek_bar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    applyFilter();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                applyFilter();
            }
        });

        CustomFilters customFilters = new CustomFilters(this);

        //create previews

        Bitmap previewBitmap = Helper.ResizeBitmap(editedBitmap, 2*72, 2*80, true);

        int weight = 50;
        previews = new ArrayList<>();
        IFilter[] filters = customFilters.GetFilters();
        Set<String> favorites = new PreferencesHelper(this).getFavoriteFilters();
        for (IFilter f : filters) {
            previews.add(new PreviewData(
                    f.applyFilter(previewBitmap, weight), f , favorites.contains(f.getName())));
            //TODO find all liked filters from shared preferences
        }

        recyclerView = (RecyclerView) findViewById(R.id.preview_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PreviewAdapter(this, previews, onItemSelectListener );
        recyclerView.setAdapter(adapter);

//        //old recycler
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.preview_recycler);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//
//        PreviewAdapter adapter = new PreviewAdapter(this, previews, onItemSelectListener );
//        recyclerView.setAdapter(adapter);

        //old onItemSelectListener
        /*recyclerView.  setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserData selectedUser = (UserData) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "You select " + selectedUser.getName(),
                        Toast.LENGTH_SHORT).show();
            }
        });*/

    }



    // show image without change (and hide)
    public void previewClick(View view) {

        if (editedBitmap != null) {
            if (displayOriginal) {
                imageView.setImageBitmap(editedBitmap);
                previewButton.setImageResource(R.drawable.preview_button_dark);
                displayOriginal = false;
            } else {
                imageView.setImageBitmap(scaledOriginalBitmap);//currentSession.currentBitmap);
                previewButton.setImageResource(R.drawable.preview_button_light);
                displayOriginal = true;
            }
        }
    }

    // hide action bar
    public void resizeClick(View view) {
//        TransitionSet transitionSet = new TransitionSet();
//        transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
//        transitionSet.addTransition(new AutoTransition());
//
//        TransitionManager.beginDelayedTransition(workspaceLayout, transitionSet);
        if (barToHide.getVisibility() == View.VISIBLE) {
            barToHide.setVisibility(View.GONE);
            resizeButton.setImageResource(R.drawable.resize_big);
        } else {
            barToHide.setVisibility(View.VISIBLE);
            resizeButton.setImageResource(R.drawable.resize_small);
        }
    }

    @Override
    public void onBackPressed() {
        if (barToHide.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
            //Intent intent = new Intent(this, EditImageActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //startActivity(intent);
        } else {
            resizeClick(resizeButton);
        }
    }

    public void returnBack(View view) {
        onBackPressed();
    }

    public void saveResult(View view) {
        if (editedBitmap != null) {
            currentSession.currentBitmap = editedBitmap; //TODO return updated original image
//            currentSession.currentBitmap = selectedFilter.getFilter().applyFilter(
//                    currentSession.currentBitmap,
//                    seekBar.getProgress());
        }
        onBackPressed();
    }

    private View.OnClickListener onItemSelectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            seekBar.setVisibility(View.VISIBLE);
            likeButton.setVisibility(View.VISIBLE);

            selectedFilter = (PreviewData) v.getTag();
            Log.d("LOG from filter", selectedFilter.getFilter().getName());

            if (selectedFilter.getFilter().hasWeight()) {
                seekBar.setVisibility(View.VISIBLE);
            } else {
                seekBar.setVisibility(View.GONE);
            }

            if (seekBar.getProgress() == 50) {
                applyFilter();
            } else {
                seekBar.setProgress(50);
            }

            if (selectedFilter.getIsLiked()) {
                likeButton.setImageResource(R.drawable.like_enabled);
                displayLiked = true;
            } else {
                likeButton.setImageResource(R.drawable.like_disabled);
                displayLiked = false;
            }
            //apply actual filter
        }
    };


    private void applyFilter() {
        editedBitmap = selectedFilter.getFilter().applyFilter(
                scaledOriginalBitmap, //currentSession.currentBitmap,
                seekBar.getProgress());
        if (displayOriginal) {
            previewClick(null);
        } else {
            imageView.setImageBitmap(editedBitmap);
        }
    }

    public void likeClick(View view) {
        if (!displayLiked) {
            likeButton.setImageResource(R.drawable.like_enabled);
            displayLiked = true;

            new PreferencesHelper(this).add(selectedFilter.getFilter().getName());
        } else {
            likeButton.setImageResource(R.drawable.like_disabled);
            displayLiked = false;

            new PreferencesHelper(this).remove(selectedFilter.getFilter().getName());
        }

        int newPos = adapter.changeFavorite(selectedFilter.getFilter().getName(), displayLiked);
        recyclerView.scrollToPosition(newPos);
    }
}
