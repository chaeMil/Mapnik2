package cz.mapnik.app.utils;

/**
 * Created by chaemil on 4.6.16.
 */
public class ChromeOSUtils {

    public static boolean isRunningInChromeOS() {
        return (android.os.Build.BRAND.equals("chromium")) || (android.os.Build.MANUFACTURER.equals("chromium"));
    }
}
