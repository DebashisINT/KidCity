package com.kcteam.features.member.presentation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.FileProvider
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.NewQuotation.ViewAllQuotListFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.member.api.TeamRepoProvider
import com.kcteam.features.member.model.TeamLocListResponseModel
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*

/**
 * Created by Saikat on 30-Mar-20.
 */
class MemberActivityFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var pickDate: AppCustomTextView
    private lateinit var dayWiseHistory: RecyclerView
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private lateinit var tv_total_distance: AppCustomTextView
    private lateinit var tv_share_logs: AppCustomTextView
    private lateinit var tv_sync_all: AppCustomTextView
    private lateinit var rl_loc_main: RelativeLayout
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var tv_share_pdf: AppCustomTextView
    private lateinit var tv_visit_distance: AppCustomTextView
    private lateinit var iv_team_map_view: ImageView

    private var userId = ""

    private val myCalendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    companion object {

        fun newInstance(userId: Any): MemberActivityFragment {
            val fragment = MemberActivityFragment()

            if (userId is String) {
                val bundle = Bundle()
                bundle.putString("user_id", userId)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        userId = arguments?.getString("user_id")?.toString()!!
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_history_daywise, container, false)

        initView(view)
        updateLabel()
        callFetchLocationApi()

        return view
    }

    private fun initView(view: View) {
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        pickDate = view.findViewById(R.id.pick_a_date_TV)
        dayWiseHistory = view.findViewById(R.id.history_daywise_RCV)
        dayWiseHistory.layoutManager = LinearLayoutManager(mContext)

        tv_total_distance = view.findViewById(R.id.tv_total_distance)
        tv_share_logs = view.findViewById(R.id.tv_share_logs)
        tv_sync_all = view.findViewById(R.id.tv_sync_all)
        rl_loc_main = view.findViewById(R.id.rl_loc_main)
        tv_no_data = view.findViewById(R.id.tv_no_data)
        tv_share_pdf = view.findViewById(R.id.tv_share_pdf)
        tv_visit_distance = view.findViewById(R.id.tv_visit_distance)

        iv_team_map_view = view.findViewById(R.id.iv_team_map_view)
        iv_team_map_view.visibility=View.VISIBLE

        tv_sync_all.visibility = View.GONE
        tv_share_logs.visibility = View.GONE

        pickDate.setOnClickListener(this)
        rl_loc_main.setOnClickListener(null)
        tv_share_pdf.setOnClickListener(this)
        iv_team_map_view.setOnClickListener(this)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.iv_team_map_view ->{
                (mContext as DashboardActivity).loadFragment(FragType.MapViewForTeamFrag, true, userId!!+"~"+AppUtils.getFormattedDateString(myCalendar))
            }
            R.id.pick_a_date_TV -> {
                val datePicker = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datePicker.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datePicker.show()
            }

            R.id.tv_share_pdf -> {
                if ((mContext as DashboardActivity).memberLocationList == null || (mContext as DashboardActivity).memberLocationList!!.isEmpty()) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                    return
                }

                val heading = "TEAM TIMELINE DETAILS"
                var pdfBody = "\n\n\nDate: " + pickDate.text.toString().trim() + "\n\n\n\n" + getString(R.string.visit_distance) +
                        " " + tv_visit_distance.text.toString().trim() + "\n\n" + getString(R.string.total_distance_travelled) + " " +
                        tv_total_distance.text.toString().trim() + "\n\n\n\n"

                (mContext as DashboardActivity).memberLocationList?.forEach {
                    pdfBody += it.last_update_time + ":        " + it.location_name +
                            "\n                           " + it.distance_covered + " " + getString(R.string.distance_covered) +
                            "\n                           " + it.shops_covered + " " + getString(R.string.no_of_shop_visited) +
                            "\n                           " + it.meetings_attended + " " + getString(R.string.no_of_meeting_visited) +
                            "\n\n\n"
                }


                val image = BitmapFactory.decodeResource(mContext.resources, R.mipmap.ic_launcher)

                val path = FTStorageUtils.stringToPdf(pdfBody, mContext, "FTS_Timeline_" +
                        AppUtils.getFormattedDateForApi(myCalendar.time) + "_" + userId + ".pdf", image, heading, 3.2f)
                if (!TextUtils.isEmpty(path)) {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        val fileUrl = Uri.parse(path)

                        val file = File(fileUrl.path)
//                        val uri = Uri.fromFile(file)
                        val uri:Uri= FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
                        shareIntent.type = "image/png"
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                        startActivity(Intent.createChooser(shareIntent, "Share pdf using"));
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else
                    (mContext as DashboardActivity).showSnackMessage("Pdf can not be sent.")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel()
        callFetchLocationApi()
    }

    private fun updateLabel() {
        pickDate.text = AppUtils.getFormattedDate(myCalendar.time)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callFetchLocationApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = TeamRepoProvider.teamRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.teamLocList(userId, AppUtils.getFormattedDateString(myCalendar))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val list = result as TeamLocListResponseModel
                            when (list.status) {
                                "200" -> {
                                    tv_total_distance.text = list.total_distance + " Km(s)"
                                    tv_no_data.visibility = View.GONE
                                    (mContext as DashboardActivity).memberLocationList = list.location_details
                                    dayWiseHistory.adapter = MemberActivityAdapter(mContext, list.location_details)
                                    tv_visit_distance.text = list.total_visit_distance + " Km(s)"
                                    progress_wheel.stopSpinning()
                                }
                                NetworkConstant.SESSION_MISMATCH -> {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).apply {
                                        clearData()
                                        startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                        overridePendingTransition(0, 0)
                                        finish()
                                    }
                                }
                                NetworkConstant.NO_DATA -> {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(list.message!!)
                                    tv_no_data.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).memberLocationList = list.location_details
                                    tv_visit_distance.text = list.total_visit_distance + " Km(s)"
                                }
                                else -> {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(list.message!!)
                                    (mContext as DashboardActivity).memberLocationList = list.location_details
                                }
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            (mContext as DashboardActivity).memberLocationList = null
                        })
        )
    }
}