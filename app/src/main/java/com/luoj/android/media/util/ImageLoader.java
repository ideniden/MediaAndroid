package com.luoj.android.media.util;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.tosee.android.R;

/**
 * Created by 京 on 2016/7/19.
 */
public class ImageLoader {

    private static ImageLoader mImageLoader;

    public static ImageLoader init(Application appContext){
        if(null==mImageLoader){
            mImageLoader=new ImageLoader(appContext);
        }
        return mImageLoader;
    }

    public static BitmapUtils imageLoader;

    private ImageLoader(Application appContext) {
        imageLoader = new BitmapUtils(appContext);
//        imageLoader.configDefaultLoadingImage(R.drawable.default_profile_image_m);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        imageLoader.configDefaultImageLoadAnimation(alphaAnimation);
        imageLoader.configDefaultAutoRotation(true);
        imageLoader.configDefaultLoadFailedImage(R.drawable.default_profile_image_m);
    }

    public static void display(View v,String url){
        imageLoader.display(v,url);
    }

    public static void display(View v,String url,ImageLoadCallback callback){
        imageLoader.display(v, url, callback);
    }

    public static void displayInAdapter(View v,String url){
        displayInAdapter(v,url,null);
    }

    /**
     * 解决列表中重复加载图片的问题。
     * @param v
     * @param url
     * @param callback
     */
    public static void displayInAdapter(View v,String url,ImageLoadCallback callback){
        String cacheUrl= (String) v.getTag();
        if(!TextUtils.isEmpty(cacheUrl)&&cacheUrl.equals(url)){
            return;
        }
        display(v,url,callback);
        v.setTag(url);
    }

    public static void clearCache(){
        imageLoader.clearCache();
    }

    public static abstract class ImageLoadCallback extends BitmapLoadCallBack<View>{
        @Override
        public void onLoadCompleted(View view, String s, Bitmap bitmap, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {

        }
        @Override
        public void onLoadFailed(View view, String s, Drawable drawable) {

        }
        @Override
        public void onLoading(View container, String uri, BitmapDisplayConfig config, long total, long current) {
            onLoading(config,total,current);
        }
        protected abstract void onLoading(BitmapDisplayConfig config, long total, long current);
    }

}
