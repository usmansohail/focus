package proflo.focus;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by forre on 10/18/2017.
 */

public class App extends Application{

    public static Context get() { return this.getApplicationContext(); }

    @Override
    public void onCreate() {
        super.onCreate();
        Global.getInstance().storeContext(getApplicationContext());
    }
}
