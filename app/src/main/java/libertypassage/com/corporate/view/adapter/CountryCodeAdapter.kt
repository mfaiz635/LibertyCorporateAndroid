package libertypassage.com.corporate.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.CountryDto
import java.util.*

class CountryCodeAdapter(var context: Context, var countryList: ArrayList<CountryDto>) :
    RecyclerView.Adapter<CountryCodeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val itemView =  LayoutInflater.from(viewGroup.context).inflate(R.layout.country_text, viewGroup, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val countries = countryList[i]
        viewHolder.lbl_name.text = countries.countryName
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lbl_name: TextView

        init {
            lbl_name = itemView.findViewById(R.id.lbl_name)
        }
    }

    fun filterList(filterdNames: ArrayList<CountryDto>?) {
        countryList = ArrayList()
        countryList.addAll(filterdNames!!)
        notifyDataSetChanged()
    }
}