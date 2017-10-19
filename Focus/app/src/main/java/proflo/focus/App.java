package proflo.focus;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by forre on 10/18/2017.
 */

public class App extends Application{
    static Context context;
    public static Context get(){ return context; }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Global.getInstance().storeContext(getApplicationContext());
    }
}
