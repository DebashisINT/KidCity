package com.kcteam.features.newcollection

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.content.FileProvider
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.newcollection.model.CollectionDetailsResponseModel
import com.kcteam.features.newcollection.model.NewCollectionListResponseModel
import com.kcteam.features.newcollection.newcollectionlistapi.NewCollectionListRepoProvider
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.ArrayList
import kotlin.math.roundToInt

class CollectionDetailsStatusFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var tv_total_pending: AppCustomTextView
    private lateinit var tv_total_paid: AppCustomTextView
    private lateinit var pb_total_collection: ProgressBar
    private lateinit var tv_total_percentage: AppCustomTextView
    private lateinit var tv_today_pending: AppCustomTextView
    private lateinit var tv_today_paid: AppCustomTextView
    private lateinit var tv_today_percentage: AppCustomTextView
    private lateinit var pb_today_collection: ProgressBar
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var cv_total_collection: CardView
    private lateinit var cv_today_collection: CardView
    private lateinit var rl_collection_status_main: RelativeLayout
    private lateinit var iv_share: AppCompatImageView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_collection_details, container, false)

        initView(view)
        initClickListener()
        getDetails()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            tv_total_pending = findViewById(R.id.tv_total_pending)
            tv_total_paid = findViewById(R.id.tv_total_paid)
            pb_total_collection = findViewById(R.id.pb_total_collection)
            tv_total_percentage = findViewById(R.id.tv_total_percentage)
            tv_today_pending = findViewById(R.id.tv_today_pending)
            tv_today_paid = findViewById(R.id.tv_today_paid)
            tv_today_percentage = findViewById(R.id.tv_today_percentage)
            pb_today_collection = findViewById(R.id.pb_today_collection)
            progress_wheel = findViewById(R.id.progress_wheel)
            cv_today_collection = findViewById(R.id.cv_today_collection)
            cv_total_collection = findViewById(R.id.cv_total_collection)
            rl_collection_status_main = findViewById(R.id.rl_collection_status_main)
            iv_share = findViewById(R.id.iv_share)
        }
        progress_wheel.stopSpinning()
    }

    private fun initClickListener() {
        cv_today_collection.setOnClickListener(this)
        cv_total_collection.setOnClickListener(this)
        rl_collection_status_main.setOnClickListener(null)
        iv_share.setOnClickListener(this)
    }

    private fun getDetails() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = NewCollectionListRepoProvider.newCollectionListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.collectionDetails()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as CollectionDetailsResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (!TextUtils.isEmpty(response.total_paid))
                                    tv_total_paid.text = response.total_paid

                                if (!TextUtils.isEmpty(response.total_pending))
                                    tv_total_pending.text = response.total_pending

                                if (!TextUtils.isEmpty(response.today_pending))
                                    tv_today_pending.text = response.today_pending

                                if (!TextUtils.isEmpty(response.today_paid))
                                    tv_today_paid.text = response.today_paid

                                val total_percentage = ((tv_total_pending.text.toString().trim().toDouble() -
                                        tv_total_paid.text.toString().trim().toDouble())) / 100
                                tv_total_percentage.text = String.format("%.2f", total_percentage) + "%"
                                pb_total_collection.progress = total_percentage.roundToInt()

                                val today_percentage = ((tv_today_pending.text.toString().trim().toDouble() -
                                        tv_today_paid.text.toString().trim().toDouble())) / 100
                                tv_today_percentage.text = String.format("%.2f", today_percentage) + "%"
                                pb_today_collection.progress = today_percentage.roundToInt()
                            }
                            else
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.cv_total_collection -> {
                (mContext as DashboardActivity).loadFragment(FragType.CollectionShopListFragment, true, false)
            }

            R.id.cv_today_collection -> {
                (mContext as DashboardActivity).loadFragment(FragType.CollectionShopListFragment, true, true)
            }

            R.id.iv_share -> {
                val path = FTStorageUtils.bitmapToPdf(mContext, "Collection_" + Pref.user_id + ".pdf", FTStorageUtils.screenShot((mContext as DashboardActivity).window.decorView.rootView))
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
}