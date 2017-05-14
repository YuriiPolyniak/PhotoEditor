package com.project.yura.photoeditor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.SeekBar;

import com.project.yura.photoeditor.Model.CurrentSession;
import com.project.yura.photoeditor.Model.CustomFilters;
import com.project.yura.photoeditor.Model.Helper;
import com.project.yura.photoeditor.Model.IFilter;
import com.project.yura.photoeditor.Model.PreferencesHelper;
import com.project.yura.photoeditor.Model.PreviewData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ApplyFilterActivity extends AppCompatActivity
    implements PreviewAdapter.IUpdateFilter{
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

        customFilters = new CustomFilters(this);

        //create previews

        previewBitmap = Helper.ResizeBitmap(editedBitmap, 2*72, 2*80, true);

        int weight = 50;
        previews = new ArrayList<>();
        filters = customFilters.getFilters();
        //IFilter[] userFilters = customFilters.getCustomFilters();

        Set<String> favorites = new PreferencesHelper(this).getFavoriteFilters();
        for (IFilter f : filters) {
            previews.add(new PreviewData(
                    f.applyFilter(previewBitmap, weight), f , favorites.contains(f.getName()), false));

        }

        recyclerView = (RecyclerView) findViewById(R.id.preview_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


        /*
        //moved to onResume
        adapter = new PreviewAdapter(this, previews, onItemSelectListener, true);
        recyclerView.setAdapter(adapter);
*/

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

    @Override
    protected void onResume() {
        super.onResume();

        int weight = 50;
        IFilter[] userFilters = customFilters.getCustomFilters();
        List<PreviewData> ufPreviews = new ArrayList<>();
        //Set<String> favorites = new PreferencesHelper(this).getFavoriteFilters();

        for (IFilter f : userFilters) {
            ufPreviews.add(new PreviewData(
                    f.applyFilter(previewBitmap, weight), f , false, true));
        }

        List<PreviewData> adapterData = new ArrayList<>(previews);
        adapterData.addAll(ufPreviews);

        //adapter.updateFiltersList(ufPreviews);
        adapter = new PreviewAdapter(this, adapterData, onItemSelectListener, true);
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
            currentSession.currentBitmap = editedBitmap; //TODO return updated original image (don't think this is important)
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
            likeButton.setVisibility(View.VISIBLE);

            selectedFilter = (PreviewData) v.getTag();
            String filterName = selectedFilter.getFilter().getName();
            Log.d("LOG from filter", filterName);

            if (!filterName.equals(PreviewAdapter.NEW_FILTER)){
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
                if (selectedFilter.getIsCustom()) {
                    likeButton.setVisibility(View.GONE);
                }

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
    };


    void applyFilter() {
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

            new PreferencesHelper(this).addFavoriteFilter(selectedFilter.getFilter().getName());
        } else {
            likeButton.setImageResource(R.drawable.like_disabled);
            displayLiked = false;

            new PreferencesHelper(this).removeFavoriteFilter(selectedFilter.getFilter().getName());
        }

        //int newPos = adapter.changeFavorite(selectedFilter.getFilter().getName(), displayLiked);
        int newPos = adapter.changeFavorite(selectedFilter, displayLiked);
        recyclerView.scrollToPosition(newPos);
    }

    @Override
    public void deleteFilter(final PreviewData filter) {
        final String filterName = filter.getFilter().getName();
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Delete filter?")
                .setMessage("Do you really want to delete the \"" + filterName + "\" filter?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new PreferencesHelper(getApplicationContext())
                                .removeCustomFilter(filterName);
                        adapter.removeFilter(filter);

                        //imageView.setImageBitmap(editedBitmap);
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
