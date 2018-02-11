package com.project.yura.photoeditor.ui.activity;

import android.graphics.Bitmap;
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

import com.bumptech.glide.Glide;
import com.project.yura.photoeditor.R;
import com.project.yura.photoeditor.event.ui.FilterSelectedEvent;
import com.project.yura.photoeditor.manager.CurrentSession;
import com.project.yura.photoeditor.processing.CustomFrameFilters;
import com.project.yura.photoeditor.processing.IFilter;
import com.project.yura.photoeditor.processing.model.PreviewData;
import com.project.yura.photoeditor.ui.adapter.PreviewAdapter;
import com.project.yura.photoeditor.utils.Helper;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ApplyFrameActivity extends BaseActivity {

    @BindView(R.id.imageToEdit)
    ImageView imageView;
    @BindView(R.id.preview_button)
    ImageView previewButton;
    @BindView(R.id.resize_button)
    ImageView resizeButton;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindView(R.id.layout_to_hide)
    ViewGroup barToHide;
    @BindView(R.id.preview_recycler)
    RecyclerView previewRecycler;

    private List<PreviewData> previews;
    private PreviewData selectedFilter;
    private Bitmap editedBitmap = null;
    private Bitmap scaledOriginalBitmap = null;
    private CurrentSession currentSession;
    private CustomFrameFilters customFilters;

    private boolean displayOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentSession = CurrentSession.GetInstance();

        scaledOriginalBitmap = currentSession.currentBitmap;
        editedBitmap = scaledOriginalBitmap;

        customFilters = new CustomFrameFilters(this);

        initPreviews();

        initUI();
    }

    private void initPreviews() {
        Bitmap previewBitmap = Helper.ResizeBitmap(editedBitmap, 2 * 72, 2 * 80, true);

        int weight = 50;
        previews = new ArrayList<>();
        IFilter[] filters = customFilters.getFilters();
        for (IFilter f : filters) {
            previews.add(new PreviewData(
                    f.applyFilter(previewBitmap, weight), f, false, false));
        }
    }

    private void initUI() {
        loadImage(scaledOriginalBitmap);

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

        previewRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        previewRecycler.setLayoutManager(layoutManager);

        PreviewAdapter adapter = new PreviewAdapter(this, previews, false);
        previewRecycler.setAdapter(adapter);
    }

    private void loadImage(Bitmap bitmap) {
        Glide.with(this)
                .load(bitmap)
                .into(imageView);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_apply_frame;
    }

    // show image without change (and hide)
    @OnClick(R.id.preview_button)
    void previewClick(View view) {
        if (editedBitmap != null) {
            if (displayOriginal) {
                loadImage(editedBitmap);
                previewButton.setImageResource(R.drawable.preview_button_dark);
                displayOriginal = false;
            } else {
                loadImage(scaledOriginalBitmap);
                previewButton.setImageResource(R.drawable.preview_button_light);
                displayOriginal = true;
            }
        }
    }

    // hide action bar
    @OnClick(R.id.resize_button)
    void resizeClick() {
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
            overridePendingTransition(R.anim.slide_down_to, R.anim.slide_down_from);
        } else {
            resizeClick();
        }
    }

    @OnClick(R.id.cancel_button)
    public void returnBack() {
        onBackPressed();
    }

    @OnClick(R.id.ok_button)
    public void saveResult() {
        if (editedBitmap != null) {
            currentSession.currentBitmap = editedBitmap;
        }
        onBackPressed();
    }

    @Subscribe
    public void onFilterSelected(FilterSelectedEvent event) {
        selectedFilter = event.getData();

        seekBar.setVisibility(View.VISIBLE);

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

    void applyFilter() {
        editedBitmap = selectedFilter.getFilter().applyFilter(
                scaledOriginalBitmap,
                seekBar.getProgress());
        if (displayOriginal) {
            previewClick(null);
        } else {
            loadImage(editedBitmap);
        }
    }
}
