package libertypassage.com.app.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import libertypassage.com.app.R;



@SuppressLint("AppCompatCustomView")
public class CustomCheckBox extends CheckBox {


    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setButtonDrawable(new StateListDrawable());
    }
    @Override
    public void setChecked(boolean t){
        if(t)
        {
            this.setBackgroundResource(R.drawable.check_box_mark);
        }
        else
        {
            this.setBackgroundResource(R.drawable.check_box);
        }
        super.setChecked(t);
    }
}