package invonate.cn.ygscanner.Util;

public class Utils {
	private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 3000) {   
            return true;   
        }   
        lastClickTime = time;   
        return false;   
    }
}
