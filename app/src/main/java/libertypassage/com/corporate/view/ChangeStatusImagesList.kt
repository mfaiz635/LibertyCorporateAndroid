package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.change_status.*
import kotlinx.android.synthetic.main.change_status_image_list.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.ClinicQrImage
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.Utility
import java.io.Serializable
import java.util.*



class ChangeStatusImagesList : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    private var dialogProgress: DialogProgress? = null
    private var multiSizeImagesAdapter: MultiSizeImagesAdapter? = null
    private var returnImagesList: MutableList<ClinicQrImage> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_status_image_list)

        context = this@ChangeStatusImagesList
        dialogProgress = DialogProgress(context)
        init()
    }

    private fun init() {
        iv_back.setOnClickListener(this)
        llNoData.setOnClickListener(this)

        returnImagesList.clear()
        returnImagesList.addAll(Utility.getStatusImages(context))
        Log.e("imagesArrayList",  returnImagesList.toString())

        if(returnImagesList.size>0){
            rv_images.visibility = View.VISIBLE
            llNoData.visibility = View.GONE
        }else{
            rv_images.visibility = View.GONE
            llNoData.visibility = View.VISIBLE
        }

        multiSizeImagesAdapter = MultiSizeImagesAdapter(
            returnImagesList,
            context
        )
        val layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        rv_images.layoutManager = layoutManager
        rv_images.adapter = multiSizeImagesAdapter
        multiSizeImagesAdapter!!.notifyDataSetChanged()
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
            }
        }
    }

    class MultiSizeImagesAdapter(
        var list: MutableList<ClinicQrImage>, var context: Context ): RecyclerView.Adapter<MultiSizeImagesAdapter.ViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
            val itemView = LayoutInflater.from(viewGroup.context).inflate(
                R.layout.images_adapter,
                viewGroup,
                false
            )
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val model = list[position]
            try {
                Glide.with(context).load(Constants.ImageUrl + model.clinicQrImage)
                //    .placeholder(R.drawable.place_holder)
                    .centerCrop()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivMain)
            } catch (e: Exception) {
                Log.e("Exception", e.message!!)
            }
            Log.e("img123", Constants.ImageUrl + model.clinicQrImage)


            holder.cardViewImage.setOnClickListener {
                val intent = Intent(context, ImagesSliderActivity::class.java)
                intent.putExtra("list", list as Serializable)
                intent.putExtra("position", position)
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var cardViewImage: CardView = itemView.findViewById(R.id.cardViewImage)
            var ivMain: ImageView = itemView.findViewById(R.id.ivMain)
        }
    }


