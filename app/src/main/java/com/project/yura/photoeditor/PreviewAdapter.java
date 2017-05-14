package com.project.yura.photoeditor;


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

import com.project.yura.photoeditor.Model.IFilter;
import com.project.yura.photoeditor.Model.PreviewData;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.CustomViewHolder> {
    private final LayoutInflater mInflater;
    private final List<PreviewData> previewDataList;
    private final Context mContext;
    Drawable foreground;
    private View.OnClickListener externalListener = null;
   // private View.OnLongClickListener externalLongListener = null;
    private int selectedItemId = -1;
    private PreviewData newFilterPreviewData;

    static final String NEW_FILTER = "NEW FILTER";

    class CustomViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final ImageView foregroundImageView;
        final ImageView likeImageView;
        final ImageView newImageView;
        final TextView nameView;

        CustomViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.preview_image);
            nameView = (TextView) view.findViewById(R.id.preview_title);
            foregroundImageView = (ImageView) view.findViewById(R.id.foreground_image);
            likeImageView = (ImageView) view.findViewById(R.id.like_image);
            newImageView = (ImageView) view.findViewById(R.id.new_image);
        }
    }

    public PreviewAdapter(Context context, List<PreviewData> previewDataList, View.OnClickListener listener,
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
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        externalListener = listener;
       // externalLongListener = longListener;
        //foreground = new ColorDrawable(0x7F14CFE5);
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.preview_item, null);
        View view = mInflater.inflate(R.layout.preview_item, viewGroup, false);
        view.setOnClickListener(customOnItemClickListener);
        view.setOnLongClickListener(customOnItemLongClickListener);
        return new CustomViewHolder(view);
    }

    final private View.OnClickListener customOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //save id of selected filter
            PreviewData selectedFilter = (PreviewData) v.getTag();
            selectedItemId = previewDataList.indexOf(selectedFilter);

            //refresh view
            notifyDataSetChanged();

            String title = selectedFilter.getFilter().getName();
            Log.d("FROM Adapter", title);

            if (externalListener != null)
                externalListener.onClick(v);

        }
    };

    final private View.OnLongClickListener customOnItemLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            //save id of selected filter
            PreviewData selectedFilter = (PreviewData) v.getTag();
            int currentIndex = previewDataList.indexOf(selectedFilter);
            if (currentIndex != selectedItemId) {
                v.performClick();
            }
            //refresh view
            //notifyDataSetChanged();

            //String title = selectedFilter.getFilter().getName();

            if (selectedFilter.getIsCustom()) {
                //externalLongListener.onLongClick(v);
                if (mContext instanceof IUpdateFilter) {
                    ((IUpdateFilter) mContext).deleteFilter(selectedFilter);
                }
            }

            //notifyDataSetChanged();

            return true;
        }
    };

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        PreviewData previewItem = previewDataList.get(i);

        customViewHolder.itemView.setTag(previewItem);

        customViewHolder.imageView.setImageBitmap(previewItem.getBitmap());
        customViewHolder.nameView.setText(previewItem.getFilter().getName());

        if (previewItem.getIsLiked()) {
            customViewHolder.likeImageView.setVisibility(View.VISIBLE);
        } else {
            customViewHolder.likeImageView.setVisibility(View.INVISIBLE);
        }

        if (previewItem.getIsCustom()) {
            customViewHolder.newImageView.setVisibility(View.VISIBLE);
        } else {
            customViewHolder.newImageView.setVisibility(View.INVISIBLE);
        }

        if (i == selectedItemId) {
            customViewHolder.nameView.setBackgroundResource(R.color.selectedItem);
//            if (previewItem.getFilter().getName().equals(NEW_FILTER)) {
            if (previewItem.equals(newFilterPreviewData)) {
                customViewHolder.foregroundImageView.setVisibility(View.INVISIBLE);
            } else {
                customViewHolder.foregroundImageView.setVisibility(View.VISIBLE);
            }
        } else {
            customViewHolder.nameView.setBackgroundResource(R.color.lightOrange);
            customViewHolder.foregroundImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (null != previewDataList ? previewDataList.size() : 0);
    }

    public int changeFavorite(PreviewData filter, boolean mode) {
//        public int changeFavorite(String name, boolean mode) {
//        PreviewData data = null;
//        for (int i = 0; i < previewDataList.size(); i++) {
//            data = previewDataList.get(i);
//            if (data.getFilter().getName().equals(name)) {
//                data.setIsLiked(mode);
//                previewDataList.remove(i);
//                break;
//            }
//        }
//        previewDataList.add(0, data);

        filter.setIsLiked(mode);

        /*PreviewData data = null;
        for (int i = 0; i < previewDataList.size(); i++) {
            data = previewDataList.get(i);
            if (data.getFilter().getName().equals(name)) {
                data.setIsLiked(mode);
//                previewDataList.remove(i);

                break;
            }
        }*/

        //PreviewData selectedFilter = filter;//previewDataList.get(selectedItemId);

        sortPreviewData(previewDataList);

        selectedItemId = previewDataList.indexOf(filter);
        /*for (int i = 0; i < previewDataList.size(); i++) {
            data = previewDataList.get(i);
            if (data.getFilter().getName().equals(name)) {
                selectedItemId = i;

                break;
            }
        }*/

        notifyDataSetChanged();
        return selectedItemId;
    }

    public void removeFilter(PreviewData filter) {
        previewDataList.remove(filter);
        //selectedItemId = -1;
        View view = new View(mContext);
        view.setTag(previewDataList.get(selectedItemId));
        if (externalListener != null)
            externalListener.onClick(view);
        notifyDataSetChanged();
    }

    private void sortPreviewData(List<PreviewData> data) {
        Collections.sort( data, new Comparator<PreviewData>() {
            @Override
            public int compare(PreviewData o1, PreviewData o2) {
                /*if (o1.getFilter().getName().equals(NEW_FILTER)) {
                    return -1;
                }
                if (o2.getFilter().getName().equals(NEW_FILTER)) {
                    return 1;
                }*/

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
            }
        });
    }

    public interface IUpdateFilter {
        void deleteFilter(PreviewData data);
    }
}
