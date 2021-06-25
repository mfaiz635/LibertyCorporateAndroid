package libertypassage.com.corporate.utilities

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Window
import libertypassage.com.corporate.R


class DialogProgress(context: Context) : Dialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.progress_loader)
        this.setCancelable(true)
        this.setCanceledOnTouchOutside(false)
        this.window!!.setBackgroundDrawable(ColorDrawable(0))
    }
}