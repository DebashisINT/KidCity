package com.kcteam.features.myjobs.presentation

import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.TaskEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.myjobs.api.MyJobRepoProvider
import com.kcteam.features.myjobs.model.CustomerDataModel
import com.kcteam.features.myjobs.model.CustomerListResponseModel
import com.kcteam.features.myjobs.model.CustomerStatusResponseModel
import com.kcteam.features.task.presentation.EditTaskFragment
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class JobsCustomerFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var shop_IV: ImageView
    private lateinit var iv_sms: AppCompatImageView
    private lateinit var iv_whatsapp: AppCompatImageView
    private lateinit var share_icon: AppCompatImageView
    private lateinit var tv_cust_name: AppCustomTextView
    private lateinit var tv_cust_address: AppCustomTextView
    private lateinit var tv_shop_contact_no: AppCustomTextView
    private lateinit var tv_service_for: AppCustomTextView
    private lateinit var tv_total_service: AppCustomTextView
    private lateinit var tv_service_frequency: AppCustomTextView
    private lateinit var tv_total_service_committed: AppCustomTextView
    private lateinit var tv_last_service_committed: AppCustomTextView
    private lateinit var tv_total_service_pending: AppCustomTextView
    private lateinit var fl_job_cust_main: FrameLayout
    private lateinit var ll_service: LinearLayout
    private lateinit var tv_update_status: AppCustomTextView
    private lateinit var ll_work_update: LinearLayout
    private lateinit var tv_wip: AppCustomTextView
    private lateinit var tv_woh: AppCustomTextView
    private lateinit var tv_work_completed: AppCustomTextView
    private lateinit var tv_work_cancelled: AppCustomTextView
    private lateinit var tv_update_review: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_last_status: AppCustomTextView
    private lateinit var tv_job_code: AppCustomTextView
    private lateinit var history_icon: AppCompatImageView

    var isUpdateStatusClicked = false
    var customerdata: CustomerDataModel? = null

    companion object {
        fun newInstance(mcustomerdata: Any): JobsCustomerFragment {
            val fragment = JobsCustomerFragment()

            if (mcustomerdata is CustomerDataModel) {
                val bundle = Bundle()
                bundle.putSerializable("customer", mcustomerdata)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        customerdata = arguments?.getSerializable("customer") as CustomerDataModel?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_job_cust_details_item, container, false)

        initView(view)
        initClickListener()

        getStatusApi()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            shop_IV = findViewById(R.id.shop_IV)
            iv_sms = findViewById(R.id.iv_sms)
            iv_whatsapp = findViewById(R.id.iv_whatsapp)
            share_icon = findViewById(R.id.share_icon)
            tv_cust_name = findViewById(R.id.tv_cust_name)
            tv_cust_address = findViewById(R.id.tv_cust_address)
            tv_shop_contact_no = findViewById(R.id.tv_shop_contact_no)
            tv_service_for = findViewById(R.id.tv_service_for)
            tv_total_service = findViewById(R.id.tv_total_service)
            tv_service_frequency = findViewById(R.id.tv_service_frequency)
            tv_total_service_committed = findViewById(R.id.tv_total_service_committed)
            tv_last_service_committed = findViewById(R.id.tv_last_service_committed)
            tv_total_service_pending = findViewById(R.id.tv_total_service_pending)
            fl_job_cust_main = findViewById(R.id.fl_job_cust_main)
            ll_service = findViewById(R.id.ll_service)
            tv_update_status = findViewById(R.id.tv_update_status)
            ll_work_update = findViewById(R.id.ll_work_update)
            tv_wip = findViewById(R.id.tv_wip)
            tv_woh = findViewById(R.id.tv_woh)
            tv_work_completed = findViewById(R.id.tv_work_completed)
            tv_work_cancelled = findViewById(R.id.tv_work_cancelled)
            tv_update_review = findViewById(R.id.tv_update_review)
            progress_wheel = findViewById(R.id.progress_wheel)
            tv_last_status = findViewById(R.id.tv_last_status)
            tv_job_code = findViewById(R.id.tv_job_code)
            history_icon = findViewById(R.id.history_icon)
        }

        customerdata?.apply {
            tv_cust_address.text = address
            tv_shop_contact_no.text = contact_no
            tv_cust_name.text = name

            val drawable = TextDrawable.builder()
                    .buildRoundRect(name.trim().toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

            shop_IV.setImageDrawable(drawable)

            tv_job_code.text = job_code
            tv_service_for.text = service_for
            tv_total_service.text = total_service
            tv_service_frequency.text = service_frequency
            tv_total_service_committed.text = total_service_commited
            tv_last_service_committed.text = last_service_committed
            tv_total_service_pending.text = total_service_pending

            if (isShowUpdateStatus) {
                if (date > AppUtils.getCurrentDateForShopActi())
                    tv_update_status.visibility = View.GONE
                else
                    tv_update_status.visibility = View.VISIBLE
            }
            else
                tv_update_status.visibility = View.GONE
        }


        if(Pref.IsMyJobFromTeam){
            tv_update_status.visibility=View.GONE
        }
    }

    private fun initClickListener() {
        fl_job_cust_main.setOnClickListener(null)
        iv_sms.setOnClickListener(this)
        iv_whatsapp.setOnClickListener(this)
        share_icon.setOnClickListener(this)
        tv_update_status.setOnClickListener(this)
        tv_wip.setOnClickListener(this)
        tv_woh.setOnClickListener(this)
        tv_work_completed.setOnClickListener(this)
        tv_work_cancelled.setOnClickListener(this)
        tv_update_review.setOnClickListener(this)
        history_icon.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_sms -> {

            }

            R.id.iv_whatsapp -> {

            }

            R.id.share_icon -> {

            }

            R.id.history_icon -> {
                (mContext as DashboardActivity).loadFragment(FragType.ServiceHistoryFragment, true, customerdata!!)
            }

            R.id.tv_update_status -> {
                isUpdateStatusClicked = true
                ll_service.visibility = View.GONE
                ll_work_update.visibility = View.VISIBLE
                tv_update_status.visibility = View.GONE
            }

            R.id.tv_wip -> {
                customerdata?.let {
                    (mContext as DashboardActivity).loadFragment(FragType.WorkInProgressFragment, true, it)
                }
            }

            R.id.tv_woh -> {
                (mContext as DashboardActivity).loadFragment(FragType.WorkOnHoldFragment, true, customerdata!!)
            }

            R.id.tv_work_completed -> {
                (mContext as DashboardActivity).loadFragment(FragType.WorkCompletedFragment, true, customerdata!!)
            }

            R.id.tv_work_cancelled -> {
                (mContext as DashboardActivity).loadFragment(FragType.WorkCancelledFragment, true, customerdata!!)
            }

            R.id.tv_update_review -> {
                (mContext as DashboardActivity).loadFragment(FragType.UpdateReviewFragment, true, customerdata!!)
            }
        }
    }

    fun getStatusApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        if(MyJobsFragment.usr_id!!.length==0 || MyJobsFragment.usr_id.equals("")){
            MyJobsFragment.usr_id =Pref.user_id
        }


        progress_wheel.spin()
        val repository = MyJobRepoProvider.jobRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.getStatus(MyJobsFragment.usr_id!!,customerdata?.id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as CustomerStatusResponseModel
                            progress_wheel.stopSpinning()

                            if (response.status == NetworkConstant.SUCCESS) {
                                tv_last_status.text = response.last_status
                                customerdata?.statusCode = response.job_status
                                showButtonUI(response.job_status.toInt())
                            }
                            else
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun showButtonUI(status: Int) {
        when (status) {
            3 -> {
                tv_work_cancelled.isEnabled = false
                tv_work_cancelled.isSelected = true
                tv_wip.isEnabled = false
                tv_wip.isSelected = true
                tv_woh.isEnabled = false
                tv_woh.isSelected = true
                tv_work_completed.isEnabled = false
                tv_work_completed.isSelected = true

                tv_update_review.isEnabled = true
                tv_update_review.isSelected = false
            }
            4 -> {
                tv_work_cancelled.isEnabled = false
                tv_work_cancelled.isSelected = true
                tv_wip.isEnabled = false
                tv_wip.isSelected = true
                tv_woh.isEnabled = false
                tv_woh.isSelected = true
                tv_work_completed.isEnabled = false
                tv_work_completed.isSelected = true

                tv_update_review.isEnabled = true
                tv_update_review.isSelected = false
            }
            5 -> {
                tv_update_review.isEnabled = false
                tv_update_review.isSelected = true
                tv_work_cancelled.isEnabled = false
                tv_work_cancelled.isSelected = true
                tv_wip.isEnabled = false
                tv_wip.isSelected = true
                tv_woh.isEnabled = false
                tv_woh.isSelected = true
                tv_work_completed.isEnabled = false
                tv_work_completed.isSelected = true
            }
            2 -> {
                tv_woh.text = getString(R.string.unhold_service)

                tv_update_review.isEnabled = false
                tv_update_review.isSelected = true
                tv_work_cancelled.isEnabled = false
                tv_work_cancelled.isSelected = true
                tv_wip.isEnabled = false
                tv_wip.isSelected = true
                tv_woh.isEnabled = true
                tv_woh.isSelected = false
                tv_work_completed.isEnabled = false
                tv_work_completed.isSelected = true
            }
            7 -> {
                tv_update_review.isEnabled = true
                tv_update_review.isSelected = false
                tv_work_cancelled.isEnabled = true
                tv_work_cancelled.isSelected = false
                tv_wip.isEnabled = true
                tv_wip.isSelected = false
                tv_woh.isEnabled = true
                tv_woh.isSelected = false
                tv_work_completed.isEnabled = true
                tv_work_completed.isSelected = false

                tv_woh.text = getString(R.string.woh)
            }
        }
    }
}