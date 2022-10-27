package com.kcteam.features.survey

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.survey.api.SurveyDataProvider
import com.kcteam.widgets.AppCustomTextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SurveyViewFrag: BaseFragment() {
    private lateinit var mContext: Context
    lateinit var progress_wheel: ProgressWheel
    lateinit var addData:FloatingActionButton
    private lateinit var myshop_name_TV: AppCustomTextView
    private lateinit var myshop_addr_TV: AppCustomTextView
    private lateinit var myshop_contact_TV: AppCustomTextView
    private lateinit var shop_IV: ImageView
    private lateinit var rv_survey_product_list: RecyclerView
    private lateinit var noDataTV: AppCustomTextView
    var viewAllsurveyAdapter: SurveyProductAdapter?=null
    var viewList: ArrayList<survey_list> = ArrayList()
    lateinit var simpleDialog: Dialog


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var mAddShopDataObj: AddShopDBModelEntity? = null
        var shop_id:String = ""
        fun getInstance(objects: Any): SurveyViewFrag {
            val fragment = SurveyViewFrag()
            if (!TextUtils.isEmpty(objects.toString())) {
                shop_id=objects.toString()
                mAddShopDataObj = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shop_id)
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_survey_view, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View?) {
        progress_wheel=view?.findViewById(R.id.progress_wheel) as ProgressWheel
        addData= view?.findViewById(R.id.add_new_survey_tv) as FloatingActionButton
        shop_IV =  view?.findViewById(R.id.shop_IV)
        myshop_name_TV = view?.findViewById(R.id.myshop_name_TV)
        myshop_addr_TV = view?.findViewById(R.id.myshop_address_TV)
        myshop_contact_TV = view?.findViewById(R.id.tv_contact_number)
        rv_survey_product_list=view?.findViewById(R.id.rv_survey_product_list)
        noDataTV=view?.findViewById(R.id.no_survey_data_tv)

        if(mAddShopDataObj !=null){
            myshop_name_TV.text= mAddShopDataObj?.shopName
            myshop_addr_TV.text= mAddShopDataObj?.address
            myshop_contact_TV.text="Owner Contact Number: " + mAddShopDataObj?.ownerContactNumber.toString()

            val drawable = TextDrawable.builder()
                .buildRoundRect(mAddShopDataObj?.shopName!!.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

            shop_IV.setImageDrawable(drawable)

            if (!AppUtils.isOnline(mContext)) {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }
            getQuestionData()
        }

        addData.setOnClickListener {
            (mContext as DashboardActivity).loadFragment(FragType.SurveyFrag, true, shop_id)
        }
    }

    private fun adapterSetUp() {
        viewAllsurveyAdapter = SurveyProductAdapter(mContext, viewList, object : SurveyProductAdapter.OnClickListener {
            override fun onView(obj: survey_list) {
                (mContext as DashboardActivity).loadFragment(FragType.SurveyViewDtlsFrag, true, shop_id+"~"+obj.survey_id)
            }

            override fun onShare(obj: ArrayList<survey_list>) {

            }

            override fun onDelete(obj: survey_list) {
                simpleDialog = Dialog(mContext)
                simpleDialog.setCancelable(false)
                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialog.setContentView(R.layout.dialog_yes_no)
                val dialogHeader = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                val dialogBody = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                val btn_no = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                val btn_yes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView

                dialogHeader.text = AppUtils.hiFirstNameText() + "!"
                dialogBody.text = "Do you want to delete this Survey Number "+ obj.survey_id+"?"

                btn_yes.setOnClickListener({ view ->
                    simpleDialog.cancel()
                    deleteSurvey(obj.survey_id!!)
                })
                btn_no.setOnClickListener({ view ->
                    simpleDialog.cancel()
                })
                simpleDialog.show()
            }

        })
        rv_survey_product_list.adapter=viewAllsurveyAdapter
    }


    fun getQuestionData(){
        try{
            val repository = SurveyDataProvider.provideSurveyQ()
            BaseActivity.compositeDisposable.add(
                repository.provideSurveyViewApi(Pref.session_token!!,Pref.user_id!!,shop_id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        progress_wheel.stopSpinning()
                        var response = result as viewsurveyModel
                        if (response.status == NetworkConstant.SUCCESS) {
                            doAsync {
                                if(response.survey_list!=null){
                                    viewList=response.survey_list!!
                                }
                                uiThread {
                                    rv_survey_product_list.visibility=View.VISIBLE
                                    noDataTV.visibility=View.GONE
                                    adapterSetUp()
                                }
                            }
                        }else{
                            rv_survey_product_list.visibility=View.GONE
                            noDataTV.visibility=View.VISIBLE
                        }
                    }, { error ->
                        progress_wheel.stopSpinning()
                        error.printStackTrace()
                        (mContext as DashboardActivity).showSnackMessage("ERROR")
                    })
            )
        }catch (ex:Exception){
            progress_wheel.stopSpinning()
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage("ERROR")
        }
    }

    private fun deleteSurvey(surveyId: String) {
        progress_wheel.spin()
        try{
            val repository = SurveyDataProvider.provideSurveyQMultiP()
            BaseActivity.compositeDisposable.add(
                repository.provideSurveyDelApi(Pref.session_token!!,Pref.user_id!!,surveyId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        progress_wheel.stopSpinning()
                        var response = result as BaseResponse
                        if (response.status == NetworkConstant.SUCCESS) {
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            getQuestionData()
                        }else {
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        }

                    }, { error ->
                        progress_wheel.stopSpinning()
                        error.printStackTrace()
                        (mContext as DashboardActivity).showSnackMessage("ERROR")
                    })
            )
        }catch (ex:Exception){
            progress_wheel.stopSpinning()
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage("ERROR")
        }

    }

   fun updatePage(){
       getQuestionData()
    }

}