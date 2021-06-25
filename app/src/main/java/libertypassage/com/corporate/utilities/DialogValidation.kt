package libertypassage.com.corporate.utilities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_box_validation.*
import libertypassage.com.corporate.R


class DialogValidation(context: Context, title: String, subTitle: String, messages: ArrayList<String>?) : Dialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_box_validation)
        this.window!!.setBackgroundDrawable(ColorDrawable(0))
        this.setCanceledOnTouchOutside(false)
        this.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        tvSubTitle.text = subTitle
        if (messages != null) {
            val inflater = (context as Activity).layoutInflater
            for (i in messages.indices) {
                val view = inflater.inflate(R.layout.validation_view, llValidation, false)
                val tvName = view.findViewById<TextView>(R.id.tvName)
                tvName.text = messages[i]
                llValidation.addView(view)
            }
        }
        tv_ok.setOnClickListener { dismiss() }
    }
}
