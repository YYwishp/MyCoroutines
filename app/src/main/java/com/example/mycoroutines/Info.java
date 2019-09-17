package com.example.mycoroutines;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Created by gaoyuxiang on 2019-09-17.
 * ==============================
 * 功能描述：
 */
public class Info {

	public static synchronized Bitmap getBitmap(Context context) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		Drawable d = packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
		BitmapDrawable bd = (BitmapDrawable) d;
		Bitmap bm = bd.getBitmap();
		return bm;
	}


}
