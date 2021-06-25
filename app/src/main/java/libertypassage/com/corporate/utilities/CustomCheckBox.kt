package libertypassage.com.corporate.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.CheckBox
import libertypassage.com.corporate.R


@SuppressLint("AppCompatCustomView")
class CustomCheckBox(context: Context?, attrs: AttributeSet?) : CheckBox(context, attrs) {
    override fun setChecked(t: Boolean) {
        if (t) {
            setBackgroundResource(R.drawable.check_box_mark)
        } else {
            setBackgroundResource(R.drawable.check_box)
        }
        super.setChecked(t)
    }
}