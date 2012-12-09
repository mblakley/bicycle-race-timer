package com.xcracetiming.android.tttimer.Utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.xcracetiming.android.tttimer.R;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;

    public static Integer[] ImageIds = {
//            R.drawable.sample_1,
//            R.drawable.sample_2,
//            R.drawable.sample_3,
//            R.drawable.sample_4,
//            R.drawable.sample_5,
//            R.drawable.sample_6,
//            R.drawable.sample_7
    };

    public ImageAdapter(Context c) {
        mContext = c;
        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.MarshalLocationsGallery);
        mGalleryItemBackground = attr.getResourceId(
                R.styleable.MarshalLocationsGallery_android_galleryItemBackground, 0);
        attr.recycle();
    }

    public int getCount() {
        return ImageIds.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);

        imageView.setImageResource(ImageIds[position]);
        imageView.setLayoutParams(new Gallery.LayoutParams(150, 100));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundResource(mGalleryItemBackground);

        return imageView;
    }
}
