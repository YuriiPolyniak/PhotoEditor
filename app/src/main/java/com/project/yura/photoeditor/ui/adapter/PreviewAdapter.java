package com.project.yura.photoeditor.ui.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.yura.photoeditor.R;
import com.project.yura.photoeditor.event.MainThreadBus;
import com.project.yura.photoeditor.event.ui.DeleteFilterEvent;
import com.project.yura.photoeditor.event.ui.FilterSelectedEvent;
import com.project.yura.photoeditor.processing.IFilter;
import com.project.yura.photoeditor.processing.model.PreviewData;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.CustomViewHolder> {
    public static final String NEW_FILTER = "NEW FILTER";

    private final List<PreviewData> previewDataList;

    private int selectedItemId = -1;

    private PreviewData newFilterPreviewData;

    public PreviewAdapter(Context context, List<PreviewData> previewDataList,
                          boolean haveNewOption) {
        if (haveNewOption) {
            newFilterPreviewData = new PreviewData(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.new_filter),
                    new IFilter() {
                        @Override
                        public String getName() {
                            return NEW_FILTER;
                        }

                        @Override
                        public Bitmap applyFilter(Bitmap image, int weight) {
                            return null;
                        }

                        @Override
                        public boolean hasWeight() {
                            return false;
                        }
                    },
                    false,
                    false
            );
            previewDataList.add(0, newFilterPreviewData);
        }

        sortPreviewData(previewDataList);

        this.previewDataList = previewDataList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.preview_item, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        customViewHolder.init(previewDataList.get(i));
    }

    @Override
    public int getItemCount() {
        return (previewDataList != null ? previewDataList.size() : 0);
    }

    public int changeFavorite(PreviewData filter, boolean mode) {
        filter.setIsLiked(mode);

        sortPreviewData(previewDataList);

        selectedItemId = previewDataList.indexOf(filter);

        notifyDataSetChanged();
        return selectedItemId;
    }

    public void removeFilter(PreviewData filter) {
        previewDataList.remove(filter);

        MainThreadBus.getInstance().post(new FilterSelectedEvent(previewDataList.get(selectedItemId)));

        notifyDataSetChanged();
    }

    private void sortPreviewData(List<PreviewData> data) {
        Collections.sort(data, (o1, o2) -> {
            //preview for new filter first
            if (o1.equals(newFilterPreviewData)) {
                return -1;
            }
            if (o2.equals(newFilterPreviewData)) {
                return 1;
            }

            //then custom filters
            if (o1.getIsCustom() && !o2.getIsCustom()) {
                return -1;
            }
            if (!o1.getIsCustom() && o2.getIsCustom()) {
                return 1;
            }

            if (o1.getIsLiked() == o2.getIsLiked()) {
                return o1.getFilter().getName().compareTo(o2.getFilter().getName());
            }
            if (o1.getIsLiked()) {
                return -1;
            } else {
                return 1;
            }
        });
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.preview_image)
        ImageView imageView;
        @BindView(R.id.foreground_image)
        ImageView foregroundImageView;
        @BindView(R.id.like_image)
        ImageView likeImageView;
        @BindView(R.id.new_image)
        ImageView newImageView;
        @BindView(R.id.preview_title)
        TextView nameView;

        CustomViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }

        public void init(PreviewData item) {

            imageView.setImageBitmap(item.getBitmap());
            nameView.setText(item.getFilter().getName());

            if (item.getIsLiked()) {
                likeImageView.setVisibility(View.VISIBLE);
            } else {
                likeImageView.setVisibility(View.INVISIBLE);
            }

            if (item.getIsCustom()) {
                newImageView.setVisibility(View.VISIBLE);
            } else {
                newImageView.setVisibility(View.INVISIBLE);
            }

            if (previewDataList.indexOf(item) == selectedItemId) {
                nameView.setBackgroundResource(R.color.selectedItem);
//            if (previewItem.getFilter().getName().equals(NEW_FILTER)) {
                if (item.equals(newFilterPreviewData)) {
                    foregroundImageView.setVisibility(View.INVISIBLE);
                } else {
                    foregroundImageView.setVisibility(View.VISIBLE);
                }
            } else {
                nameView.setBackgroundResource(R.color.lightOrange);
                foregroundImageView.setVisibility(View.INVISIBLE);
            }

            itemView.setOnClickListener(v -> {
                //save id of selected filter
                selectedItemId = previewDataList.indexOf(item);

                //refresh view
                notifyDataSetChanged();

                String title = item.getFilter().getName();
                Log.d("FROM Adapter", title);

                MainThreadBus.getInstance().post(new FilterSelectedEvent(item));
            });

            itemView.setOnLongClickListener(v -> {
                //save id of selected filter
                int currentIndex = previewDataList.indexOf(item);
                if (currentIndex != selectedItemId) {
                    v.performClick();
                }
                //refresh view

                if (item.getIsCustom()) {
                    MainThreadBus.getInstance().post(new DeleteFilterEvent(item));
                }

                //notifyDataSetChanged();

                return true;
            });
        }
    }
}
