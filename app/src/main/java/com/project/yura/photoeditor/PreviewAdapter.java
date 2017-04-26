package com.project.yura.photoeditor;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.yura.photoeditor.Model.CustomFilters;
import com.project.yura.photoeditor.Model.PreviewData;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.CustomViewHolder> {
    private LayoutInflater mInflater;
    private List<PreviewData> previewDataList;
    private Context mContext;
    Drawable foreground;
    View.OnClickListener externalListener = null;
    int selectedItemId = -1;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageView foregroundImageView;
        public ImageView likeImageView;
        public TextView nameView;

        public CustomViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.preview_image);
            nameView = (TextView) view.findViewById(R.id.preview_title);
            foregroundImageView = (ImageView) view.findViewById(R.id.foreground_image);
            likeImageView = (ImageView) view.findViewById(R.id.like_image);
        }
    }

    public PreviewAdapter(Context context, List<PreviewData> previewDataList, View.OnClickListener listener) {
        sortPreviewData(previewDataList);

        this.previewDataList = previewDataList;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        externalListener = listener;

        //foreground = new ColorDrawable(0x7F14CFE5);
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.preview_item, null);
        View view = mInflater.inflate(R.layout.preview_item, viewGroup, false);
        view.setOnClickListener(customOnItemClickListener);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    final private View.OnClickListener customOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //save id of selected filter
            PreviewData selectedFilter = (PreviewData) v.getTag();
            int id = previewDataList.indexOf(selectedFilter);
            selectedItemId = id;

            //refresh view
            notifyDataSetChanged();

            String title = selectedFilter.getFilter().getName();
            Log.d("FROM Adapter", title);

            if (externalListener != null)
                externalListener.onClick(v);

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

        if (i == selectedItemId) {
            customViewHolder.nameView.setBackgroundResource(R.color.selectedItem);
            customViewHolder.foregroundImageView.setVisibility(View.VISIBLE);
        } else {
            customViewHolder.nameView.setBackgroundResource(R.color.lightOrange);
            customViewHolder.foregroundImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (null != previewDataList ? previewDataList.size() : 0);
    }

    public int changeFavorite(String name, boolean mode) {
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
        PreviewData data = null;
        for (int i = 0; i < previewDataList.size(); i++) {
            data = previewDataList.get(i);
            if (data.getFilter().getName().equals(name)) {
                data.setIsLiked(mode);
//                previewDataList.remove(i);

                break;
            }
        }
//        previewDataList.add(0, data);

        sortPreviewData(previewDataList);

        for (int i = 0; i < previewDataList.size(); i++) {
            data = previewDataList.get(i);
            if (data.getFilter().getName().equals(name)) {
                selectedItemId = i;

                break;
            }
        }
        //selectedItemId = 0;
        notifyDataSetChanged();
        return selectedItemId;
    }

    private void sortPreviewData(List<PreviewData> data) {
        Collections.sort( data, new Comparator<PreviewData>() {
            @Override
            public int compare(PreviewData o1, PreviewData o2) {
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
}
