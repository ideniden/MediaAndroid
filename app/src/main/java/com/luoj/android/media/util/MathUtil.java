package com.luoj.android.media.util;
/**
 * @author LuoJ
 * @date 2014-6-3
 * @package j.android.library.utils -- MathUtil.java
 * @Description 
 */
public class MathUtil {

	/**
	 * 计算百分比值
	 *
	 * @return
	 */
	public static int calculatePercentage(long total, long current) {
		return (int) ((float) current / (float) total * 100);
	}

	/**
	 * 两点间的距离
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2)
				+ Math.abs(y1 - y2) * Math.abs(y1 - y2));
	}

	/**
	 * 计算点a(x,y)的角度
	 * @param x
	 * @param y
	 * @return
	 */
	public static double pointTotoDegrees(double x, double y) {
		return Math.toDegrees(Math.atan2(x, y));
	}
	
	/**
	 * 点在圆肉
	 * @param sx
	 * @param sy
	 * @param r
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean checkInRound(float sx, float sy, float r, float x,
			float y) {
		return Math.sqrt((sx - x) * (sx - x) + (sy - y) * (sy - y)) < r;
	}

	public static float mapping(float val,float valueIntervalStart,float valueIntervalEnd,float convertIntervalStart,float convertIntervalEnd){
		//如果数值小于0，则整个区间右移至0点以后
		if(val<0){

		}
		if(val<valueIntervalStart){
			val=valueIntervalStart;
		}
		if(val>valueIntervalEnd){
			val=valueIntervalEnd;
		}
		return val/((valueIntervalEnd-valueIntervalStart)/(convertIntervalEnd-convertIntervalStart))+convertIntervalStart;
	}

	public static int mappingReturnInt(float val,float intervalStart1,float intervalEnd1,float intervalStart2,float intervalEnd2){
		return (int)mapping(val,intervalStart1,intervalEnd1,intervalStart2,intervalEnd2);
	}

}
