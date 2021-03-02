package libertypassage.com.corporate.services

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log



class ProcessMainClass {
    private val TAG = ProcessMainClass::class.java.simpleName
    private var serviceIntent: Intent? = null

    fun ProcessMainClass() {}


    private fun setServiceIntent(context: Context) {
     //   if (serviceIntent == null) {
            serviceIntent = Intent(context, MyService::class.java)
    //    }
    }

    /**
     * launching the service
     */
    fun launchService(context: Context) {
        setServiceIntent(context)
        // depending on the version of Android we eitehr launch the simple service (version<O)
        // or we start a foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
        Log.e(TAG, "ProcessMainClass: start service go!!!!")
    }
}