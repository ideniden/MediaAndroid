package com.luoj.android.media.util;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapperTransformation;

/**
 * Created by 京 on 2016/9/1.
 */
public class GifBlurTransformation extends GifBitmapWrapperTransformation{

    public GifBlurTransformation(Context context) {
        super(Glide.get(context).getBitmapPool(), new JniBlurTransformation(context));
    }

}
