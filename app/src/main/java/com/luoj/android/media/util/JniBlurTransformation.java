package com.luoj.android.media.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.blur.android.JNIBlur;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.tosee.android.AppConfig;

/**
 * Created by 京 on 2016/9/1.
 */
public class JniBlurTransformation implements Transformation<Bitmap>{

    Context context;

    public JniBlurTransformation(Context context) {
        this.context = context;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        return BitmapResource.obtain(JNIBlur.doBlur(resource.get(), AppConfig.BLUR_RADIUS), Glide.get(context).getBitmapPool());
    }

    @Override
    public String getId() {
        return "JniBlurTransformation";
    }

}
