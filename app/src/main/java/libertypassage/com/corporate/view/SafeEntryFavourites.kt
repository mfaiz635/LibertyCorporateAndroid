package libertypassage.com.corporate.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_header.*
import kotlinx.android.synthetic.main.safe_entry.*
import kotlinx.android.synthetic.main.safe_entry.llNoData
import kotlinx.android.synthetic.main.safe_entry.rvHistory
import kotlinx.android.synthetic.main.safe_entry.tvCheckIn
import kotlinx.android.synthetic.main.safe_entry.tvTag
import kotlinx.android.synthetic.main.safe_entry_favourites.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.ModelResponse
import libertypassage.com.corporate.model.ModelSafeEntry
import libertypassage.com.corporate.retofit.ApiInterface
import libertypassage.com.corporate.retofit.ClientInstance
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.Constants.Companion.safeEntryArrayList
import libertypassage.com.corporate.utilities.DialogProgress
import libertypassage.com.corporate.utilities.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SafeEntryFavourites : AppCompatActivity(), View.OnClickListener {

    private lateinit var context: Context
    var adapter: SafeEntryAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.safe_entry_favourites)
        context = this@SafeEntryFavourites

        iv_back.setOnClickListener(this)
        tvCheckIn.setOnClickListener(this)
        tvAllEntry.setOnClickListener(this)

        safeEntryArrayList.clear()
        safeEntryArrayList.addAll(Utility.getSafeEntry(context))
        if(safeEntryArrayList.size==0){
            llNoData.visibility = View.VISIBLE
            tvTag.visibility = View.GONE
            rvHistory.visibility = View.GONE
        }else{
            llNoData.visibility = View.GONE
            tvTag.visibility = View.VISIBLE
            rvHistory.visibility = View.VISIBLE
        }

        adapter = SafeEntryAdapter(safeEntryArrayList, context)
        rvHistory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvHistory.adapter = adapter
        adapter!!.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.tvCheckIn -> {
                val intent = Intent(context, UpdateMyTemperature::class.java)
                intent.putExtra("from", "home")
                startActivity(intent)
                finish()
            }
            R.id.tvAllEntry -> {
                val intent = Intent(context, SafeEntryActivity::class.java)
                startActivity(intent)
                this.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_left);
                finish()
            }
        }
    }

    class SafeEntryAdapter(list: List<ModelSafeEntry>, context: Context) : RecyclerView.Adapter<SafeEntryAdapter.ViewHolder>() {
        var list: List<ModelSafeEntry>
        var context: Context
        val checkoutList = ArrayList<String>()
        var dialogProgress: DialogProgress? = null
        var token = ""

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
            val itemView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.adapter_safe_entry, viewGroup,false)
            token = Utility.getSharedPreferences(context, Constants.KEY_BEARER_TOKEN)!!
            dialogProgress = DialogProgress(context)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val model: ModelSafeEntry = list[position]
            holder.tvUrlName.text = model.venueId
            holder.tvTime.text = "Visited on "+model.currentDate

            holder.tvCheckout.setOnClickListener {
                addAddressAndTemp(
                    model.address,
                    model.tempType,
                    model.currentTemp,
                    model.venueId,
                    model.nircId,
                    model.currentDate,
                    model.checkout,
                    model.favorite,
                    position,
                    holder)
                checkoutList.add(position.toString())
            }

            holder.ivFav.setOnClickListener {
                holder.ivFav.visibility = View.GONE
                holder.ivNotFav.visibility = View.VISIBLE
                Constants.safeEntryArrayList.set(position, ModelSafeEntry(
                    model.address,
                    model.tempType,
                    model.currentTemp,
                    model.venueId,
                    model.nircId,
                    model.currentDate,
                    model.checkout,
                    0))
                Utility.saveSafeEntry(context, Constants.safeEntryArrayList)
                notifyDataSetChanged()
            }

            if (list[position].favorite == 1) {
                holder.rlMain.visibility = View.VISIBLE
                holder.ivFav.visibility = View.VISIBLE
            } else {
                holder.rlMain.visibility = View.GONE
                holder.ivNotFav.visibility = View.GONE
            }

            if (list[position].checkout == 1) {
                holder.tvCheckoutDone.visibility = View.VISIBLE
                holder.tvCheckout.visibility = View.GONE
            } else {
                holder.tvCheckoutDone.visibility = View.GONE
                holder.tvCheckout.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var rlMain: RelativeLayout
            var tvUrlName: TextView
            var tvTime: TextView
            var tvCheckout: TextView
            var tvCheckoutDone: TextView
            var ivFav: ImageView
            var ivNotFav: ImageView

            init {
                rlMain = itemView.findViewById(R.id.rlMain)
                tvCheckout = itemView.findViewById(R.id.tvCheckout)
                tvCheckoutDone = itemView.findViewById(R.id.tvCheckoutDone)
                tvUrlName = itemView.findViewById(R.id.tvUrlName)
                tvTime = itemView.findViewById(R.id.tvTime)
                ivFav = itemView.findViewById(R.id.ivFav)
                ivNotFav = itemView.findViewById(R.id.ivNotFav)
            }
        }

        init {
            this.list = list
            this.context = context
        }

        private fun addAddressAndTemp(
            address: String,
            tempType: String,
            currentTemp: String,
            venueId: String,
            nircId: String,
            currentDate: String,
            checkout: Int,
            favorite: Int,
            position: Int,
            holder: ViewHolder
        ) {
            dialogProgress!!.show()
            val apiInterface: ApiInterface = ClientInstance.retrofitInstance!!.create(ApiInterface::class.java)
            val call: Call<ModelResponse> = apiInterface.addAddressTemp(Constants.KEY_HEADER+token,
                Constants.KEY_BOT, address, tempType, currentTemp, venueId, nircId, "checkout")

            call.enqueue(object : Callback<ModelResponse?> {
                override fun onResponse(call: Call<ModelResponse?>, response: Response<ModelResponse?>) {
                    dialogProgress!!.dismiss()
                    val responses: ModelResponse? = response.body()

                    if (responses != null && responses.error!!.equals(false)) {
                        safeEntryArrayList.set(position, ModelSafeEntry(
                            address,
                            tempType,
                            currentTemp,
                            venueId,
                            nircId,
                            currentDate,
                            1,
                            favorite))
                        Utility.saveSafeEntry(context, safeEntryArrayList)
                        notifyDataSetChanged()
                        holder.tvCheckout.visibility = View.GONE
                        holder.tvCheckoutDone.visibility = View.VISIBLE
                        val intent = Intent(context, SafeEntryCheckOut::class.java)
                        intent.putExtra("address", address)
                        intent.putExtra("tempType", tempType)
                        intent.putExtra("currentTemp", currentTemp)
                        intent.putExtra("venueId", venueId)
                        intent.putExtra("nricId", nircId)
                        intent.putExtra("currentDate", currentDate)
                        intent.putExtra("checkout", 1)
                        intent.putExtra("favorite", favorite)
                        intent.putExtra("position", position)
                        context.startActivity(intent)

                    } else if (responses != null && responses.error!!.equals(true)) {
                        dialogProgress!!.dismiss()
                        Toast.makeText(context, responses.message, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ModelResponse?>, t: Throwable) {
                    dialogProgress!!.dismiss()
                    Log.e("model", "onFailure    " + t.message)
                }
            })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
