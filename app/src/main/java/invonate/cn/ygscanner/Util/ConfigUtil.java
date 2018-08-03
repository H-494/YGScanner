package invonate.cn.ygscanner.Util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class ConfigUtil {
	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.yonggang.ygckscanner", 0).versionCode;
		} catch (NameNotFoundException e) {

		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.yonggang.ygckscanner", 0).versionName;
		} catch (NameNotFoundException e) {

		}
		return verName;
	}
}
