package com.kcteam.features.dymanicSection.presentation

import android.content.Context
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.domain.TaskEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.dymanicSection.api.DynamicRepoProvider
import com.kcteam.features.dymanicSection.model.DynamicListDataModel
import com.kcteam.features.dymanicSection.model.DynamicListResponseModel
import com.kcteam.features.task.presentation.EditTaskFragment
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DynamicListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_all_dynamic_list: RecyclerView
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_dynamic_list_main: RelativeLayout
    private lateinit var fab: FloatingActionButton

    private var id = ""

    companion object {
        fun newInstance(id: Any): DynamicListFragment {
            val fragment = DynamicListFragment()

            if (id is String) {
                val bundle = Bundle()
                bundle.putString("id", id)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        id = arguments?.getString("id").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_dynamic_list, container, false)

        initView(view)
        getDynamicList()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            rv_all_dynamic_list = findViewById(R.id.rv_all_dynamic_list)
            tv_no_data = findViewById(R.id.tv_no_data)
            progress_wheel = findViewById(R.id.progress_wheel)
            rl_dynamic_list_main = findViewById(R.id.rl_dynamic_list_main)
            fab = findViewById(R.id.fab)
        }
        progress_wheel.stopSpinning()
        rv_all_dynamic_list.layoutManager = LinearLayoutManager(mContext)
        rl_dynamic_list_main.setOnClickListener(null)
        fab.setOnClickListener {
            (mContext as DashboardActivity).loadFragment(FragType.AddDynamicFragment, true, id)
        }
    }

    private fun getDynamicList() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            tv_no_data.visibility = View.VISIBLE
            return
        }

        val repository = DynamicRepoProvider.dynamicRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getDynamicListData(id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            progress_wheel.stopSpinning()

                            val response = result as DynamicListResponseModel

                            XLog.d("DYNAMIC ALL LIST RESPONSE=======> " + response.status)

                            if (response.status == NetworkConstant.SUCCESS) {
                                if (response.info_list != null && response.info_list!!.size > 0) {
                                    tv_no_data.visibility = View.GONE
                                    initAdapter(response.info_list)

                                } else {
                                    tv_no_data.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            } else {
                                tv_no_data.visibility = View.VISIBLE
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            tv_no_data.visibility = View.VISIBLE
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            XLog.d("DYNAMIC ALL LIST ERROR=======> " + error.localizedMessage)
                        })
        )
    }

    private fun initAdapter(infoList: ArrayList<DynamicListDataModel>?) {
        rv_all_dynamic_list.adapter = DynamicListAdapter(mContext, infoList) {
            (mContext as DashboardActivity).loadFragment(FragType.EditDynamicFragment, true, it)
        }
    }

    fun updateList() {
        getDynamicList()
    }
}