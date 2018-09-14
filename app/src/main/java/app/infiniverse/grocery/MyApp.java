package app.infiniverse.grocery;

import android.app.Application;
import android.os.SystemClock;


public class MyApp extends Application {

    private static MyApp mInstance;

    public static synchronized MyApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(500);
        mInstance = this;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
