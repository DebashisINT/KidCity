package com.kcteam.features.nearbyshops.presentation

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.SearchListener
import com.kcteam.app.domain.BeatEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.typeList.TypeListRepoProvider
import com.kcteam.features.addshop.model.BeatListResponseModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.google.android.gms.maps.model.Dash
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class BeatListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var tv_beat_count: AppCustomTextView
    private lateinit var rv_beat_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var no_beat_tv: AppCustomTextView

    private val beatAdapter: BeatAdapter by lazy {
        BeatAdapter(mContext) {
            (mContext as DashboardActivity).isShopFromChatBot = true
            (mContext as DashboardActivity).loadFragment(FragType.NearByShopsListFragment, true, it.beat_id!!)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_beat_list, container, false)

        initView(view)
        initSearchListener()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            no_beat_tv = findViewById(R.id.no_beat_tv)
            tv_beat_count = findViewById(R.id.tv_beat_count)
            progress_wheel = findViewById(R.id.progress_wheel)
            rv_beat_list = findViewById(R.id.rv_beat_list)
        }
        progress_wheel.stopSpinning()

        rv_beat_list.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = beatAdapter
        }

        val list = AppDatabase.getDBInstance()!!.beatDao().getAll()
        if (list != null && list.isNotEmpty()) {
            beatAdapter.updateAdapter(list as ArrayList<BeatEntity>)
            no_beat_tv.visibility = View.GONE
            tv_beat_count.text = "Total Beat(s): ${list.size}"
        }
        else
            callBeatListApi()
    }

    private fun initSearchListener() {
        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                if (query.isBlank()) {
                    val list = AppDatabase.getDBInstance()!!.beatDao().getAll()

                    if (list != null && list.isNotEmpty()) {
                        no_beat_tv.visibility = View.GONE
                        tv_beat_count.text = "Total Beat(s): ${list.size}"
                        beatAdapter.updateAdapter(list as ArrayList<BeatEntity>)
                    }
                    else {
                        no_beat_tv.visibility = View.VISIBLE
                        tv_beat_count.text = "Total Beat(s): 0"
                        beatAdapter.updateAdapter(ArrayList())
                    }
                } else {
                    val searchedList = AppDatabase.getDBInstance()!!.beatDao().getBeatBySearchData(query)

                    if (searchedList != null && searchedList.isNotEmpty()) {
                        no_beat_tv.visibility = View.GONE
                        tv_beat_count.text = "Total Beat(s): ${searchedList.size}"
                        beatAdapter.updateAdapter(searchedList as ArrayList<BeatEntity>)
                    }
                    else {
                        no_beat_tv.visibility = View.VISIBLE
                        tv_beat_count.text = "Total Beat(s): 0"
                        beatAdapter.updateAdapter(ArrayList())
                    }
                }
            }
        })
    }

    private fun callBeatListApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.beatList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            val response = result as BeatListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.beat_list
                                doAsync {
                                    list?.forEach {
                                        val beat = BeatEntity()
                                        AppDatabase.getDBInstance()?.beatDao()?.insert(beat.apply {
                                            beat_id = it.id
                                            name = it.name
                                        })
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        no_beat_tv.visibility = View.GONE
                                        tv_beat_count.text = "Total Beat(s): ${list?.size}"
                                        beatAdapter.updateAdapter(AppDatabase.getDBInstance()?.beatDao()?.getAll() as ArrayList<BeatEntity>)
                                    }
                                }
                            }
                            else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    fun update() {
        initSearchListener()
    }
}