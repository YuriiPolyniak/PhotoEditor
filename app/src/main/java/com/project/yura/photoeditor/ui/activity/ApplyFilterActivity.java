package com.project.yura.photoeditor.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.project.yura.photoeditor.R;
import com.project.yura.photoeditor.event.ui.DeleteFilterEvent;
import com.project.yura.photoeditor.event.ui.FilterSelectedEvent;
import com.project.yura.photoeditor.manager.CurrentSession;
import com.project.yura.photoeditor.manager.PreferencesHelper;
import com.project.yura.photoeditor.processing.CustomFilters;
import com.project.yura.photoeditor.processing.IFilter;
import com.project.yura.photoeditor.processing.model.PreviewData;
import com.project.yura.photoeditor.ui.adapter.PreviewAdapter;
import com.project.yura.photoeditor.utils.Helper;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

public class ApplyFilterActivity extends BaseActivity {

    @BindView(R.id.imageToEdit)
    ImageView imageView;
    @BindView(R.id.preview_button)
    ImageView previewButton;
    @BindView(R.id.resize_button)
    ImageView resizeButton;
    @BindView(R.id.like_button)
    ImageView likeButton;
    @BindView(R.id.layout_to_hide)
    ViewGroup barToHide;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private List<PreviewData> previews;
    private PreviewData selectedFilter;
    private Bitmap editedBitmap = null;
    private Bitmap scaledOriginalBitmap = null;
    private Bitmap realEditedBitmap = null;
    private CurrentSession currentSession;

    private PreviewAdapter adapter;
    private RecyclerView recyclerView;
    //private LinearLayout workspaceLayout;

    //part for onResume
    private CustomFilters customFilters;
    private IFilter[] filters;
    private Bitmap previewBitmap;

    private boolean displayOriginal;
    private boolean displayLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentSession = CurrentSession.GetInstance();

        scaledOriginalBitmap = currentSession.currentBitmap;/*Helper.ResizeBitmap(
                currentSession.currentBitmap, displayMetrics.widthPixels, displayMetrics.heightPixels, false);*/
        editedBitmap = scaledOriginalBitmap; //currentSession.currentBitmap;

        imageView.setImageBitmap(scaledOriginalBitmap); //currentSession.currentBitmap

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

        customFilters = new CustomFilters(this);

        //create previews

        previewBitmap = Helper.ResizeBitmap(editedBitmap, 2 * 72, 2 * 80, true);

        int weight = 50;
        previews = new ArrayList<>();
        filters = customFilters.getFilters();
        //IFilter[] userFilters = customFilters.getCustomFilters();

        Set<String> favorites = PreferencesHelper.getInstance().getFavoriteFilters();
        for (IFilter f : filters) {
            previews.add(new PreviewData(
                    f.applyFilter(previewBitmap, weight), f, favorites.contains(f.getName()), false));

        }

        recyclerView = (RecyclerView) findViewById(R.id.preview_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_apply_filter;
    }

    @Override
    protected void onResume() {
        super.onResume();

        int weight = 50;
        IFilter[] userFilters = customFilters.getCustomFilters();
        List<PreviewData> ufPreviews = new ArrayList<>();
        //Set<String> favorites = PreferencesHelper.getInstance().getFavoriteFilters();

        for (IFilter f : userFilters) {
            ufPreviews.add(new PreviewData(
                    f.applyFilter(previewBitmap, weight), f, false, true));
        }

        List<PreviewData> adapterData = new ArrayList<>(previews);
        adapterData.addAll(ufPreviews);

        adapter = new PreviewAdapter(this, adapterData, true);
        recyclerView.setAdapter(adapter);
    }

    // show image without change (and hide)
    @OnClick(R.id.preview_button)
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
    @OnClick(R.id.resize_button)
    void resizeClick(View view) {
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
            resizeClick(resizeButton);
        }
    }

    private void startImageProcessing() {
        seekBar.setEnabled(false);
        recyclerView.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void stopImageProcessing() {
        seekBar.setEnabled(true);
        recyclerView.setEnabled(true);
        mProgressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.cancel_button)
    public void returnBack(View view) {
        onBackPressed();
    }

    @OnClick(R.id.ok_button)
    public void saveResult(View view) {
        if (editedBitmap != null) {
            currentSession.currentBitmap = editedBitmap;
        }
        onBackPressed();
    }

    @Subscribe
    public void onFilterSelected(FilterSelectedEvent event) {
        selectedFilter = event.getData();

        seekBar.setVisibility(View.VISIBLE);
        likeButton.setVisibility(View.VISIBLE);

        String filterName = selectedFilter.getFilter().getName();
        Log.d("LOG from filter", filterName);

        if (!filterName.equals(PreviewAdapter.NEW_FILTER)) {
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

            //TODO decide what to do with like on your own filters
            //cant like custom filters, they terrible
                /*if (selectedFilter.getIsCustom()) {
                    likeButton.setVisibility(View.GONE);
                }*/

            if (selectedFilter.getIsLiked()) {
                likeButton.setImageResource(R.drawable.like_enabled);
                displayLiked = true;
            } else {
                likeButton.setImageResource(R.drawable.like_disabled);
                displayLiked = false;
            }
        } else { //open create new filter activity
            seekBar.setVisibility(View.GONE);
            likeButton.setVisibility(View.GONE);
            editedBitmap = scaledOriginalBitmap;
            imageView.setImageBitmap(editedBitmap);
            startActivity(new Intent(getApplicationContext(), NewFilterActivity.class));
        }
        //apply actual filter
    }

    void applyFilter() {
        startImageProcessing();
        final int progress = seekBar.getProgress();
        if (displayOriginal) {
            previewClick(null);
        } /*else {
            imageView.setImageBitmap(editedBitmap);
        }*/

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                editedBitmap = selectedFilter.getFilter().applyFilter(
                        scaledOriginalBitmap, //currentSession.currentBitmap,
                        progress);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                imageView.setImageBitmap(editedBitmap);
                stopImageProcessing();
            }
        };
        task.execute();
    }

    @OnClick(R.id.like_button)
    public void likeClick(View view) {
        if (!displayLiked) {
            likeButton.setImageResource(R.drawable.like_enabled);
            displayLiked = true;

            PreferencesHelper.getInstance().addFavoriteFilter(selectedFilter.getFilter().getName());
        } else {
            likeButton.setImageResource(R.drawable.like_disabled);
            displayLiked = false;

            PreferencesHelper.getInstance().removeFavoriteFilter(selectedFilter.getFilter().getName());
        }

        //int newPos = adapter.changeFavorite(selectedFilter.getFilter().getName(), displayLiked);
        int newPos = adapter.changeFavorite(selectedFilter, displayLiked);
        recyclerView.scrollToPosition(newPos);
    }

    @Subscribe
    public void onDeleteFilter(DeleteFilterEvent event) {
        final PreviewData filter = event.getData();
        final String filterName = filter.getFilter().getName();

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Delete filter?")
                .setMessage("Do you really want to delete the \"" + filterName + "\" filter?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferencesHelper.getInstance()
                                .removeCustomFilter(filterName);
                        adapter.removeFilter(filter);
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.darkOrange));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.colorText));
            }
        });
        dialog.show();
    }
}
