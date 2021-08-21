package libertypassage.com.corporate.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.images_slider_activity.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.ClinicQrImage
import libertypassage.com.corporate.utilities.Constants
import java.util.*


class ImagesSliderActivity : AppCompatActivity(){
    lateinit var context: Context
    private var mViewPagerAdapter: ViewPagerAdapter? = null
    private var model: ArrayList<ClinicQrImage> = ArrayList()
    private var positions = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.images_slider_activity)

        context = this@ImagesSliderActivity

        init()
    }

    private fun init() {
        model = intent.getSerializableExtra("list") as ArrayList<ClinicQrImage>
        positions = intent.getIntExtra("position", 0)

        iv_back.setOnClickListener { finish() }

        mViewPagerAdapter = ViewPagerAdapter(this@ImagesSliderActivity, model)
        mViewPager!!.adapter = mViewPagerAdapter
        mViewPager!!.currentItem=positions
    }

    internal class ViewPagerAdapter(var context: Context,  var images: ArrayList<ClinicQrImage>) : PagerAdapter() {

        var mLayoutInflater: LayoutInflater
        override fun getCount(): Int {
            return images.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object` as RelativeLayout
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView: View = mLayoutInflater.inflate(R.layout.adapter_image_slider, container, false)
            val imageView = itemView.findViewById(R.id.ivMain) as ImageView
            try {
                Glide.with(context).load(Constants.ImageUrl + images[position].clinicQrImage)
                    .placeholder(R.drawable.place_holder)
                    .fitCenter().into(imageView)
            } catch (e: Exception) {
                Log.e("Exception", e.message.toString())
            }
            // Adding the View
            Objects.requireNonNull(container).addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as RelativeLayout)
        }

        // Viewpager Constructor
        init {
            images = images
            mLayoutInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
    }

    override fun onBackPressed() {
        finish()
    }
}