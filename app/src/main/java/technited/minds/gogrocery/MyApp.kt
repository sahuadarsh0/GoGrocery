package technited.minds.gogrocery

import android.app.Application
import androidx.databinding.library.baseAdapters.BuildConfig
import com.rezwan.knetworklib.KNetwork
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KNetwork.initialize(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}