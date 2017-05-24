package com.project.yura.photoeditor;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.project.yura.photoeditor.Model.CurrentSession;
import com.project.yura.photoeditor.Model.CustomFrameFilters;
import com.project.yura.photoeditor.Model.Helper;
import com.project.yura.photoeditor.Model.IFilter;
import com.project.yura.photoeditor.Model.PreviewData;

import java.util.ArrayList;
import java.util.List;

public class ApplyFrameActivity    extends AppCompatActivity {

    private List<PreviewData> previews;
    private PreviewData selectedFilter;
    private Bitmap editedBitmap = null;
    private Bitmap scaledOriginalBitmap = null;
    private Bitmap realEditedBitmap = null;
    private CurrentSession currentSession;
    private CustomFrameFilters customFilters;
    private ImageView imageView;
    private ImageView previewButton;
    private ImageView resizeButton;
    private SeekBar seekBar;
    private ViewGroup barToHide;

    private boolean displayOriginal;
    private LinearLayout workspaceLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_frame);

        currentSession = CurrentSession.GetInstance();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        scaledOriginalBitmap = currentSession.currentBitmap; /*Helper.ResizeBitmap(
                currentSession.currentBitmap, displayMetrics.widthPixels, displayMetrics.heightPixels, false);*/
        editedBitmap = scaledOriginalBitmap; //currentSession.currentBitmap;
        imageView = (ImageView)findViewById(R.id.imageToEdit);
        imageView.setImageBitmap(scaledOriginalBitmap); //currentSession.currentBitmap
        previewButton = (ImageView)findViewById(R.id.preview_button);
        resizeButton = (ImageView)findViewById(R.id.resize_button);
        barToHide = (ViewGroup)findViewById(R.id.layout_to_hide);
        workspaceLayout = (LinearLayout) findViewById(R.id.activity_apply_frame);
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

        customFilters = new CustomFrameFilters(this);


        Bitmap previewBitmap = Helper.ResizeBitmap(editedBitmap, 2*72, 2*80, true);

        int weight = 50;
        previews = new ArrayList<>();
        IFilter[] filters = customFilters.getFilters();
        for (IFilter f : filters) {
            previews.add(new PreviewData(
                    f.applyFilter(previewBitmap, weight), f , false, false));
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.preview_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        PreviewAdapter adapter = new PreviewAdapter(this, previews, onItemSelectListener, false);
        recyclerView.setAdapter(adapter);

    }


    // show image without change (and hide)
    void previewClick(View view) {
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
    void resizeClick(View view) {
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
        } else {
            resizeClick(resizeButton);
        }
    }

    public void returnBack(View view) {
        onBackPressed();
    }

    public void saveResult(View view) {
        if (editedBitmap != null) {
            currentSession.currentBitmap = editedBitmap;
//            currentSession.currentBitmap = selectedFilter.getFilter().applyFilter(
//                    currentSession.currentBitmap,
//                    seekBar.getProgress());
        }
        onBackPressed();
    }

    View.OnClickListener onItemSelectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            seekBar.setVisibility(View.VISIBLE);

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
        }
    };


    void applyFilter() {
        editedBitmap = selectedFilter.getFilter().applyFilter(
                scaledOriginalBitmap,
                seekBar.getProgress());
        if (displayOriginal) {
            previewClick(null);
        } else {
            imageView.setImageBitmap(editedBitmap);
        }
    }
}
