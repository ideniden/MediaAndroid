package com.luoj.android.media.util;

/**
 * Created by 京 on 2016/8/16.
 */
public class AsyncObject<T> {
    T result;
    ThreadUtil.SyncInterface<T> callback;

    public AsyncObject(T result, ThreadUtil.SyncInterface callback) {
        this.result = result;
        this.callback = callback;
    }

    public void execute(){
        if(null!=callback)callback.workThreadIsDone(result);
    }

}
