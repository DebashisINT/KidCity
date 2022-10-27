package com.kcteam.features.member.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.MemberEntity
import com.kcteam.app.domain.PjpListEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.dashboard.presentation.DashboardFragment
import com.kcteam.features.member.api.TeamRepoProvider
import com.kcteam.features.member.model.TeamListDataModel
import com.kcteam.features.member.model.TeamPjpDataModel
import com.kcteam.features.member.model.TeamPjpResponseModel
import com.kcteam.features.member.model.UserPjpResponseModel
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pnikosis.materialishprogress.ProgressWheel
import com.rackspira.kristiawan.rackmonthpicker.RackMonthPicker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.DateFormatSymbols
import java.util.*

/**
 * Created by Saikat on 30-Mar-20.
 */
class MemberPJPListFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var rv_pjp_list: RecyclerView
    private lateinit var tv_year: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_month: AppCustomTextView
    private lateinit var ll_month: LinearLayout
    private lateinit var tv_supervisor_name: AppCustomTextView
    private lateinit var fab: FloatingActionButton
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var rl_pjp_list_main: RelativeLayout
    private lateinit var tv_employee_name: AppCustomTextView

    private var userId = ""
    private var isFloatingVisible = false
    private var member: TeamListDataModel? = null

    companion object {

        /*fun newInstance(userId: Any): MemberPJPListFragment {
            val fragment = MemberPJPListFragment()

            if (userId is String) {
                val bundle = Bundle()
                bundle.putString("user_id", userId)
                fragment.arguments = bundle
            }

            return fragment
        }*/

        private var mMember: MemberEntity? = null

        fun newInstance(member: Any): MemberPJPListFragment {
            val fragment = MemberPJPListFragment()

            if (member is TeamListDataModel) {
                val bundle = Bundle()
                bundle.putSerializable("member", member)
                fragment.arguments = bundle
            } else if (member is MemberEntity) {
                mMember = member
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        if (arguments?.getSerializable("member") != null) {
            member = arguments?.getSerializable("member") as TeamListDataModel?
            userId = member?.user_id!! //arguments?.getString("user_id")?.toString()!!
        }
        else if (mMember != null)
            userId = mMember?.user_id!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_pjp_list, container, false)

        initView(view)
        getTeamPjpList()

        return view
    }

    @SuppressLint("RestrictedApi")
    private fun initView(view: View) {
        view.apply {
            rv_pjp_list = findViewById(R.id.rv_pjp_list)
            rv_pjp_list.layoutManager = LinearLayoutManager(mContext)

            progress_wheel = findViewById(R.id.progress_wheel)
            progress_wheel.stopSpinning()

            tv_year = findViewById(R.id.tv_year)
            tv_month = findViewById(R.id.tv_month)
            ll_month = findViewById(R.id.ll_month)
            tv_supervisor_name = findViewById(R.id.tv_supervisor_name)
            fab = findViewById(R.id.fab)
            tv_no_data = findViewById(R.id.tv_no_data)
            rl_pjp_list_main = findViewById(R.id.rl_pjp_list_main)
            tv_employee_name = findViewById(R.id.tv_employee_name)
        }

        tv_employee_name.apply {
            visibility = View.VISIBLE

            if (member != null)
                text = member?.user_name
            else if (mMember != null)
                text = mMember?.user_name
        }

        tv_month.text = AppUtils.getCurrentMonth()
        tv_year.text = AppUtils.getCurrentYear()

        fab.also {
            if (Pref.isAllowPJPUpdateForTeam) {
                it.visibility = View.VISIBLE
                isFloatingVisible = true
            } else {
                if (userId == Pref.user_id) {
                    it.visibility = View.VISIBLE
                    isFloatingVisible = true
                } else {
                    it.visibility = View.GONE
                    isFloatingVisible = false
                }
            }

            if (isFloatingVisible) {
                rv_pjp_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy < 0 && !it.isShown)
                            it.show()
                        else if (dy > 0 && it.isShown)
                            it.hide()
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView!!, newState)
                    }
                })
            }

            it.setOnClickListener(this)
        }
        ll_month.setOnClickListener(this)
        rl_pjp_list_main.setOnClickListener(null)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> {
                (mContext as DashboardActivity).loadFragment(FragType.AddPJPFragment, true, userId)
            }

            R.id.ll_month -> {
                callMonthPicker()
            }
        }
    }

    private fun callMonthPicker() {
        RackMonthPicker(mContext)
                .setLocale(Locale.ENGLISH)
                .setColorTheme(R.color.colorPrimary)
                .setSelectedMonth(AppUtils.getCurrentMonthInNum().toInt() - 1)
                .setSelectedYear(AppUtils.getCurrentYear().toInt())
                .setPositiveButton({ month, startDate, endDate, year, monthLabel ->
                    tv_month.text = getMonth(month)
                    tv_year.text = year.toString()
                    getTeamPjpList()
                })
                .setNegativeButton { dialog ->
                    dialog.dismiss()
                }.show()
    }

    private fun getMonth(month: Int): String {
        return DateFormatSymbols().months[month - 1]
    }

    private fun getTeamPjpList() {

        if (!AppUtils.isOnline(mContext)) {
            tv_no_data.visibility = View.VISIBLE
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val month = tv_month.text.toString().trim().substring(0, 3)

        progress_wheel.spin()
        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.teamPjpList(userId, tv_year.text.toString().trim(), AppUtils.getMonthValue(month))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TeamPjpResponseModel
                            XLog.d("GET TEAM PJP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                tv_supervisor_name.text = response.supervisor_name

                                if (response.pjp_list != null && response.pjp_list.size > 0) {
                                    initAdapter(response.pjp_list)
                                } else {
                                    rv_pjp_list.visibility = View.GONE
                                    tv_no_data.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            } else {
                                tv_no_data.visibility = View.VISIBLE
                                rv_pjp_list.visibility = View.GONE
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET TEAM PJP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            tv_no_data.visibility = View.VISIBLE
                            rv_pjp_list.visibility = View.GONE
                        })
        )
    }

    private fun initAdapter(pjp_list: ArrayList<TeamPjpDataModel>?) {
        tv_no_data.visibility = View.GONE
        rv_pjp_list.visibility = View.VISIBLE
        rv_pjp_list.adapter = MemberPJPAdapter(mContext, pjp_list!!, { adapterPosition: Int ->
            (mContext as DashboardActivity).loadFragment(FragType.EditPJPFragment, true, pjp_list[adapterPosition])
        }, { adapterPosition: Int ->
            showDeleterAlert(pjp_list[adapterPosition])
        })

        if (isFloatingVisible) {
            rv_pjp_list.smoothScrollToPosition(0)
            fab.show()
        }
    }

    private fun showDeleterAlert(teamPjpDataModel: TeamPjpDataModel) {
        CommonDialog.getInstance("Delete Alert", "Do you really want to delete this Permanent Journey Plan?", getString(R.string.cancel), getString(R.string.ok), object : CommonDialogClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String) {
                deletePjp(userId, teamPjpDataModel.id)
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun deletePjp(userId: String, id: String) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.deletePjp(userId, id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("DELETE PJP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)

                            if (response.status == NetworkConstant.SUCCESS) {
                                CustomStatic.IsPJPAddEdited=true
                                getPjpListApi()
                                //getTeamPjpList()
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("DELETE PJP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }


    private fun getPjpListApi() {
        progress_wheel.spin()
        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.getUserPJPList(AppUtils.getCurrentDateForShopActi())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as UserPjpResponseModel
                            XLog.d("GET USER PJP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {
                                if (response.pjp_list != null && response.pjp_list.isNotEmpty()) {
                                    doAsync {
                                        AppDatabase.getDBInstance()?.pjpListDao()?.deleteAll()
                                        response.pjp_list.forEach {
                                            val pjpEntity = PjpListEntity()
                                            AppDatabase.getDBInstance()?.pjpListDao()?.insert(pjpEntity.apply {
                                                pjp_id = it.id
                                                from_time = it.from_time
                                                to_time = it.to_time
                                                customer_name = it.customer_name
                                                customer_id = it.customer_id
                                                location = it.location
                                                date = it.date
                                                remarks = it.remarks
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getTeamPjpList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getTeamPjpList()
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                AppDatabase.getDBInstance()?.pjpListDao()?.deleteAll()
                                getTeamPjpList()
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET USER PJP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            getTeamPjpList()
                        })
        )
    }

    fun updateList() {
        tv_month.text = AppUtils.getCurrentMonth()
        tv_year.text = AppUtils.getCurrentYear()

        Handler().postDelayed(Runnable {
            getTeamPjpList()
        }, 150)

    }
}