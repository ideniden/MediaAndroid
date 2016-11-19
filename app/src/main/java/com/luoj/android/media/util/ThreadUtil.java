package com.luoj.android.media.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author LuoJ
 * @date 2014-7-22
 * @package j.android.library.utils -- ThreadUtil.java
 * @Description 线程工具类
 */
public class ThreadUtil {

	/**
	 * 判断调用此函数的执行线程是否为Android UI线程
	 * @param context
	 * @return
	 */
	public static boolean isRunInMainThread(Context context){
		Looper mainLooper = context.getMainLooper();
		if (null!=mainLooper) {
			long mainThreadId = mainLooper.getThread().getId();
			long currentThreadId = Thread.currentThread().getId();
			if (currentThreadId==mainThreadId) {
				return true;
			}
		}
		return false;
	}

	@SuppressLint("HandlerLeak")
	final static Handler handler=new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			AsyncObject asyncObject= (AsyncObject) msg.obj;
			if(null!=asyncObject){
				asyncObject.execute();
			}
		}
	};

	/**
	 * 异步实现同步运行.
	 * 实现关键的接口函数即可
	 * @param syncInterface
	 */
	public static <T> Thread syncRun(final SyncInterface<T> syncInterface){
		if(null==syncInterface){
			throw new RuntimeException("parameter cannot be empty");
		}
		return new Thread() {
			@Override
			public void run() {
				Object result=null;
				if(null!=syncInterface){
					result=syncInterface.runInWorkThread();
				}
				Message msg= handler.obtainMessage(0);
				msg.obj= new AsyncObject(result,syncInterface);
				handler.sendMessage(msg);
			}
		};
	}
	
	/**
	 * 同步接口
	 * @author LuoJ
	 * @date 2014-7-22
	 * @package com.anhry.android.utils -- ThreadUtil.java
	 * @Description
	 */
	public interface SyncInterface<T>{
		T runInWorkThread();
		void workThreadIsDone(T result);
	}

}


