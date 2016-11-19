package com.luoj.android.media.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.blur.android.JNIBlur;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.tosee.android.AppConfig;
import com.tosee.android.media.GifBlurTransformation;
import com.tosee.android.util.ImageLoader;
import com.tosee.android.util.MathUtil;
import com.tosee.android.util.ThreadUtil;
import com.tosee.android.util.Util;
import com.tosee.android.view.photoview.PhotoView;
import com.tosee.android.view.photoview.PhotoViewAttacher;

import static com.bumptech.glide.Glide.with;

/**
 * Created by 京 on 2016/9/27.
 * 封装用于Tosee媒体内容展示
 * 包含图片、gif、视频
 */

public class MediaView extends RelativeLayout implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnBufferingUpdateListener{

    public MediaView(Context context) {
        super(context);
        init();
    }

    public MediaView(Context context, int id) {
        super(context);
        this.id=id;
        init();
    }

    public MediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    int id;

    final int DISPLAY_PHOTO=0;
    final int DISPLAY_VIDEO=1;
    final int DISPLAY_VIDEO_PREVIEW=2;
    final int DISPLAY_VIDEO_GIF=3;
    int curDisplay;

    boolean blurDisplay;

    boolean inLoading;
    boolean isLoadCompleted;

    private PhotoView mPhotoView;
    private String mPhotoUrl;

    private VideoView mVideoView;
    private String mVideoUrl;
    private String mVideoPreviewImageUrl;
    private String mVideoGifUrl;

    private void init(){
        mPhotoView=new PhotoView(getContext());
        mPhotoView.setLayoutParams(new LayoutParams(-1,-1));
        addView(mPhotoView);

        mVideoView=new VideoView(getContext());
        mVideoView.setLayoutParams(new LayoutParams(-1,-1));
        addView(mVideoView);
        mVideoView.setOnPreparedListener(this);
    }

    public int getMediaViewId(){
        return this.id;
    }

    public MediaView setPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
        return this;
    }

    public MediaView setPhotoUrl(String mPhotoUrl,ImageView.ScaleType scaleType) {
        this.mPhotoUrl = mPhotoUrl;
        mPhotoView.setScaleType(scaleType);
        return this;
    }

    public void setVideoUrl(String mVideoUrl) {
        this.mVideoUrl = mVideoUrl;
    }

    public void setVideoPreviewImageUrl(String mVideoPreviewImageUrl) {
        this.mVideoPreviewImageUrl = mVideoPreviewImageUrl;
    }

    public MediaView setVideoGifUrl(String mVideoGifUrl) {
        this.mVideoGifUrl = mVideoGifUrl;
        return this;
    }

    public void setPhotoOnClickListener(OnPhotoClickListener listener){
        this.mOnPhotoClickListener=listener;
        mPhotoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if(null!=mOnPhotoClickListener){
                    mOnPhotoClickListener.onClick(MediaView.this,x,y);
                }
            }
            @Override
            public void onOutsidePhotoTap() {

            }
        });
    }

    void setPhotoViewVisible(){
        curDisplay=DISPLAY_PHOTO;
        mVideoView.setVisibility(View.GONE);
        mPhotoView.setVisibility(View.VISIBLE);
    }

    void setVideoViewVisible(){
        curDisplay=DISPLAY_VIDEO;
        mVideoView.setVisibility(View.VISIBLE);
        mPhotoView.setVisibility(View.GONE);
    }

    public void displayPhoto(){
        setPhotoViewVisible();
        blurDisplay=false;
        ImageLoader.display(mPhotoView, mPhotoUrl, imageLoadCallback);
    }

    public void displayPhoto(@DrawableRes int resId){
        setPhotoViewVisible();
        blurDisplay=false;
        mPhotoView.setImageResource(resId);
    }

    public void displayBlurPhoto(){
        setPhotoViewVisible();
        blurDisplay=true;
        if(!inLoading){
            ImageLoader.display(mPhotoView, mPhotoUrl, imageLoadCallback);
        }
    }

    public void displayVideo(){
        setVideoViewVisible();
        blurDisplay=false;
        Util.displayVideo(mVideoView,mVideoUrl);
    }

    public void stopVideo(){
        mVideoView.stopPlayback();
    }

    public void displayVideoGif(){
        setPhotoViewVisible();
        blurDisplay=false;
        with(getContext())
                .load(mVideoGifUrl)
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .into(mPhotoView);
    }

    public void displayBlurVideo(){
        setPhotoViewVisible();
        blurDisplay=true;
        DrawableTypeRequest<String> glide=Glide.with(getContext()).load(mVideoGifUrl);
        glide.asGif();
        glide.transform(new GifBlurTransformation(getContext()));
        glide.diskCacheStrategy(DiskCacheStrategy.SOURCE);
        glide.crossFade();
        glide.into(mPhotoView);
    }

    ImageLoader.ImageLoadCallback imageLoadCallback=new ImageLoader.ImageLoadCallback() {
        @Override
        protected void onLoading(BitmapDisplayConfig config, long total, long current) {
            inLoading=true;
            isLoadCompleted=false;
            if(null!=mMediaLoadingListener){
                mMediaLoadingListener.onLoading(MediaView.this, MathUtil.calculatePercentage(total,current),curDisplay);
            }
        }

        @Override
        public void onLoadCompleted(View view, String s,final Bitmap bitmap, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
            inLoading=false;
            isLoadCompleted=true;
            if(null!=mMediaLoadingListener){
                mMediaLoadingListener.onLoadCompleted(MediaView.this,curDisplay);
            }
            if (blurDisplay) {
                ThreadUtil.syncRun(new ThreadUtil.SyncInterface<Bitmap>() {
                    @Override
                    public Bitmap runInWorkThread() {
                        return JNIBlur.doBlur(bitmap, AppConfig.BLUR_RADIUS);
                    }
                    @Override
                    public void workThreadIsDone(Bitmap result) {
                        mPhotoView.setImageBitmap(result);
                    }
                }).start();
            } else {
                mPhotoView.setImageBitmap(bitmap);
            }
        }
    };

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        if (percent == 100) {
            inLoading = false;
            isLoadCompleted = true;
            if(null!=mMediaLoadingListener){
                mMediaLoadingListener.onLoadCompleted(MediaView.this,curDisplay);
            }
            return;
        }
        inLoading = true;
        isLoadCompleted = false;
        if(null!=mMediaLoadingListener){
            mMediaLoadingListener.onLoading(MediaView.this,percent,curDisplay);
        }
    }

    MediaLoadingListener mMediaLoadingListener;
    public void setMediaLoadingListener(MediaLoadingListener mediaLoadingListener) {
        this.mMediaLoadingListener = mediaLoadingListener;
    }

    public interface MediaLoadingListener{
        void onLoading(MediaView view, int percent, int curDisplay);
        void onLoadCompleted(MediaView view, int curDisplay);
    }

    OnPhotoClickListener mOnPhotoClickListener;

    public interface OnPhotoClickListener{
        void onClick(MediaView mediaView, float x, float y);
    }

}
