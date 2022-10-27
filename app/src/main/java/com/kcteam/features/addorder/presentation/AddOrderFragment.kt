package com.kcteam.features.addorder.presentation

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.app.utils.AppUtils
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Pratishruti on 30-10-2017.
 */
class AddOrderFragment:BaseFragment() {
    private lateinit var mAddedItemListAdapter: AddedItemListAdapter
    private lateinit var myAddedItemList: RecyclerView
    private lateinit var mContext: Context
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var orderDate:AppCustomEditText
    var myCalendar = Calendar.getInstance(Locale.ENGLISH)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater!!.inflate(R.layout.fragment_add_order, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        myAddedItemList=view.findViewById(R.id.added_item_RCV)
        orderDate=view.findViewById(R.id.order_date_EDT)
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        orderDate.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                // TODO Auto-generated method stub
                DatePickerDialog(mContext, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        initAdapter()
    }

  fun updateLabel() {
      val myFormat = "dd/MM/yyyy"; //In which you need put here
     val sdf = SimpleDateFormat(myFormat, Locale.US);
      orderDate.setText(AppUtils.getFormattedDate(myCalendar.getTime()));  //sdf.format(myCalendar.getTime())
    }
    private fun initAdapter() {
        mAddedItemListAdapter = AddedItemListAdapter(this!!.context!!, object : AddItemListClickListener {
            override fun OnItemChkBoxClickListener(position: Int) {

            }

        })
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        myAddedItemList.layoutManager=layoutManager
        myAddedItemList.adapter=mAddedItemListAdapter

    }

}