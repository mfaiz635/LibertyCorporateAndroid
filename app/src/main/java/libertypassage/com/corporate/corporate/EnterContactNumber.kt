package libertypassage.com.corporate.corporate

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.c_enter_contact_number.*
import libertypassage.com.corporate.R
import libertypassage.com.corporate.model.CountryDetail
import libertypassage.com.corporate.model.CountryDto
import libertypassage.com.corporate.utilities.Constants
import libertypassage.com.corporate.utilities.RecyclerItemClickListener
import libertypassage.com.corporate.utilities.Utility
import libertypassage.com.corporate.view.adapter.CountryCodeAdapter
import libertypassage.com.corporate.view.adapter.CountryCodeSpinnerAdapter
import java.util.*


class EnterContactNumber : AppCompatActivity(), View.OnClickListener {
    lateinit var context: Context
    var countryCode = ""
    var mobile = ""
    //  country dialog
    var countryNamesArray = ArrayList<String>()
    var countries = ArrayList<CountryDto>()
    var filterNames = ArrayList<CountryDto>()
    var adapter: CountryCodeSpinnerAdapter? = null
    var dialog: Dialog? = null
    var countryName = ""
    var countryId = 0



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_enter_contact_number)
        setupParent(window.decorView.findViewById(android.R.id.content))
        context = this@EnterContactNumber

        init()
        tv_countryCode.setOnClickListener(this)
        tvDesh.setOnClickListener(this)
        tv_continue.setOnClickListener(this)

        et_mobileNo.isFocusable = false
        et_mobileNo.setOnTouchListener { v, event ->
            et_mobileNo.isFocusableInTouchMode = true
            false
        }
    }

    private fun init() {
        val jsonFileString = Utility.getJsonFromAssets(applicationContext, "countrylist.json")
        val gson = Gson()
        val listUserType = object : TypeToken<List<CountryDetail?>?>() {}.type
        val countryDetails: List<CountryDetail> = gson.fromJson(
            jsonFileString,
            listUserType
        )

        for (i in countryDetails.indices) {
            countries.add(
                CountryDto(
                    countryDetails[i].countryName,
                    countryDetails[i].callingcodes,
                    countryDetails[i].id
                )
            )
            countryDetails[i].countryName?.toUpperCase(Locale.ROOT)?.let {
                countryNamesArray.add(
                    it
                )
            }
        }
        adapter = CountryCodeSpinnerAdapter(context, countryNamesArray)
        spnCountryCode.adapter = adapter
        spnCountryCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val flag = ""
                if (flag == "1") {
                    val k: Int = countryNamesArray.indexOf(countryName)
                    val code: Int = countries[k].countryCode!!
                    countryId = countries[position].countryId!!
                    spnCountryCode.setSelection(k)
                    tv_countryCode.setText(code)
                } else if (flag == "x") {
                    val country = spnCountryCode.selectedItem.toString()
                    val positions: Int = countryNamesArray.indexOf(country)
                    val code: Int = countries[positions].countryCode!!
                    countryId = countries[positions].countryId!!
                    spnCountryCode.setSelection(positions)
                    tv_countryCode.setText(code)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_continue -> {
                countryCode = tv_countryCode.text.toString().trim()
                mobile = et_mobileNo.text.toString().trim()

                if (!TextUtils.isEmpty(mobile)) {
                    if (mobile.length > 7) {
                        Utility.setSharedPreference(context, "isVerify", "refNumber")
                        Utility.setSharedPreference(context, Constants.KEY_MOBILE,countryCode+mobile)
                        Utility.setSharedPreference(context, Constants.KEY_COUNTRY_ID, countryId.toString())

                        val intent = Intent(context, EnterReferenceNumber::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(context,"Please enter valid mobile number", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter mobile number", Toast.LENGTH_LONG).show()
                }
            }

            R.id.tv_countryCode -> {
                selectCountryDialog()
            }

            R.id.tvDesh -> {
                selectCountryDialog()
            }
        }
    }

    private fun selectCountryDialog() {
        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.country_popup)
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(true)
        val edtCountry = dialog!!.findViewById<EditText>(R.id.edtCountry)
        val cancel = dialog!!.findViewById<ImageView>(R.id.cancel)
        val rvCountry: RecyclerView = dialog!!.findViewById(R.id.rvCountry)
        val linearLayoutManager = LinearLayoutManager(context)
        val countryAdapter = CountryCodeAdapter(context, countries)
        rvCountry.layoutManager = linearLayoutManager
        rvCountry.adapter = countryAdapter
        rvCountry.addOnItemTouchListener(
            RecyclerItemClickListener(context, rvCountry,
                object : RecyclerItemClickListener.OnItemClickListener {
                    @SuppressLint("SetTextI18n")
                    override fun onItemClick(view: View?, position: Int) {
                        val country: CountryDto = if (filterNames.size > 0) {
                            filterNames[position]
                        } else {
                            countries[position]
                        }
                        tv_countryCode.text = "+" + country.countryCode
                        dialog!!.dismiss()
                    }

                    override fun onItemDoubleClick(view: View?, position: Int) {}
                    override fun onItemLongClick(view: View?, position: Int) {}
                })
        )

        edtCountry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(s: Editable) {
                filter(s.toString(), countryAdapter)
            }
        })
        cancel.setOnClickListener { dialog!!.dismiss() }
        dialog!!.show()
    }

    @SuppressLint("DefaultLocale")
    private fun filter(text: String, codeAdapter: CountryCodeAdapter) {
        //new array list that will hold the filtered data
        filterNames = ArrayList()
        //looping through existing elements
        for (s in countries) {
            //if the existing elements contains the search input
            if (s.countryName?.toUpperCase()?.startsWith(text.toUpperCase())!!) {
                //adding the element to filtered list
                filterNames.add(s)
            }
        }
        //calling a method of the adapter class and passing the filtered list
        codeAdapter.filterList(filterNames)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupParent(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                if (currentFocus != null) {
                    val inputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                }
                false
            }
        }
        //If a layout container, iterate over children
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupParent(innerView)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
