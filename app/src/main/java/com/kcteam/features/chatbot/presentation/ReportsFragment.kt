package com.kcteam.features.chatbot.presentation

import android.content.Context
import android.os.Bundle
import androidx.cardview.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.types.FragType
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity

class ReportsFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var fl_reports_main: FrameLayout
    private lateinit var cv_attendance_reports: CardView
    private lateinit var cv_performance_reports: CardView
    private lateinit var cv_visit_reports: CardView
    private lateinit var cv_team_details: CardView
    private lateinit var cv_timeline: CardView
    private lateinit var cv_party_details: CardView
    private lateinit var cv_order_details: CardView
    private lateinit var cv_visit_duration: CardView
    private lateinit var cv_total_visit: CardView
    private lateinit var cv_collection: CardView
    private lateinit var cv_map: CardView
    private lateinit var cv_quot: CardView
    private lateinit var cv_targ_achv: CardView
    private lateinit var cv_achv_details: CardView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_reports, container, false)

        initView(view)
        initClickListener()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            fl_reports_main = findViewById(R.id.fl_reports_main)
            cv_attendance_reports = findViewById(R.id.cv_attendance_reports)
            cv_performance_reports = findViewById(R.id.cv_performance_reports)
            cv_visit_reports = findViewById(R.id.cv_visit_reports)
            cv_team_details = findViewById(R.id.cv_team_details)
            cv_timeline = findViewById(R.id.cv_timeline)
            cv_party_details = findViewById(R.id.cv_party_details)
            cv_order_details = findViewById(R.id.cv_order_details)
            cv_visit_duration = findViewById(R.id.cv_visit_duration)
            cv_total_visit = findViewById(R.id.cv_total_visit)
            cv_collection = findViewById(R.id.cv_collection)
            cv_map = findViewById(R.id.cv_map)
            cv_quot = findViewById(R.id.cv_quot)
            cv_targ_achv = findViewById(R.id.cv_targ_achv)
            cv_achv_details = findViewById(R.id.cv_achv_details)
        }

        if (Pref.willPerformanceReportShow)
            cv_performance_reports.visibility = View.VISIBLE
        else
            cv_performance_reports.visibility = View.GONE

        if (Pref.willVisitReportShow)
            cv_visit_reports.visibility = View.VISIBLE
        else
            cv_visit_reports.visibility = View.GONE

        if (Pref.willShowTeamDetails)
            cv_team_details.visibility = View.VISIBLE
        else
            cv_team_details.visibility = View.GONE

        if (Pref.isOrderShow)
            cv_order_details.visibility = View.VISIBLE
        else
            cv_order_details.visibility = View.GONE

        if (Pref.isCollectioninMenuShow)
            cv_collection.visibility = View.VISIBLE
        else
            cv_collection.visibility = View.GONE

        if (Pref.isQuotationShow)
            cv_quot.visibility = View.VISIBLE
        else
            cv_quot.visibility = View.GONE

        if (Pref.isTarVsAchvEnable)
            cv_targ_achv.visibility = View.VISIBLE
        else
            cv_targ_achv.visibility = View.GONE

        if (Pref.isAchievementEnable)
            cv_achv_details.visibility = View.VISIBLE
        else
            cv_achv_details.visibility = View.GONE
    }

    private fun initClickListener() {
        fl_reports_main.setOnClickListener(null)
        cv_attendance_reports.setOnClickListener(this)
        cv_performance_reports.setOnClickListener(this)
        cv_visit_reports.setOnClickListener(this)
        cv_team_details.setOnClickListener(this)
        cv_timeline.setOnClickListener(this)
        cv_party_details.setOnClickListener(this)
        cv_order_details.setOnClickListener(this)
        cv_visit_duration.setOnClickListener(this)
        cv_total_visit.setOnClickListener(this)
        cv_collection.setOnClickListener(this)
        cv_map.setOnClickListener(this)
        cv_quot.setOnClickListener(this)
        cv_targ_achv.setOnClickListener(this)
        cv_achv_details.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.cv_attendance_reports -> {
                (mContext as DashboardActivity).isAttendanceReportFromDrawer = false
                (mContext as DashboardActivity).loadFragment(FragType.AttendanceReportFragment, true, "")
            }

            R.id.cv_performance_reports -> {
                (mContext as DashboardActivity).isPerformanceReportFromDrawer = false
                (mContext as DashboardActivity).loadFragment(FragType.PerformanceReportFragment, true, "")
            }

            R.id.cv_visit_reports -> {
                (mContext as DashboardActivity).isVisitReportFromDrawer = false
                (mContext as DashboardActivity).loadFragment(FragType.VisitReportFragment, true, "")
            }

            R.id.cv_team_details -> {
                if (Pref.isOfflineTeam)
                    (mContext as DashboardActivity).loadFragment(FragType.OfflineMemberListFragment, true, Pref.user_id!!)
                else
                    (mContext as DashboardActivity).loadFragment(FragType.MemberListFragment, true, Pref.user_id!!)
            }

            R.id.cv_timeline -> {
                (mContext as DashboardActivity).isMemberMap = true
                if (!Pref.willTimelineWithFixedLocationShow)
                    (mContext as DashboardActivity).loadFragment(FragType.OrderhistoryFragment, true, "")
                else
                    (mContext as DashboardActivity).loadFragment(FragType.TimeLineFragment, true, "")
            }

            R.id.cv_party_details -> {
                (mContext as DashboardActivity).isShopFromChatBot = true
                (mContext as DashboardActivity).loadFragment(FragType.NearByShopsListFragment, true, "")
            }

            R.id.cv_order_details -> {
                (mContext as DashboardActivity).isOrderFromChatBot = true
                (mContext as DashboardActivity).loadFragment(FragType.NewOrderListFragment, true, "")
            }

            R.id.cv_visit_duration -> {
                (mContext as DashboardActivity).loadFragment(FragType.AvgTimespentShopListFragment, true, "")
            }

            R.id.cv_total_visit -> {
                (mContext as DashboardActivity).loadFragment(FragType.AverageShopFragment, true, "")
            }

            R.id.cv_collection -> {
                (mContext as DashboardActivity).isCollectionStatusFromDrawer = false
                (mContext as DashboardActivity).loadFragment(FragType.CollectionDetailsStatusFragment, true, "")
            }

            R.id.cv_map -> {
                (mContext as DashboardActivity).isMapFromDrawer = false
                (mContext as DashboardActivity).loadFragment(FragType.NearByShopsMapFragment, true, "")
            }

            R.id.cv_quot -> {
                (mContext as DashboardActivity).isBack = true
                (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, "")
            }

            R.id.cv_targ_achv -> {
                (mContext as DashboardActivity).isTargAchvFromDrawer = false
                (mContext as DashboardActivity).loadFragment(FragType.TargetVsAchvFragment, true, "")
            }

            R.id.cv_achv_details -> {
                (mContext as DashboardActivity).isAchvFromDrawer = false
                (mContext as DashboardActivity).loadFragment(FragType.AchievementReportFragment, true, "")
            }
        }
    }
}