package com.kcteam.features.dailyPlan.prsentation

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.elvishew.xlog.XLog
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dailyPlan.api.PlanRepoProvider
import com.kcteam.features.dailyPlan.model.GetPlanDetailsDataModel
import com.kcteam.features.dailyPlan.model.GetPlanDetailsResponseModel
import com.kcteam.features.dailyPlan.model.GetPlanListDataModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Saikat on 23-12-2019.
 */
class PlanDetailsFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_plan_details_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_shop_name: AppCustomTextView
    private lateinit var tv_shop_address: AppCustomTextView
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var iv_shop_initial: ImageView
    private lateinit var rl_view_all_plan_main: RelativeLayout

    private var getPlanListDataModel: GetPlanListDataModel? = null

    companion object {

        fun newInstance(objects: Any): PlanDetailsFragment {
            val fragment = PlanDetailsFragment()

            if (objects is GetPlanListDataModel) {
                val bundle = Bundle()
                bundle.putSerializable("get_plan_list", objects)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getPlanListDataModel = arguments?.getSerializable("get_plan_list") as GetPlanListDataModel?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_daily_plan_details, container, false)
        initView(view)
        getPlanDetails()
        return view
    }

    private fun initView(view: View) {
        rv_plan_details_list = view.findViewById(R.id.rv_plan_details_list)
        rv_plan_details_list.layoutManager = LinearLayoutManager(mContext)

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        tv_shop_name = view.findViewById(R.id.tv_shop_name)
        tv_shop_address = view.findViewById(R.id.tv_shop_address)
        tv_no_data = view.findViewById(R.id.tv_no_data)
        iv_shop_initial = view.findViewById(R.id.iv_shop_initial)

        rl_view_all_plan_main = view.findViewById(R.id.rl_view_all_plan_main)
        rl_view_all_plan_main.setOnClickListener(null)

        tv_shop_name.text = getPlanListDataModel?.party_name
        tv_shop_address.text = getPlanListDataModel?.location

        val drawable = TextDrawable.builder()
                .buildRoundRect(getPlanListDataModel?.party_name?.trim()?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)

        iv_shop_initial.setImageDrawable(drawable)
    }

    private fun getPlanDetails() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            tv_no_data.visibility = View.VISIBLE
            return
        }

        val repository = PlanRepoProvider.planListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getPlanDetails(getPlanListDataModel?.plan_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            progress_wheel.stopSpinning()

                            val planDetailsResponse = result as GetPlanDetailsResponseModel

                            if (planDetailsResponse.status == NetworkConstant.SUCCESS) {
                                if (planDetailsResponse.plan_data_details != null && planDetailsResponse.plan_data_details!!.size > 0) {
                                    tv_no_data.visibility = View.GONE

                                    initAdapter(planDetailsResponse.plan_data_details!!)

                                } else {
                                    tv_no_data.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(planDetailsResponse.message!!)
                                }
                            } else {
                                tv_no_data.visibility = View.VISIBLE
                                (mContext as DashboardActivity).showSnackMessage(planDetailsResponse.message!!)
                            }

                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            tv_no_data.visibility = View.VISIBLE
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            XLog.d("DailyPlanDetails ERROR: " + error.localizedMessage)
                        })
        )
    }

    private fun initAdapter(planDataDetails: ArrayList<GetPlanDetailsDataModel>) {
        rv_plan_details_list.adapter = DailyPlanDetailsAdapter(mContext, planDataDetails)
    }
}