package com.xzh.core.utils

import android.app.ActivityManager
import android.content.Context

/**
 *  created by xzh on 2019/7/9
 *  /**
 * 判断服务是否开启
 *
 * @return
*/
public static boolean isServiceRunning(Context context, String ServiceName) {
if (TextUtils.isEmpty(ServiceName)) {
return false;
}
ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(1000);
for (int i = 0; i < runningService.size(); i++) {
Log.i("服务运行1：",""+runningService.get(i).service.getClassName().toString());
if (runningService.get(i).service.getClassName().toString().equals(ServiceName)) {
return true;
}
}
return false;
}
 */

fun isServiceRunning(context: Context?, serviceName: String?): Boolean {
    val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    manager.getRunningServices(Int.MAX_VALUE).forEach {
        if (it.service.shortClassName == serviceName) return true
    }
    return false
}