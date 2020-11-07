package com.example.musicplayer.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class ScreenUtils {

    /**
     * 转换dip为px
     */
    public static int convertDIP2PX(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 转换px为dip
     */
    public static int convertPX2DIP(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int convertSP2PX(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private volatile static int screenHeight = 0;
    private volatile static int screenWidth = 0;
    private volatile static float screenDensity = 0;

    public static int getScreenHeight(Activity context) {
        if (screenWidth == 0 || screenHeight == 0) {
            if (context==null){
                return getScreenHeight();
            }
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenDensity = dm.density;
            screenHeight = dm.heightPixels;
            screenWidth = dm.widthPixels;
        }
        return screenHeight;
    }

    public static int getScreenWidth(Activity context) {
        if (screenWidth == 0 || screenHeight == 0) {
            if (context==null){
                return getScreenWidth();
            }
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenDensity = dm.density;
            screenHeight = dm.heightPixels;
            screenWidth = dm.widthPixels;
        }
        return screenWidth;
    }

    public static int getScreenHeight() {
        DisplayMetrics displayMetric = Resources.getSystem().getDisplayMetrics();
        return displayMetric.heightPixels;
    }
    public static int getScreenWidth() {
        DisplayMetrics displayMetric = Resources.getSystem().getDisplayMetrics();
        return displayMetric.widthPixels;
    }

    public static int getScreenHeightDp() {
        DisplayMetrics displayMetric = Resources.getSystem().getDisplayMetrics();
        int height=displayMetric.heightPixels;
        float density = displayMetric.density;// "屏幕密度"（0.75 / 1.0 / 1.5）
        int densityDpi = displayMetric.densityDpi;// 屏幕密度dpi（120 / 160 / 240）每一英寸的屏幕所包含的像素数.值越高的设备，其屏幕显示画面的效果也就越精细
        // 屏幕宽度算法:屏幕宽度（像素）/"屏幕密度"   px = dp * (dpi / 160)
        return (int) (height / density);//屏幕高度dp
    }
    public static int getScreenWidthDp() {
        DisplayMetrics displayMetric = Resources.getSystem().getDisplayMetrics();
        int width=displayMetric.widthPixels;
        float density = displayMetric.density;// "屏幕密度"（0.75 / 1.0 / 1.5）
        int densityDpi = displayMetric.densityDpi;// 屏幕密度dpi（120 / 160 / 240）每一英寸的屏幕所包含的像素数.值越高的设备，其屏幕显示画面的效果也就越精细
        // 屏幕宽度算法:屏幕宽度（像素）/"屏幕密度"   px = dp * (dpi / 160)
        return (int) (width / density);//屏幕高度dp
    }
    /**
     * 反馈宽高的数组【宽：高】
     * @return
     */
    public static int[] getScreenSize() {
        DisplayMetrics displayMetric = Resources.getSystem().getDisplayMetrics();
        return new int[]{displayMetric.widthPixels,displayMetric.heightPixels};
    }
    /**
     * 反馈宽高的数组【宽：高】
     * @return
     */
    public static int[] getScreenSizeDb() {
        DisplayMetrics displayMetric = Resources.getSystem().getDisplayMetrics();
        int width=displayMetric.widthPixels;
        int height=displayMetric.heightPixels;
        float density = displayMetric.density;// "屏幕密度"（0.75 / 1.0 / 1.5）
        int densityDpi = displayMetric.densityDpi;// 屏幕密度dpi（120 / 160 / 240）每一英寸的屏幕所包含的像素数.值越高的设备，其屏幕显示画面的效果也就越精细
        // 屏幕宽度算法:屏幕宽度（像素）/"屏幕密度"   px = dp * (dpi / 160)
        return new int[]{(int) (width / density),(int) (width / height)};
    }
}
