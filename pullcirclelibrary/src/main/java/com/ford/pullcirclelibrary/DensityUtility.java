package com.ford.pullcirclelibrary;


import android.content.Context;


public class DensityUtility {
 
  private static final String TAG = null;

/** 
   * 根据手机的分辨率从dp 的单位 转成为px(像素) 
   */ 
  public static int dip2px(Context context, float dpValue) { 
          final float scale = context.getResources().getDisplayMetrics().density; 
          return (int) (dpValue * scale + 0.5f); 
  } 

  /** 
   * 根据手机的分辨率从px(像素) 的单位 转成为dp 
   */ 
  public static int px2dip(Context context, float pxValue) { 
          final float scale = context.getResources().getDisplayMetrics().density; 
          return (int) (pxValue / scale + 0.5f);
  } 
  
  /**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(float pxValue, float fontScale) {
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(float spValue, float fontScale) {
		return (int) (spValue * fontScale + 0.5f);
	}


}