package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.images_slider_activity.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.ClinicQrImage
import libertypassage.com.corporate.utilities.Constants
import java.util.*



class ImagesSliderActivity : AppCompatActivity(){
    lateinit var context: Context
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

        imageSlider.setSliderAdapter(
            BannerSliderAdapter(
                model,
                context
            )
        )
        imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imageSlider.currentPagePosition=positions
    }

    private class BannerSliderAdapter(
        var model: List<ClinicQrImage>,
        var context: Context
    ) : SliderViewAdapter<BannerSliderAdapter.SliderAdapterVH>() {
        override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
            @SuppressLint("InflateParams") val inflate = LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_image_slider,
                null
            )
            return SliderAdapterVH(inflate)
        }

        override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
            try {
                Glide.with(context).load(Constants.ImageUrl + model[position].clinicQrImage)
                    .placeholder(R.drawable.place_holder)
                    .fitCenter().into(viewHolder.ivMain)
            } catch (e: Exception) {
                Log.e("Exception", e.message.toString())
            }

            Log.e("imageSlider", model[position].clinicQrImage)
        }

        override fun getCount(): Int {
            //slider view count could be dynamic size
            return model.size
        }

        class SliderAdapterVH(itemView: View) :
            ViewHolder(itemView) {
            lateinit var itemView: View
            var ivMain: ImageView = itemView.findViewById(R.id.ivMain)
        }

        init {
            notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}