package org.horaapps.adbfi;

import android.app.Application;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;

/**
 * Created by gilbertndr on 2/14/17.
 */

public class App extends Application {

    private static App myInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new GoogleMaterial());
        Iconics.registerFont(new FontAwesome());
        myInstance = this;
    }


    public static synchronized App getMyInstance(){
        return myInstance;
    }

}
