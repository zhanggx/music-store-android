package com.example.musicplayer.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class ContextUtils {

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param context
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context context, String serviceName) {
        ActivityManager myAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(200);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
           // Log.v(TAG,mName);
            if (mName.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
