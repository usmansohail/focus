package proflo.focus;

import android.app.Application;

/**
 * Created by forre on 10/18/2017.
 */

public class App extends Application{
    private static App instance;
    public static App get() { return instance; }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
