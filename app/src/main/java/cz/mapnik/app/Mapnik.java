package cz.mapnik.app;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by chaemil on 2.2.16.
 */
public class Mapnik extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/RobotoSlab-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}
