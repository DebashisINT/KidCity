package com.kcteam.features.shopdetail.presentation

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.*
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.InputFilterDecimal
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.areaList.AreaListRepoProvider
import com.kcteam.features.addshop.api.assignToPPList.AssignToPPListRepoProvider
import com.kcteam.features.addshop.api.assignedToDDList.AssignToDDListRepoProvider
import com.kcteam.features.addshop.api.typeList.TypeListRepoProvider
import com.kcteam.features.addshop.model.*
import com.kcteam.features.addshop.model.assigntoddlist.AssignToDDListResponseModel
import com.kcteam.features.addshop.model.assigntopplist.AssignToPPListResponseModel
import com.kcteam.features.addshop.presentation.*
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.commondialogsinglebtn.CommonDialogSingleBtn
import com.kcteam.features.commondialogsinglebtn.OnDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.dashboard.presentation.api.otpsentapi.OtpSentRepoProvider
import com.kcteam.features.dashboard.presentation.api.otpverifyapi.OtpVerificationRepoProvider
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.login.model.productlistmodel.ModelListResponse
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.nearbyshops.api.ShopListRepositoryProvider
import com.kcteam.features.nearbyshops.model.*
import com.kcteam.features.reimbursement.presentation.FullImageDialog
import com.kcteam.features.shopdetail.presentation.api.EditShopRepoProvider
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.hahnemann.features.commondialog.presentation.CommonDialogTripleBtn
import com.hahnemann.features.commondialog.presentation.CommonTripleDialogClickListener
import com.squareup.picasso.Picasso
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_add_shop.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Created by Pratishruti on 30-10-2017.
 */
class ShopDetailFragment : BaseFragment(), View.OnClickListener {

    var competitorStockAdded:Boolean=false
    var currentStockAdded:Boolean=false

    private lateinit var mContext: Context
    private lateinit var shopName: AppCustomEditText
    private lateinit var agency_name_TV: AppCustomEditText

    private lateinit var shop_name_label_TV: AppCustomTextView

    private lateinit var shopAddress: AppCustomEditText
    private lateinit var shopGSTIN: AppCustomEditText
    private lateinit var shopPancard: AppCustomEditText

    private lateinit var shopPin: AppCustomEditText
    private lateinit var shopOwnerName: AppCustomTextView
    private lateinit var shopContactNumber: AppCustomEditText
    private lateinit var shopOwnerEmail: AppCustomEditText
    private lateinit var shopImage: ImageView
    private lateinit var callShop: RelativeLayout
    private lateinit var sendEmail: RelativeLayout
    private lateinit var order_amt_p_TV: AppCustomTextView
    private lateinit var total_visited_value_TV: AppCustomTextView
    private lateinit var last_visited_date_TV: AppCustomTextView
    private lateinit var address_RL: RelativeLayout
    private lateinit var ownwr_name_TV: AppCustomEditText
    private lateinit var ownwr_dob_TV: AppCustomTextView
    private lateinit var ownwr_ani_TV: AppCustomTextView
    private lateinit var shop_type_TV: AppCustomTextView
    private lateinit var assigned_to_TV: AppCustomTextView
    private lateinit var iv_category_dropdown_icon: ImageView

    private lateinit var popup_image: ImageView
    private lateinit var overlay_rl: FrameLayout
    private lateinit var bg_blurred_iv: ImageView

    private lateinit var themeListPopupWindowAdapter: InflateThemeListPopupWindowAdapter
    private lateinit var floating_fab: FloatingActionMenu
    private lateinit var getFloatingVal: ArrayList<String>
    private var programFab1: FloatingActionButton? = null
    private var programFab2: FloatingActionButton? = null
    private var programFab3: FloatingActionButton? = null
    private var programFab4: FloatingActionButton? = null
    private var programFab6: FloatingActionButton? = null
    private var programFab7: FloatingActionButton? = null
    private lateinit var rl_assigned_to_dd: RelativeLayout
    private lateinit var assigned_to_dd_TV: AppCustomTextView
    private lateinit var rl_assigned_to_pp: RelativeLayout
    private lateinit var assigned_to_pp_TV: AppCustomTextView
    private lateinit var iv_pp_drop_down_icon: ImageView
    private lateinit var iv_dd_dropdown_icon: ImageView
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private lateinit var save_TV: AppCustomTextView
    private var myCalendar = Calendar.getInstance(Locale.ENGLISH)
    private lateinit var ll_verified: LinearLayout
    private lateinit var iv_otp_check: ImageView
    private lateinit var tv_verified: AppCustomTextView
    private lateinit var rl_amount: RelativeLayout
    private lateinit var amount_ET: AppCustomEditText
    private lateinit var tv_area: AppCustomTextView
    private lateinit var iv_area_dropdown: ImageView
    private lateinit var assigned_to_pp_header_TV: AppCustomTextView
    private lateinit var assigned_to_dd_header_tv: AppCustomTextView
    private lateinit var tv_model: AppCustomTextView
    private lateinit var tv_primary_application: AppCustomTextView
    private lateinit var tv_secondary_application: AppCustomTextView
    private lateinit var tv_lead_type: AppCustomTextView
    private lateinit var tv_stage: AppCustomTextView
    private lateinit var tv_funnel_stage: AppCustomTextView
    private lateinit var et_booking_amount: AppCustomEditText
    private lateinit var iv_model_dropdown: ImageView
    private lateinit var iv_primary_application_dropdown: ImageView
    private lateinit var iv_secondary_application_dropdown: ImageView
    private lateinit var iv_lead_type_dropdown: ImageView
    private lateinit var iv_stage_dropdown: ImageView
    private lateinit var iv_funnel_stage_dropdown: ImageView
    private lateinit var ll_customer_view: LinearLayout
    private lateinit var owner_name_RL: RelativeLayout
    private lateinit var owner_contact_no_label_TV: AppCustomTextView
    private lateinit var owner_email_label_TV: AppCustomTextView
    private lateinit var quot_RL: RelativeLayout
    private lateinit var view2: View
    private lateinit var rl_area_main: RelativeLayout
    private lateinit var quot_amt_p_TV: AppCustomTextView
    private lateinit var rl_type: RelativeLayout
    private lateinit var type_TV: AppCustomTextView
    private lateinit var iv_type_dropdown_icon: ImageView
    private lateinit var ll_doc_extra_info: LinearLayout
    private lateinit var ll_extra_info: LinearLayout
    private lateinit var tv_degree_img_link: AppCustomTextView
    private lateinit var et_specialization: AppCustomEditText
    private lateinit var et_patient_count: AppCustomEditText
    private lateinit var et_category: AppCustomEditText
    private lateinit var tv_doc_family_dob: AppCustomTextView
    private lateinit var et_doc_add: AppCustomEditText
    private lateinit var et_doc_pincode: AppCustomEditText
    private lateinit var ll_yes: LinearLayout
    private lateinit var ll_no: LinearLayout
    private lateinit var iv_yes: ImageView
    private lateinit var iv_no: ImageView
    private lateinit var et_remarks: AppCustomEditText
    private lateinit var et_chemist_name: AppCustomEditText
    private lateinit var et_chemist_add: AppCustomEditText
    private lateinit var et_chemist_pincode: AppCustomEditText
    private lateinit var et_assistant_name: AppCustomEditText
    private lateinit var et_assistant_no: AppCustomEditText
    private lateinit var tv_assistant_dob: AppCustomTextView
    private lateinit var tv_assistant_doa: AppCustomTextView
    private lateinit var tv_assistant_family_dob: AppCustomTextView
    private lateinit var view_shop_image: View
    private lateinit var shops_detail_CV: CardView
    private lateinit var et_dir_name_value: AppCustomEditText
    private lateinit var tv_family_dob: AppCustomTextView
    private lateinit var et_person_name_value: AppCustomEditText
    private lateinit var tv_add_dob: AppCustomTextView
    private lateinit var tv_add_doa: AppCustomTextView
    private lateinit var et_person_no_value: AppCustomEditText
    private lateinit var scroll_view: ScrollView
    private lateinit var view_on_map_label_TV: AppCustomTextView
    private lateinit var rl_party_main: RelativeLayout
    private lateinit var tv_party: AppCustomTextView
    private lateinit var iv_party_dropdown: ImageView
    private lateinit var rl_entity_main: RelativeLayout
    private lateinit var tv_entity: AppCustomTextView
    private lateinit var iv_entity_dropdown: ImageView
    private lateinit var rl_retailer: RelativeLayout
    private lateinit var retailer_TV: AppCustomTextView
    private lateinit var iv_retailer_dropdown: ImageView
    private lateinit var rl_dealer: RelativeLayout
    private lateinit var dealer_TV: AppCustomTextView
    private lateinit var iv_dealer_dropdown: ImageView
    private lateinit var rl_beat: RelativeLayout
    private lateinit var beat_TV: AppCustomTextView
    private lateinit var iv_beat_dropdown: ImageView
    private lateinit var rl_assign_to_shop: RelativeLayout
    private lateinit var tv_assign_to_shop_header: AppCustomTextView
    private lateinit var tv_assign_to_shop: AppCustomTextView
    private lateinit var iv_assign_to_shop_dropdown: ImageView

    private lateinit var tv_shoptype_asterisk_mark: TextView
    private lateinit var tv_name_asterisk_mark: TextView
    private lateinit var tv_agency_asterisk_mark: TextView
    private lateinit var tv_address_asterisk_mark: TextView
    private lateinit var tv_pincode_asterisk_mark: TextView
    private lateinit var tv_owner_name_asterisk_mark: TextView
    private lateinit var tv_no_asterisk_mark: TextView
    private lateinit var tv_dd_asterisk_mark: TextView
    private lateinit var tv_pp_asterisk_mark: TextView
    private lateinit var tv_amount_asterisk_mark: TextView
    private lateinit var tv_area_asterisk_mark: TextView
    private lateinit var tv_model_asterisk_mark: TextView
    private lateinit var tv_stage_asterisk_mark: TextView
    private lateinit var tv_dir_name_asterisk_mark: TextView
    private lateinit var tv_family_mem_dob_asterisk_mark: TextView
    private lateinit var tv_key_person_name_asterisk_mark: TextView
    private lateinit var tv_key_person_no_asterisk_mark: TextView
    private lateinit var tv_attachment_asterisk_mark: TextView
    private lateinit var tv_specalization_asterisk_mark: TextView
    private lateinit var tv_patient_asterisk_mark: TextView
    private lateinit var tv_category_asterisk_mark: TextView
    private lateinit var tv_doc_family_mem_dob_asterisk_mark: TextView
    private lateinit var tv_doc_address_asterisk_mark: TextView
    private lateinit var tv_doc_pincode_asterisk_mark: TextView
    private lateinit var tv_chamber_asterisk_mark: TextView
    private lateinit var tv_chemist_name_asterisk_mark: TextView
    private lateinit var tv_chemist_address_asterisk_mark: TextView
    private lateinit var tv_chemist_pincode_asterisk_mark: TextView
    private lateinit var tv_entity_asterisk_mark: TextView
    private lateinit var tv_party_asterisk_mark: TextView
    private lateinit var tv_assign_to_shop_asterisk_mark: TextView
    private lateinit var tv_dealer_asterisk_mark: TextView
    private lateinit var tv_retailer_asterisk_mark: TextView

    private lateinit var quot_amt_TV: AppCustomTextView

    private var type = ""
    private var assignedToDDId = ""
    private val preid: Int = 100
    private var assignedToPPId = ""
    private var areaId = ""
    private var isDOB = ""
    var addShopData = AddShopDBModelEntity()
    private var imagePath = ""
    private var modelId = ""
    private var primaryAppId = ""
    private var secondaryAppId = ""
    private var leadId = ""
    private var stageId = ""
    private var funnelStageId = ""
    private var typeId = ""
    private var isDocDegree = -1
    private var degreeImgLink = ""
    private var isEnabled = false
    private var dob = ""
    private var doa = ""
    private var family_dob = ""
    private var addl_dob = ""
    private var addl_doa = ""
    private var doc_family_dob = ""
    private var assistant_dob = ""
    private var assistant_doa = ""
    private var assistant_family_dob = ""
    private var entityId = ""
    private var partyStatusId = ""
    private var retailerId = ""
    private var dealerId = ""
    private var beatId = ""
    private var assignedToShopId = ""

    private var programFab5: FloatingActionButton? = null

    private lateinit var rl_agencyName: RelativeLayout

    private lateinit var rl_prospect_main: RelativeLayout

    private lateinit var prospect_name: AppCustomTextView

    private lateinit var project_name_TV: AppCustomEditText

    private lateinit var land_contact_no_TV : AppCustomEditText


    private lateinit var rl_projectName : RelativeLayout
    private lateinit var land_shop_RL : RelativeLayout

    private lateinit var  alternate_RL: RelativeLayout
    private lateinit var  whatsappp_RL: RelativeLayout
    private lateinit var alternate_no_TV: AppCustomEditText

    private lateinit var whatsappp_no_TV : AppCustomEditText

    private lateinit var total_visited_RL : RelativeLayout
    private lateinit var view1 : View




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_shop_detail, container, false)
        if (!TextUtils.isEmpty(shopId)) {
            val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(shopId)

            if (shopDetail != null)
                addShopData = shopDetail
        }
        initView(view)
        return view
    }

    companion object {
        private lateinit var shopId: String
        var isOrderEntryPressed:Boolean = false
        fun getInstance(shopId: Any?): ShopDetailFragment {
            val shopDetailFragment = ShopDetailFragment()
            this.shopId = shopId as String
            return shopDetailFragment
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(view: View) {
        view1 = view.findViewById(R.id.view1)
        total_visited_RL = view.findViewById(R.id.total_visited_RL)
        rl_assigned_to_dd = view.findViewById(R.id.rl_assigned_to_dd)
        assigned_to_dd_TV = view.findViewById(R.id.assigned_to_dd_TV)
        rl_assigned_to_pp = view.findViewById(R.id.rl_assigned_to_pp)
        assigned_to_pp_TV = view.findViewById(R.id.assigned_to_pp_TV)
        floating_fab = view.findViewById(R.id.floating_fab)
        callShop = view.findViewById(R.id.call_shop_RL)
        shopImage = view.findViewById(R.id.shop_img_IV)
        sendEmail = view.findViewById(R.id.email_RL)
        overlay_rl = view.findViewById(R.id.overlay_rl)
        bg_blurred_iv = view.findViewById(R.id.bg_blurred_iv)
        order_amt_p_TV = view.findViewById(R.id.order_amt_p_TV)
        total_visited_value_TV = view.findViewById(R.id.total_visited_value_TV)
        last_visited_date_TV = view.findViewById(R.id.last_visited_date_TV)
        address_RL = view.findViewById(R.id.address_RL)
        ownwr_name_TV = view.findViewById(R.id.ownwr_name_TV)
        ownwr_dob_TV = view.findViewById(R.id.ownwr_dob_TV)
        ownwr_ani_TV = view.findViewById(R.id.ownwr_ani_TV)
        shop_type_TV = view.findViewById(R.id.shop_type_TV)
        assigned_to_TV = view.findViewById(R.id.assigned_to_TV)
        iv_category_dropdown_icon = view.findViewById(R.id.iv_category_dropdown_icon)
        iv_pp_drop_down_icon = view.findViewById(R.id.iv_pp_drop_down_icon)
        iv_dd_dropdown_icon = view.findViewById(R.id.iv_dd_dropdown_icon)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        save_TV = view.findViewById(R.id.save_TV)
        ll_verified = view.findViewById(R.id.ll_verified)
        iv_otp_check = view.findViewById(R.id.iv_otp_check)
        tv_verified = view.findViewById(R.id.tv_verified)
        rl_amount = view.findViewById(R.id.rl_amount)
        amount_ET = view.findViewById(R.id.amount_ET)
        tv_area = view.findViewById(R.id.tv_area)
        popup_image = view.findViewById(R.id.popup_image)
        shopName = view.findViewById(R.id.shop_name_TV)
        rl_agencyName = view.findViewById(R.id.rl_agencyName)
        agency_name_TV = view.findViewById(R.id.agency_name_TV)

        shop_name_label_TV = view.findViewById(R.id.shop_name_label_TV)


        shopAddress = view.findViewById(R.id.address_TV)
        shopGSTIN = view.findViewById(R.id.GSTIN_TV)
        shopPancard = view.findViewById(R.id.PAN_TV)
        shopPin = view.findViewById(R.id.pincode_TV)
        shopContactNumber = view.findViewById(R.id.owner_contact_no_TV)
        shopOwnerEmail = view.findViewById(R.id.owner_email_TV)
        iv_area_dropdown = view.findViewById(R.id.iv_area_dropdown)
        assigned_to_dd_header_tv = view.findViewById(R.id.assigned_to_dd_header_tv)
        assigned_to_pp_header_TV = view.findViewById(R.id.assigned_to_pp_header_TV)
        tv_model = view.findViewById(R.id.tv_model)
        tv_primary_application = view.findViewById(R.id.tv_primary_application)
        tv_secondary_application = view.findViewById(R.id.tv_secondary_application)
        tv_lead_type = view.findViewById(R.id.tv_lead_type)
        tv_stage = view.findViewById(R.id.tv_stage)
        tv_funnel_stage = view.findViewById(R.id.tv_funnel_stage)
        et_booking_amount = view.findViewById(R.id.et_booking_amount)
        iv_model_dropdown = view.findViewById(R.id.iv_model_dropdown)
        iv_primary_application_dropdown = view.findViewById(R.id.iv_primary_application_dropdown)
        iv_secondary_application_dropdown = view.findViewById(R.id.iv_secondary_application_dropdown)
        iv_lead_type_dropdown = view.findViewById(R.id.iv_lead_type_dropdown)
        iv_stage_dropdown = view.findViewById(R.id.iv_stage_dropdown)
        iv_funnel_stage_dropdown = view.findViewById(R.id.iv_funnel_stage_dropdown)
        ll_customer_view = view.findViewById(R.id.ll_customer_view)
        owner_name_RL = view.findViewById(R.id.owner_name_RL)
        owner_contact_no_label_TV = view.findViewById(R.id.owner_contact_no_label_TV)
        owner_email_label_TV = view.findViewById(R.id.owner_email_label_TV)
        quot_RL = view.findViewById(R.id.quot_RL)
        view2 = view.findViewById(R.id.view2)
        rl_area_main = view.findViewById(R.id.rl_area_main)
        quot_amt_p_TV = view.findViewById(R.id.quot_amt_p_TV)
        rl_type = view.findViewById(R.id.rl_type)
        type_TV = view.findViewById(R.id.type_TV)
        iv_type_dropdown_icon = view.findViewById(R.id.iv_type_dropdown_icon)
        ll_doc_extra_info = view.findViewById(R.id.ll_doc_extra_info)
        ll_extra_info = view.findViewById(R.id.ll_extra_info)
        tv_degree_img_link = view.findViewById(R.id.tv_degree_img_link)
        et_specialization = view.findViewById(R.id.et_specialization)
        et_patient_count = view.findViewById(R.id.et_patient_count)
        et_category = view.findViewById(R.id.et_category)
        tv_doc_family_dob = view.findViewById(R.id.tv_doc_family_dob)
        et_doc_add = view.findViewById(R.id.et_doc_add)
        et_doc_pincode = view.findViewById(R.id.et_doc_pincode)
        ll_yes = view.findViewById(R.id.ll_yes)
        ll_no = view.findViewById(R.id.ll_no)
        iv_yes = view.findViewById(R.id.iv_yes)
        iv_no = view.findViewById(R.id.iv_no)
        et_remarks = view.findViewById(R.id.et_remarks)
        et_chemist_name = view.findViewById(R.id.et_chemist_name)
        et_chemist_add = view.findViewById(R.id.et_chemist_add)
        et_chemist_pincode = view.findViewById(R.id.et_chemist_pincode)
        et_assistant_name = view.findViewById(R.id.et_assistant_name)
        et_assistant_no = view.findViewById(R.id.et_assistant_no)
        tv_assistant_dob = view.findViewById(R.id.tv_assistant_dob)
        tv_assistant_doa = view.findViewById(R.id.tv_assistant_doa)
        tv_assistant_family_dob = view.findViewById(R.id.tv_assistant_family_dob)
        view_shop_image = view.findViewById(R.id.view_shop_image)
        shops_detail_CV = view.findViewById(R.id.shops_detail_CV)
        et_dir_name_value = view.findViewById(R.id.et_dir_name_value)
        tv_family_dob = view.findViewById(R.id.tv_family_dob)
        et_person_name_value = view.findViewById(R.id.et_person_name_value)
        tv_add_dob = view.findViewById(R.id.tv_add_dob)
        tv_add_doa = view.findViewById(R.id.tv_add_doa)
        et_person_no_value = view.findViewById(R.id.et_person_no_value)
        scroll_view = view.findViewById(R.id.scroll_view)
        view_on_map_label_TV = view.findViewById(R.id.view_on_map_label_TV)
        rl_party_main = view.findViewById(R.id.rl_party_main)
        tv_party = view.findViewById(R.id.tv_party)
        iv_party_dropdown = view.findViewById(R.id.iv_party_dropdown)
        rl_entity_main = view.findViewById(R.id.rl_entity_main)
        tv_entity = view.findViewById(R.id.tv_entity)
        iv_entity_dropdown = view.findViewById(R.id.iv_entity_dropdown)
        rl_retailer = view.findViewById(R.id.rl_retailer)
        retailer_TV = view.findViewById(R.id.retailer_TV)
        iv_retailer_dropdown = view.findViewById(R.id.iv_retailer_dropdown)
        rl_dealer = view.findViewById(R.id.rl_dealer)
        dealer_TV = view.findViewById(R.id.dealer_TV)
        iv_dealer_dropdown = view.findViewById(R.id.iv_dealer_dropdown)
        rl_beat = view.findViewById(R.id.rl_beat)
        beat_TV = view.findViewById(R.id.beat_TV)
        iv_beat_dropdown = view.findViewById(R.id.iv_beat_dropdown)
        rl_assign_to_shop = view.findViewById(R.id.rl_assign_to_shop)
        tv_assign_to_shop_header = view.findViewById(R.id.tv_assign_to_shop_header)
        tv_assign_to_shop = view.findViewById(R.id.tv_assign_to_shop)
        iv_assign_to_shop_dropdown = view.findViewById(R.id.iv_assign_to_shop_dropdown)

        tv_entity_asterisk_mark = view.findViewById(R.id.tv_entity_asterisk_mark)
        tv_party_asterisk_mark = view.findViewById(R.id.tv_party_asterisk_mark)
        tv_shoptype_asterisk_mark = view.findViewById(R.id.tv_shoptype_asterisk_mark)
        tv_name_asterisk_mark = view.findViewById(R.id.tv_name_asterisk_mark)

        tv_agency_asterisk_mark = view.findViewById(R.id.tv_agency_asterisk_mark)
        tv_address_asterisk_mark = view.findViewById(R.id.tv_address_asterisk_mark)
        tv_pincode_asterisk_mark = view.findViewById(R.id.tv_pincode_asterisk_mark)
        tv_owner_name_asterisk_mark = view.findViewById(R.id.tv_owner_name_asterisk_mark)
        tv_no_asterisk_mark = view.findViewById(R.id.tv_no_asterisk_mark)
        tv_dd_asterisk_mark = view.findViewById(R.id.tv_dd_asterisk_mark)
        tv_pp_asterisk_mark = view.findViewById(R.id.tv_pp_asterisk_mark)
        tv_amount_asterisk_mark = view.findViewById(R.id.tv_amount_asterisk_mark)
        tv_area_asterisk_mark = view.findViewById(R.id.tv_area_asterisk_mark)
        tv_model_asterisk_mark = view.findViewById(R.id.tv_model_asterisk_mark)
        tv_stage_asterisk_mark = view.findViewById(R.id.tv_stage_asterisk_mark)
        tv_dir_name_asterisk_mark = view.findViewById(R.id.tv_dir_name_asterisk_mark)
        tv_family_mem_dob_asterisk_mark = view.findViewById(R.id.tv_family_mem_dob_asterisk_mark)
        tv_key_person_name_asterisk_mark = view.findViewById(R.id.tv_key_person_name_asterisk_mark)
        tv_key_person_no_asterisk_mark = view.findViewById(R.id.tv_key_person_no_asterisk_mark)
        tv_attachment_asterisk_mark = view.findViewById(R.id.tv_attachment_asterisk_mark)
        tv_specalization_asterisk_mark = view.findViewById(R.id.tv_specalization_asterisk_mark)
        tv_patient_asterisk_mark = view.findViewById(R.id.tv_patient_asterisk_mark)
        tv_category_asterisk_mark = view.findViewById(R.id.tv_category_asterisk_mark)
        tv_doc_family_mem_dob_asterisk_mark = view.findViewById(R.id.tv_doc_family_mem_dob_asterisk_mark)
        tv_doc_address_asterisk_mark = view.findViewById(R.id.tv_doc_address_asterisk_mark)
        tv_doc_pincode_asterisk_mark = view.findViewById(R.id.tv_doc_pincode_asterisk_mark)
        tv_chamber_asterisk_mark = view.findViewById(R.id.tv_chamber_asterisk_mark)
        tv_chemist_name_asterisk_mark = view.findViewById(R.id.tv_chemist_name_asterisk_mark)
        tv_chemist_address_asterisk_mark = view.findViewById(R.id.tv_chemist_address_asterisk_mark)
        tv_chemist_pincode_asterisk_mark = view.findViewById(R.id.tv_chemist_pincode_asterisk_mark)
        tv_assign_to_shop_asterisk_mark = view.findViewById(R.id.tv_assign_to_shop_asterisk_mark)
        tv_retailer_asterisk_mark = view.findViewById(R.id.tv_retailer_asterisk_mark)
        tv_dealer_asterisk_mark = view.findViewById(R.id.tv_dealer_asterisk_mark)
        rl_prospect_main = view.findViewById(R.id.rl_prospect_main)
        prospect_name = view.findViewById(R.id.prospect_name)
        project_name_TV = view.findViewById(R.id.project_name_TV)

        land_contact_no_TV = view.findViewById(R.id.land_contact_no_TV)

        quot_amt_TV = view.findViewById(R.id.quot_amt_TV)

        rl_projectName = view.findViewById(R.id.rl_frag_shop_dtl_project_name_root)
        land_shop_RL = view.findViewById(R.id.land_shop_RL)

        alternate_RL = view.findViewById(R.id.alternate_RL)
        whatsappp_RL = view.findViewById(R.id.whatsappp_RL)

        alternate_no_TV = view.findViewById(R.id.alternate_no_TV)

        whatsappp_no_TV = view.findViewById(R.id.whatsappp_no_TV)

        shop_name_label_TV.text = "Name"

        /*14-12-2021*/
        if(Pref.IsnewleadtypeforRuby && addShopData.type!!.toInt() == 16){
            shop_name_label_TV.text = "Lead Name"
            rl_agencyName.visibility = View.VISIBLE
            (mContext as DashboardActivity).setTopBarTitle("Lead " + "Details")
             rl_prospect_main.visibility = View.VISIBLE

            try{
                var prosNameByID=""
                var shopActivityListToProsId = AppDatabase.getDBInstance()!!.shopActivityDao().getProsId(addShopData.shop_id) as String
                if(shopActivityListToProsId!=null || !shopActivityListToProsId.equals("")){
                    prosNameByID = AppDatabase.getDBInstance()!!.prosDao().getProsNameByProsId(shopActivityListToProsId)
                }
                prospect_name.text = prosNameByID // select pros name showing
            }catch (ex:Exception){

                prospect_name.text = ""
            }


        }
        else{
            (mContext as DashboardActivity).setTopBarTitle(Pref.shopText + " Details")
        }



        //et_booking_amount.addTextChangedListener(CustomTextWatcher(et_booking_amount, 6, 2))
        et_booking_amount.filters = arrayOf<InputFilter>(InputFilterDecimal(10, 2))

        if (Pref.isCustomerFeatureEnable) {
            ll_customer_view.visibility = View.VISIBLE
            owner_name_RL.visibility = View.GONE
            owner_contact_no_label_TV.text = getString(R.string.contact_number)
            owner_email_label_TV.text = getString(R.string.only_email)
        } else {
            ll_customer_view.visibility = View.GONE
            owner_name_RL.visibility = View.VISIBLE
            owner_contact_no_label_TV.text = getString(R.string.owner_contact_number)
            owner_email_label_TV.text = getString(R.string.owner_email)
        }

        if (!Pref.isQuotationShow) {
            /*quot_RL.visibility = View.VISIBLE
            view2.visibility = View.VISIBLE*/
            quot_amt_TV.text = "Last Order Amt.:"
        } /*else {
            quot_RL.visibility = View.GONE
            view2.visibility = View.GONE
        }*/

        if (Pref.isAreaVisible)
            rl_area_main.visibility = View.VISIBLE
        else
            rl_area_main.visibility = View.GONE

        if (Pref.willShowPartyStatus)
            rl_party_main.visibility = View.VISIBLE
        else
            rl_party_main.visibility = View.GONE

        if (Pref.isShowBeatGroup)
            rl_beat.visibility = View.VISIBLE
        else
            rl_beat.visibility = View.GONE

        callShop.setOnClickListener(this)
        sendEmail.setOnClickListener(this)
        bg_blurred_iv.setOnClickListener(this)
        //shopImage.setOnClickListener(this)
        address_RL.setOnClickListener(this)
        assigned_to_TV.setOnClickListener(this)
        shop_type_TV.setOnClickListener(this)
        assigned_to_dd_TV.setOnClickListener(this)
        assigned_to_pp_TV.setOnClickListener(this)
        save_TV.setOnClickListener(this)
        ownwr_dob_TV.setOnClickListener(this)
        ownwr_ani_TV.setOnClickListener(this)
        tv_area.setOnClickListener(this)
        tv_model.setOnClickListener(this)
        tv_primary_application.setOnClickListener(this)
        tv_secondary_application.setOnClickListener(this)
        tv_lead_type.setOnClickListener(this)
        tv_funnel_stage.setOnClickListener(this)
        iv_model_dropdown.setOnClickListener(this)
        iv_primary_application_dropdown.setOnClickListener(this)
        iv_secondary_application_dropdown.setOnClickListener(this)
        iv_lead_type_dropdown.setOnClickListener(this)
        iv_stage_dropdown.setOnClickListener(this)
        iv_funnel_stage_dropdown.setOnClickListener(this)
        rl_type.setOnClickListener(this)
        tv_add_dob.setOnClickListener(this)
        tv_add_doa.setOnClickListener(this)
        tv_family_dob.setOnClickListener(this)
        ll_yes.setOnClickListener(this)
        ll_no.setOnClickListener(this)
        tv_degree_img_link.setOnClickListener(this)
        tv_doc_family_dob.setOnClickListener(this)
        tv_assistant_dob.setOnClickListener(this)
        tv_assistant_doa.setOnClickListener(this)
        tv_assistant_family_dob.setOnClickListener(this)
        tv_party.setOnClickListener(this)
        iv_party_dropdown.setOnClickListener(this)
        tv_entity.setOnClickListener(this)
        iv_entity_dropdown.setOnClickListener(this)
        retailer_TV.setOnClickListener(this)
        iv_retailer_dropdown.setOnClickListener(this)
        dealer_TV.setOnClickListener(this)
        iv_dealer_dropdown.setOnClickListener(this)
        beat_TV.setOnClickListener(this)
        iv_beat_dropdown.setOnClickListener(this)
        tv_assign_to_shop.setOnClickListener(this)
        iv_assign_to_shop_dropdown.setOnClickListener(this)
        //assigned_to_TV.text = "test1"

        assigned_to_dd_header_tv.text = getString(R.string.assign_to_hint_text) + " " + Pref.ddText
        assigned_to_dd_TV.hint = "Select Assigned to " + Pref.ddText

        tv_assign_to_shop_header.text = getString(R.string.assign_to_hint_text) + " ${Pref.shopText}"
        tv_assign_to_shop.hint = "Select Assigned to ${Pref.shopText}"
        /*assigned_to_pp_header_TV.text = "Assigned to " + Pref.ppText
        assigned_to_pp_TV.hint = "Select Assigned to " + Pref.ppText*/

        /*val userId = shopId.substring(0, shopId.indexOf("_"))
        if (userId != Pref.user_id)
            floating_fab.visibility = View.GONE
        else {
            if (!Pref.isShopAddEditAvailable && !Pref.isOrderShow && !Pref.isCollectioninMenuShow && (!Pref.willStockShow || (!Pref.isStockAvailableForAll && addShopData.type != "4")))
                floating_fab.visibility = View.GONE
            else
                floating_fab.visibility = View.VISIBLE
        }*/


        if (addShopData != null)
            setData(addShopData)

        floating_fab.menuIconView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_add))
        floating_fab.menuButtonColorNormal = mContext.resources.getColor(R.color.colorAccent)
        floating_fab.menuButtonColorPressed = mContext.resources.getColor(R.color.colorPrimaryDark)
        floating_fab.menuButtonColorRipple = mContext.resources.getColor(R.color.colorPrimary)

        floating_fab.isIconAnimated = false
        floating_fab.setClosedOnTouchOutside(true)
        getFloatingVal = ArrayList<String>()

        (mContext as DashboardActivity).shop_type = addShopData.type

        if (Pref.isShopAddEditAvailable) {
            if (Pref.isShopEditEnable){
                if(Pref.IsnewleadtypeforRuby && addShopData.type!!.toInt() == 16){
                    getFloatingVal.add("Update " + "Lead" + " Details")
                }
                else{
                    getFloatingVal.add("Update " + Pref.shopText + " Details")
                }
            }
            else {
            }
        }



        if (addShopData.type != "8") {
            if (Pref.isOrderShow)
                getFloatingVal.add("View / Create Order")

            if (Pref.isCollectioninMenuShow)
                getFloatingVal.add(/*"Enter Collection"*/"View Bill")

            if (Pref.isQuotationShow)
                getFloatingVal.add("View / Create Quotation")

            if (addShopData.type == "7")
                getFloatingVal.add("View / Create Activity")
            else if (Pref.willActivityShow)
                getFloatingVal.add("Create Activity")

            if (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4"))
                getFloatingVal.add("Update Stock")
            /*else if (Pref.isStockAvailableForAll)
            getFloatingVal.add("Take Opening Stock")*/

            if (AppUtils.isFromViewPPDD && (addShopData.type == "2" || addShopData.type == "4")) {
                getFloatingVal.add("View Stock")
                AppUtils.isFromViewPPDD = false
            }
        }
        else {
            //if (Pref.willActivityShow)
                getFloatingVal.add("View / Create Activity")
        }

        ////////////////
        var currentViewSt=AppDatabase.getDBInstance()?.shopTypeStockViewStatusDao()?.getShopCurrentStockViewStatus(addShopData?.type!!)
        var competitorViewSt=AppDatabase.getDBInstance()?.shopTypeStockViewStatusDao()?.getShopCompetitorStockViewStatus(addShopData?.type!!)


        if(AppUtils.getSharedPreferencesCurrentStock(mContext)){
            if(AppUtils.getSharedPreferencesCurrentStockApplicableForAll(mContext)){
                currentStockAdded=true
                getFloatingVal.add("Current Stock")
            }else{
                //if(addShopData?.type?.toInt() == 1 || addShopData?.type?.toInt() == 3){
                if(currentViewSt==1){
                    currentStockAdded=true
                    getFloatingVal.add("Current Stock")
                }
            }
        }
        if(AppUtils.getSharedPreferencesIscompetitorStockRequired(mContext)){
            if(!AppUtils.getSharedPreferencesIsCompetitorStockforParty(mContext)){
                competitorStockAdded=true
                getFloatingVal.add("Competitor Stock")
            }else{
                //if(addShopData?.type?.toInt() == 1 || addShopData?.type?.toInt() == 3){
                if(competitorViewSt==1){
                    competitorStockAdded=true
                    getFloatingVal.add("Competitor Stock")
                }
            }
        }
        //getFloatingVal.add("Current Stock")
        //getFloatingVal.add("Competitor Stock")
        /////////////////////////////



        for (i in getFloatingVal.indices) {
            Log.e("shop details", "Index=========> $i")

            if (addShopData.type == "8") {
                if (Pref.isShopAddEditAvailable && Pref.isShopEditEnable) {
                    if (i == 0) {
                        programFab1 = FloatingActionButton(activity)
                        programFab1?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[0]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if (Pref.willActivityShow) {
                    if (i == 0) {
                        programFab1 = FloatingActionButton(activity)
                        programFab1?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[0]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }

                if (Pref.willActivityShow) {
                    if (i == 1) {
                        programFab2 = FloatingActionButton(activity)
                        programFab2?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[1]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
            }
            else {
                if (Pref.isShopAddEditAvailable && Pref.isShopEditEnable) {
                    if (i == 0) {
                        programFab1 = FloatingActionButton(activity)
                        programFab1?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[0]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if (Pref.isOrderShow) {
                    if (i == 0) {
                        programFab1 = FloatingActionButton(activity)
                        programFab1?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[0]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if (Pref.isCollectioninMenuShow) {
                    if (i == 0) {
                        programFab1 = FloatingActionButton(activity)
                        programFab1?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[0]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if (Pref.isQuotationShow) {
                    if (i == 0) {
                        programFab1 = FloatingActionButton(activity)
                        programFab1?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[0]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if (Pref.willActivityShow) {
                    if (i == 0) {
                        programFab1 = FloatingActionButton(activity)
                        programFab1?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[0]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4"))) {
                    if (i == 0) {
                        programFab1 = FloatingActionButton(activity)
                        programFab1?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[0]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if(currentStockAdded){
                    if(i==0){
                        programFab1 = FloatingActionButton(activity)
                        programFab1?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[0]
                            it.id=701
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                        currentStockAdded=false
                    }
                }
                else if(competitorStockAdded){
                    if(i==0){
                        programFab1 = FloatingActionButton(activity)
                        programFab1?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[0]
                            it.id = 702
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                        competitorStockAdded=false
                    }
                }

                if (Pref.isOrderShow) {
                    if (i == 1) {
                        programFab2 = FloatingActionButton(activity)
                        programFab2?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[1]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if (Pref.isCollectioninMenuShow) {
                    if (i == 1) {
                        programFab2 = FloatingActionButton(activity)
                        programFab2?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[1]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if (Pref.isQuotationShow) {
                    if (i == 1) {
                        programFab2 = FloatingActionButton(activity)
                        programFab2?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[1]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if (Pref.willActivityShow) {
                    if (i == 1) {
                        programFab2 = FloatingActionButton(activity)
                        programFab2?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[1]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4"))) {
                    if (i == 1) {
                        programFab2 = FloatingActionButton(activity)
                        programFab2?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[1]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if(currentStockAdded){
                    if(i==1){
                        programFab2 = FloatingActionButton(activity)
                        programFab2?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[1]
                            it.id =701
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                        currentStockAdded=false
                    }
                }
                else if(competitorStockAdded){
                    if(i==1){
                        programFab2 = FloatingActionButton(activity)
                        programFab2?.let {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[1]
                            it.id =702
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                        competitorStockAdded=false
                    }
                }

                if (Pref.isCollectioninMenuShow) {
                    if (i == 2) {
                        programFab3 = FloatingActionButton(activity)
                        programFab3?.also {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[2]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }

                    }
                }
                else if (Pref.isQuotationShow) {
                    if (i == 2) {
                        programFab3 = FloatingActionButton(activity)
                        programFab3?.also {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[2]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }

                    }
                }
                else if (Pref.willActivityShow) {
                    if (i == 2) {
                        programFab3 = FloatingActionButton(activity)
                        programFab3?.also {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[2]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }

                    }
                }
                else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4"))) {
                    if (i == 2) {
                        programFab3 = FloatingActionButton(activity)
                        programFab3?.also {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[2]
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                    }
                }
                else if(currentStockAdded){
                    if(i==2){
                        programFab3 = FloatingActionButton(activity)
                        programFab3?.also {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[2]
                            it.id=701
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                        currentStockAdded=false
                    }

                }
                else if(competitorStockAdded){
                    if(i==2){
                        programFab3 = FloatingActionButton(activity)
                        programFab3?.also {
                            it.buttonSize = FloatingActionButton.SIZE_MINI
                            it.id = preid + i
                            it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                            it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                            it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                            it.labelText = getFloatingVal[2]
                            it.id=702
                            floating_fab.addMenuButton(it)
                            it.setOnClickListener(this)
                        }
                        competitorStockAdded=false
                    }

                }

                if (Pref.isQuotationShow) {
                    if (i == 3) {
                        programFab4 = FloatingActionButton(activity)
                        programFab4?.buttonSize = FloatingActionButton.SIZE_MINI
                        programFab4?.id = preid + i
                        programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                        programFab4?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab4?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab4?.labelText = getFloatingVal[3]
                        floating_fab.addMenuButton(programFab4)
                        programFab4?.setOnClickListener(this)
                    }
                }
                else if (Pref.willActivityShow) {
                    if (i == 3) {
                        programFab4 = FloatingActionButton(activity)
                        programFab4?.buttonSize = FloatingActionButton.SIZE_MINI
                        programFab4?.id = preid + i
                        programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                        programFab4?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab4?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab4?.labelText = getFloatingVal[3]
                        floating_fab.addMenuButton(programFab4)
                        programFab4?.setOnClickListener(this)
                    }
                }
                else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4"))) {
                    if (i == 3) {
                        programFab4 = FloatingActionButton(activity)
                        programFab4?.buttonSize = FloatingActionButton.SIZE_MINI
                        programFab4?.id = preid + i
                        programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                        programFab4?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab4?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab4?.labelText = getFloatingVal[3]
                        floating_fab.addMenuButton(programFab4)
                        programFab4?.setOnClickListener(this)
                    }
                }
                else if(currentStockAdded){
                    if(i==3){
                        programFab4 = FloatingActionButton(activity)
                        programFab4?.buttonSize = FloatingActionButton.SIZE_MINI
                        programFab4?.id = preid + i
                        programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                        programFab4?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab4?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab4?.labelText = getFloatingVal[3]
                        programFab4?.id = 701
                        floating_fab.addMenuButton(programFab4)
                        programFab4?.setOnClickListener(this)
                        currentStockAdded=false
                    }

                }
                else if(competitorStockAdded){
                    if(i==3){
                        programFab4 = FloatingActionButton(activity)
                        programFab4?.buttonSize = FloatingActionButton.SIZE_MINI
                        programFab4?.id = preid + i
                        programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                        programFab4?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab4?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab4?.labelText = getFloatingVal[3]
                        programFab4?.id = 702
                        floating_fab.addMenuButton(programFab4)
                        programFab4?.setOnClickListener(this)
                        competitorStockAdded=false
                    }

                }


                if (Pref.willActivityShow) {
                    if (i == 4) {
                        programFab5 = FloatingActionButton(activity)
                        programFab5?.buttonSize = FloatingActionButton.SIZE_MINI
                        programFab5?.id = preid + i
                        programFab5?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                        programFab5?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab5?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab5?.labelText = getFloatingVal[4]
                        if(programFab5?.labelText.equals("Current Stock")){
                            programFab5?.id=701
                        }
                        if(programFab5?.labelText.equals("Competitor Stock")){
                            programFab5?.id=702
                        }
                        floating_fab.addMenuButton(programFab5)
                        programFab5?.setOnClickListener(this)
                    }
                }
                else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4"))) {
                    if (i == 4) {
                        programFab5 = FloatingActionButton(activity)
                        programFab5?.buttonSize = FloatingActionButton.SIZE_MINI
                        programFab5?.id = preid + i
                        programFab5?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                        programFab5?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab5?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab5?.labelText = getFloatingVal[4]
                        if(programFab5?.labelText.equals("Current Stock")){
                            programFab5?.id=701
                        }
                        if(programFab5?.labelText.equals("Competitor Stock")){
                            programFab5?.id=702
                        }
                        floating_fab.addMenuButton(programFab5)
                        programFab5?.setOnClickListener(this)
                    }
                }
                else if(currentStockAdded){
                    if(i==4){
                        programFab5 = FloatingActionButton(activity)
                        programFab5?.buttonSize = FloatingActionButton.SIZE_MINI
                        programFab5?.id = preid + i
                        programFab5?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                        programFab5?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab5?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab5?.labelText = getFloatingVal[4]
                        if(programFab5?.labelText.equals("Current Stock")){
                            programFab5?.id=701
                        }
                        if(programFab5?.labelText.equals("Competitor Stock")){
                            programFab5?.id=702
                        }
                        floating_fab.addMenuButton(programFab5)
                        programFab5?.setOnClickListener(this)
                        currentStockAdded=false
                    }

                }
                else if(competitorStockAdded){
                    if(i==4){
                        programFab5 = FloatingActionButton(activity)
                        programFab5?.buttonSize = FloatingActionButton.SIZE_MINI
                        programFab5?.id = preid + i
                        programFab5?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                        programFab5?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab5?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab5?.labelText = getFloatingVal[4]
                        if(programFab5?.labelText.equals("Current Stock")){
                            programFab5?.id=701
                        }
                        if(programFab5?.labelText.equals("Competitor Stock")){
                            programFab5?.id=702
                        }
                        floating_fab.addMenuButton(programFab5)
                        programFab5?.setOnClickListener(this)
                        competitorStockAdded=false
                    }

                }

                if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4"))) {
                    if (i == 5) {
                        programFab6 = FloatingActionButton(activity)
                        programFab6?.buttonSize = FloatingActionButton.SIZE_MINI
                        programFab6?.id = preid + i
                        programFab6?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                        programFab6?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab6?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                        programFab6?.labelText = getFloatingVal[5]
                        floating_fab.addMenuButton(programFab6)
                        programFab6?.setOnClickListener(this)
                    }
                }
                else if(currentStockAdded && i == 5){
                    programFab6 = FloatingActionButton(activity)
                    programFab6?.buttonSize = FloatingActionButton.SIZE_MINI
                    programFab6?.id = preid + i
                    programFab6?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                    programFab6?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                    programFab6?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                    programFab6?.labelText = getFloatingVal[5]
                    programFab6?.id=701
                    floating_fab.addMenuButton(programFab6)
                    programFab6?.setOnClickListener(this)
                }
                else if(competitorStockAdded && i == 5){
                    programFab6 = FloatingActionButton(activity)
                    programFab6?.buttonSize = FloatingActionButton.SIZE_MINI
                    programFab6?.id = preid + i
                    programFab6?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                    programFab6?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                    programFab6?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                    programFab6?.labelText = getFloatingVal[5]
                    programFab6?.id=702
                    floating_fab.addMenuButton(programFab6)
                    programFab6?.setOnClickListener(this)
                }


                if(currentStockAdded && i == 6){
                    programFab7 = FloatingActionButton(activity)
                    programFab7?.buttonSize = FloatingActionButton.SIZE_MINI
                    programFab7?.id = preid + i
                    programFab7?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                    programFab7?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                    programFab7?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                    programFab7?.labelText = getFloatingVal[6]
                    programFab7?.id=701
                    floating_fab.addMenuButton(programFab7)
                    programFab7?.setOnClickListener(this)
                }
                else if(competitorStockAdded && i == 6){
                    programFab7 = FloatingActionButton(activity)
                    programFab7?.buttonSize = FloatingActionButton.SIZE_MINI
                    programFab7?.id = preid + i
                    programFab7?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                    programFab7?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                    programFab7?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                    programFab7?.labelText = getFloatingVal[6]
                    programFab7?.id=702
                    floating_fab.addMenuButton(programFab7)
                    programFab6?.setOnClickListener(this)
                }


                /*if ((addShopData.type == "2" || addShopData.type == "4") && i == 4) {
                    programFab7 = FloatingActionButton(activity)
                    programFab7?.buttonSize = FloatingActionButton.SIZE_MINI
                    programFab7?.id = preid + i
                    programFab7?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                    programFab7?.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                    programFab7?.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                    programFab7?.labelText = getFloatingVal[4]
                    floating_fab.addMenuButton(programFab7)
                    programFab7?.setOnClickListener(this)
                }*/
            }

            //programFab1.setImageResource(R.drawable.ic_filter);
            if (addShopData.type != "8") {
                if (i == 0 && ((Pref.isShopAddEditAvailable && Pref.isShopEditEnable) || Pref.isOrderShow || Pref.isCollectioninMenuShow || Pref.isQuotationShow || Pref.willActivityShow || (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4") || currentStockAdded || competitorStockAdded))) {
                    programFab1?.setImageResource(R.drawable.ic_tick_float_icon)
                    programFab1?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                } else if (i == 1 && (Pref.isOrderShow || Pref.isCollectioninMenuShow || Pref.isQuotationShow || Pref.willActivityShow || (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4") || currentStockAdded || competitorStockAdded )))
                    programFab2?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                else if (i == 2 && (Pref.isCollectioninMenuShow || Pref.isQuotationShow || Pref.willActivityShow || (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4"))))
                    programFab3?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                else if (i == 3 && (Pref.isQuotationShow || Pref.willActivityShow || (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4") || currentStockAdded || competitorStockAdded )))
                    programFab4?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                else if (i == 4 && (Pref.willActivityShow || (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4") || currentStockAdded || competitorStockAdded)))
                    programFab5?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                else if (i == 5 && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4") || currentStockAdded || competitorStockAdded))
                    programFab6?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                else if (i == 6 && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4") || currentStockAdded || competitorStockAdded))
                    programFab7?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                /*else if ((addShopData.type == "2" || addShopData.type == "4") && i == 4)
                    programFab7?.setImageResource(R.drawable.ic_tick_float_icon_gray)*/
            }
            else {
                if (i == 0 && ((Pref.isShopAddEditAvailable && Pref.isShopEditEnable) || Pref.willActivityShow)) {
                    programFab1?.setImageResource(R.drawable.ic_tick_float_icon)
                    programFab1?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                } else if (i == 1 && Pref.willActivityShow)
                    programFab2?.setImageResource(R.drawable.ic_tick_float_icon_gray)
            }
        }



        if (AppUtils.isRevisit!!) {
            if (shop_type_TV.text.toString().equals("shop", ignoreCase = true)) {

                if (TextUtils.isEmpty(addShopData.assigned_to_pp_id) && TextUtils.isEmpty(addShopData.assigned_to_dd_id)) {
                    /*(mContext as DashboardActivity).showSnackMessage("Please select pp_id")
                    enabledEntry(true)*/
                    showPPDDAlert("Please assign PP & DD")
                } else if (TextUtils.isEmpty(addShopData.assigned_to_dd_id)) {
                    /*(mContext as DashboardActivity).showSnackMessage("Please select dd_id")
                    enabledEntry(true)*/
                    showPPDDAlert("Please assign DD")
                } else if (TextUtils.isEmpty(addShopData.assigned_to_pp_id)) {
                    /*(mContext as DashboardActivity).showSnackMessage("Please select dd_id")
                    enabledEntry(true)*/
                    showPPDDAlert("Please assign PP")
                } else {
                    disabledEntry()
                    showRevisitActionDialog()
                }
            } else if (shop_type_TV.text.toString().equals("distributor", ignoreCase = true)) {

                if (TextUtils.isEmpty(addShopData.assigned_to_pp_id)) {
                    /*(mContext as DashboardActivity).showSnackMessage("Please select pp_id")
                    enabledEntry(true)*/
                    showPPDDAlert("Please assign PP")
                } else {
                    disabledEntry()
                    showRevisitActionDialog()
                }
            } else {
                disabledEntry()
                showRevisitActionDialog()
            }

        } else {
            disabledEntry()

            if (AppUtils.isShopAdded) {
                /*CommonDialogSingleBtn.getInstance("Action", "What you like to do?", "Order Entry", object : OnDialogClickListener {
                    override fun onOkClick() {
                        (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, addShopData)
                        //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                    }
                }).show((mContext as DashboardActivity).supportFragmentManager, "CommonDialogSingleBtn")*/

                if (!Pref.isCustomerFeatureEnable) {
                    if (Pref.isStockAvailableForPopup && Pref.isStockAvailableForAll) {
                        if (Pref.isOrderAvailableForPopup)
                            showActionDialog(1)
                        else
                            showActionDialog(3)
                    } else if (Pref.isStockAvailableForPopup && !Pref.isStockAvailableForAll) {

                        if (addShopData.type == "4" || addShopData.type == "12" || addShopData.type == "13" || addShopData.type == "14" || addShopData.type == "15") {
                            if (Pref.isOrderAvailableForPopup)
                                showActionDialog(1)
                            else
                                showActionDialog(3)
                        } else {
                            if (Pref.isOrderAvailableForPopup)
                                showActionDialog(5)
                            else
                                AppUtils.isShopAdded = false
                        }
                    } else if (!Pref.isStockAvailableForPopup) {
                        if (Pref.isOrderAvailableForPopup)
                            showActionDialog(5)
                        else
                            AppUtils.isShopAdded = false
                    } else
                        AppUtils.isShopAdded = false
                } else {
                    if (Pref.isStockAvailableForPopup && Pref.isStockAvailableForAll) {
                        if (Pref.isQuotationPopupShow)
                            showActionDialog(1)
                        else
                            showActionDialog(3)
                    } else if (Pref.isStockAvailableForPopup && !Pref.isStockAvailableForAll) {

                        if (addShopData.type == "4") {
                            if (Pref.isQuotationPopupShow)
                                showActionDialog(1)
                            else
                                showActionDialog(3)
                        } else {
                            if (Pref.isQuotationPopupShow)
                                showActionDialog(5)
                            else
                                AppUtils.isShopAdded = false
                        }
                    } else if (!Pref.isStockAvailableForPopup) {
                        if (Pref.isQuotationPopupShow)
                            showActionDialog(5)
                        else
                            AppUtils.isShopAdded = false
                    } else
                        AppUtils.isShopAdded = false
                }
            }
        }

        if(!Pref.IsprojectforCustomer){
            rl_projectName.visibility=View.GONE
        }
        if(!Pref.IslandlineforCustomer){
            land_shop_RL.visibility=View.GONE
        }

        if(!Pref.IsAlternateNoForCustomer){
            alternate_RL.visibility=View.GONE
        }
        if(!Pref.IsWhatsappNoForCustomer){
            whatsappp_RL.visibility=View.GONE
        }


    }

    private fun showActionDialog(status: Int) {
        when (status) {
            0 -> {
                var orderText = ""
                orderText = if (Pref.isQuotationPopupShow)
                    "Quot. entry"
                else
                    "Order entry"

                CommonDialogTripleBtn.getInstance(AppUtils.hiFirstNameText()+"!", "Select what would you like to do?", orderText, "Opening stock",
                        false, "Collection entry", object : CommonTripleDialogClickListener {
                    override fun onLeftClick() {
                        if (Pref.isQuotationPopupShow)
                            (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
                        else{
                            AddShopFragment.isOrderEntryPressed=true
                            ShopDetailFragment.isOrderEntryPressed=true
                            AddShopFragment.newShopID=addShopData?.shop_id
                            if(Pref.IsActivateNewOrderScreenwithSize){//13-09-2021
                                (mContext as DashboardActivity).loadFragment(FragType.NewOrderScrOrderDetailsFragment, true, addShopData!!.shop_id)
                            }else {
                                (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, addShopData)
                            }
                        }

                        //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                    }

                    override fun onRightClick() {
                        (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                        //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                    }

                    override fun onMiddleClick() {
                        (mContext as DashboardActivity).loadFragment(FragType.CollectionDetailsFragment, true, addShopData)
                        //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                    }

                    override fun onCancelClick() {
                        if(AppUtils.getSharedPreferenceslogOrderStatusRequired(mContext)){
                            val dialog = Dialog(mContext)
                            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setCancelable(false)
                            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                            dialog.setContentView(R.layout.dialog_cancel_order_status)

                            val user_name=dialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                            val order_status = dialog.findViewById(R.id.tv_cancel_order_status) as AppCustomTextView
                            val cancel_remarks = dialog.findViewById(R.id.et_cancel_order_remarks) as AppCustomEditText
                            val submitRemarks = dialog.findViewById(R.id.tv_cancel_order_submit_remarks) as AppCustomTextView

                            order_status.text="Failure"
                            user_name.text="Hi "+Pref.user_name+"!"

                            submitRemarks.setOnClickListener(View.OnClickListener { view ->
                                if(!TextUtils.isEmpty(cancel_remarks.text.toString().trim())){
                                    //Toast.makeText(mContext,cancel_remarks.text.toString(),Toast.LENGTH_SHORT).show()
                                    val obj=OrderStatusRemarksModelEntity()
                                    obj.shop_id= shopId
                                    obj.user_id=Pref.user_id
                                    obj.order_status=order_status.text.toString()
                                    obj.order_remarks=cancel_remarks!!.text!!.toString()
                                    obj.visited_date_time=AppUtils.getCurrentDateTime()
                                    obj.visited_date=AppUtils.getCurrentDateForShopActi()
                                    obj.isUploaded=false

                                    var shopAll=AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityAll()
                                    if(shopAll.size == 1){
                                        obj.shop_revisit_uniqKey=shopAll.get(0).shop_revisit_uniqKey
                                    }else if(shopAll.size!=0){
                                        obj.shop_revisit_uniqKey=shopAll.get(shopAll.size-1).shop_revisit_uniqKey
                                    }
                                    if(shopAll.size!=0)
                                    AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.insert(obj)
                                    dialog.dismiss()
                                }else{
                                    submitRemarks.setError("Enter Remarks")
                                    submitRemarks.requestFocus()
                                }

                            })
                            dialog.show()
                        }
                    }
                }).show((mContext as DashboardActivity).supportFragmentManager, "")
            }

            1 -> {
                var orderText = ""
                orderText = if (Pref.isQuotationPopupShow)
                    "Quot. entry"
                else
                    "Order entry"

                CommonDialog.getInstanceNew(AppUtils.hiFirstNameText()+"!", "Select what would you like to do?", orderText, "Opening stock", false, object : CommonDialogClickListener {
                    override fun onLeftClick() {
                        if (Pref.isQuotationPopupShow)
                            (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
                        else{
                            AddShopFragment.isOrderEntryPressed=true
                            ShopDetailFragment.isOrderEntryPressed=true
                            AddShopFragment.newShopID=addShopData?.shop_id
                            if(Pref.IsActivateNewOrderScreenwithSize){//13-09-2021
                                (mContext as DashboardActivity).loadFragment(FragType.NewOrderScrOrderDetailsFragment, true, addShopData!!.shop_id)
                            }else {
                                (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, addShopData)
                            }
                        }

                        //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                    }

                    override fun onRightClick(editableData: String) {
                        (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                        //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                    }


                },object : CommonDialog.OnCloseClickListener{
                    override fun onCloseClick() {
                        if(AppUtils.getSharedPreferenceslogOrderStatusRequired(mContext)){
                            val dialog = Dialog(mContext)
                            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setCancelable(false)
                            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                            dialog.setContentView(R.layout.dialog_cancel_order_status)

                            val user_name=dialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                            val order_status = dialog.findViewById(R.id.tv_cancel_order_status) as AppCustomTextView
                            val cancel_remarks = dialog.findViewById(R.id.et_cancel_order_remarks) as AppCustomEditText
                            val submitRemarks = dialog.findViewById(R.id.tv_cancel_order_submit_remarks) as AppCustomTextView

                            order_status.text="Failure"
                            user_name.text="Hi "+Pref.user_name+"!"

                            submitRemarks.setOnClickListener(View.OnClickListener { view ->
                                if(!TextUtils.isEmpty(cancel_remarks.text.toString().trim())){
                                    //Toast.makeText(mContext,cancel_remarks.text.toString(),Toast.LENGTH_SHORT).show()
                                    val obj=OrderStatusRemarksModelEntity()
                                    obj.shop_id= addShopData?.shop_id
                                    obj.user_id=Pref.user_id
                                    obj.order_status=order_status.text.toString()
                                    obj.order_remarks=cancel_remarks!!.text!!.toString()
                                    obj.visited_date_time=AppUtils.getCurrentDateTime()
                                    obj.visited_date=AppUtils.getCurrentDateForShopActi()
                                    obj.isUploaded=false

                                    var shopAll=AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityAll()
                                    if(shopAll.size == 1){
                                        obj.shop_revisit_uniqKey=shopAll.get(0).shop_revisit_uniqKey
                                    }else if(shopAll.size!=0){
                                        obj.shop_revisit_uniqKey=shopAll.get(shopAll.size-1).shop_revisit_uniqKey
                                    }
                                    if(shopAll.size!=0)
                                    AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.insert(obj)
                                    dialog.dismiss()
                                }else{
                                    submitRemarks.setError("Enter Remarks")
                                    submitRemarks.requestFocus()
                                }

                            })
                            dialog.show()
                        }
                    }
                }).show((mContext as DashboardActivity).supportFragmentManager, "")
            }

            2 -> CommonDialog.getInstanceNew(AppUtils.hiFirstNameText()+"!", "Select what would you like to do?", "Collection entry", "Opening stock", false, object : CommonDialogClickListener {
                override fun onLeftClick() {
                    (mContext as DashboardActivity).loadFragment(FragType.CollectionDetailsFragment, true, addShopData)
                    //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                }

                override fun onRightClick(editableData: String) {
                    (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                    //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                }

            },object : CommonDialog.OnCloseClickListener{
                override fun onCloseClick() {
                    if(AppUtils.getSharedPreferenceslogOrderStatusRequired(mContext)){
                        val dialog = Dialog(mContext)
                        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                        dialog.setContentView(R.layout.dialog_cancel_order_status)

                        val user_name=dialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                        val order_status = dialog.findViewById(R.id.tv_cancel_order_status) as AppCustomTextView
                        val cancel_remarks = dialog.findViewById(R.id.et_cancel_order_remarks) as AppCustomEditText
                        val submitRemarks = dialog.findViewById(R.id.tv_cancel_order_submit_remarks) as AppCustomTextView

                        order_status.text="Failure"
                        user_name.text="Hi "+Pref.user_name+"!"

                        submitRemarks.setOnClickListener(View.OnClickListener { view ->
                            if(!TextUtils.isEmpty(cancel_remarks.text.toString().trim())){
                                //Toast.makeText(mContext,cancel_remarks.text.toString(),Toast.LENGTH_SHORT).show()
                                val obj=OrderStatusRemarksModelEntity()
                                obj.shop_id= addShopData?.shop_id
                                obj.user_id=Pref.user_id
                                obj.order_status=order_status.text.toString()
                                obj.order_remarks=cancel_remarks!!.text!!.toString()
                                obj.visited_date_time=AppUtils.getCurrentDateTime()
                                obj.visited_date=AppUtils.getCurrentDateForShopActi()
                                obj.isUploaded=false

                                var shopAll=AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityAll()
                                if(shopAll.size == 1){
                                    obj.shop_revisit_uniqKey=shopAll.get(0).shop_revisit_uniqKey
                                }else if(shopAll.size!=0){
                                    obj.shop_revisit_uniqKey=shopAll.get(shopAll.size-1).shop_revisit_uniqKey
                                }
                                if(shopAll.size!=0)
                                AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.insert(obj)
                                dialog.dismiss()
                            }else{
                                submitRemarks.setError("Enter Remarks")
                                submitRemarks.requestFocus()
                            }

                        })
                        dialog.show()
                    }
                }
            }).show((mContext as DashboardActivity).supportFragmentManager, "")

            3 -> CommonDialogSingleBtn.getInstanceNew(AppUtils.hiFirstNameText()+"!", "Select what would you like to do?", "Opening stock", object : OnDialogClickListener {
                override fun onOkClick() {
                    (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                    //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                }
            }, object : CommonDialogSingleBtn.OnCrossClickListener {
                override fun onCrossClick() {
                    if(AppUtils.getSharedPreferenceslogOrderStatusRequired(mContext)){
                        val dialog = Dialog(mContext)
                        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                        dialog.setContentView(R.layout.dialog_cancel_order_status)

                        val user_name=dialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                        val order_status = dialog.findViewById(R.id.tv_cancel_order_status) as AppCustomTextView
                        val cancel_remarks = dialog.findViewById(R.id.et_cancel_order_remarks) as AppCustomEditText
                        val submitRemarks = dialog.findViewById(R.id.tv_cancel_order_submit_remarks) as AppCustomTextView

                        order_status.text="Failure"
                        user_name.text="Hi "+Pref.user_name+"!"

                        submitRemarks.setOnClickListener(View.OnClickListener { view ->
                            if(!TextUtils.isEmpty(cancel_remarks.text.toString().trim())){
                                //Toast.makeText(mContext,cancel_remarks.text.toString(),Toast.LENGTH_SHORT).show()
                                val obj=OrderStatusRemarksModelEntity()
                                obj.shop_id= addShopData?.shop_id
                                obj.user_id=Pref.user_id
                                obj.order_status=order_status.text.toString()
                                obj.order_remarks=cancel_remarks!!.text!!.toString()
                                obj.visited_date_time=AppUtils.getCurrentDateTime()
                                obj.visited_date=AppUtils.getCurrentDateForShopActi()
                                obj.isUploaded=false

                                var shopAll=AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityAll()
                                if(shopAll.size == 1){
                                    obj.shop_revisit_uniqKey=shopAll.get(0).shop_revisit_uniqKey
                                }else if(shopAll.size!=0){
                                    obj.shop_revisit_uniqKey=shopAll.get(shopAll.size-1).shop_revisit_uniqKey
                                }
                                if(shopAll.size!=0)
                                AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.insert(obj)
                                dialog.dismiss()
                            }else{
                                submitRemarks.setError("Enter Remarks")
                                submitRemarks.requestFocus()
                            }

                        })
                        dialog.show()
                    }
                }

            }).show((mContext as DashboardActivity).supportFragmentManager, "CommonDialogSingleBtn")

            4 -> {

                var orderText = ""
                orderText = if (Pref.isQuotationPopupShow)
                    "Quot. entry"
                else
                    "Order entry"

                CommonDialog.getInstanceNew(AppUtils.hiFirstNameText()+"!", "Select what would you like to do?", orderText, "Collection entry", false, object : CommonDialogClickListener {
                    override fun onLeftClick() {
                        if (Pref.isQuotationPopupShow)
                            (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
                        else{
                            AddShopFragment.isOrderEntryPressed=true
                            ShopDetailFragment.isOrderEntryPressed=true
                            AddShopFragment.newShopID=addShopData?.shop_id
                            if(Pref.IsActivateNewOrderScreenwithSize){//13-09-2021
                                (mContext as DashboardActivity).loadFragment(FragType.NewOrderScrOrderDetailsFragment, true, addShopData!!.shop_id)
                            }else {
                                (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, addShopData)
                            }
                        }
                        //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                    }

                    override fun onRightClick(editableData: String) {
                        (mContext as DashboardActivity).loadFragment(FragType.CollectionDetailsFragment, true, addShopData)
                        //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                    }

                },object : CommonDialog.OnCloseClickListener{
                    override fun onCloseClick() {
                        if(AppUtils.getSharedPreferenceslogOrderStatusRequired(mContext)){
                            val dialog = Dialog(mContext)
                            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setCancelable(false)
                            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                            dialog.setContentView(R.layout.dialog_cancel_order_status)

                            val user_name=dialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                            val order_status = dialog.findViewById(R.id.tv_cancel_order_status) as AppCustomTextView
                            val cancel_remarks = dialog.findViewById(R.id.et_cancel_order_remarks) as AppCustomEditText
                            val submitRemarks = dialog.findViewById(R.id.tv_cancel_order_submit_remarks) as AppCustomTextView

                            order_status.text="Failure"
                            user_name.text="Hi "+Pref.user_name+"!"

                            submitRemarks.setOnClickListener(View.OnClickListener { view ->
                                if(!TextUtils.isEmpty(cancel_remarks.text.toString().trim())){
                                    //Toast.makeText(mContext,cancel_remarks.text.toString(),Toast.LENGTH_SHORT).show()
                                    val obj=OrderStatusRemarksModelEntity()
                                    obj.shop_id= addShopData?.shop_id
                                    obj.user_id=Pref.user_id
                                    obj.order_status=order_status.text.toString()
                                    obj.order_remarks=cancel_remarks!!.text!!.toString()
                                    obj.visited_date_time=AppUtils.getCurrentDateTime()
                                    obj.visited_date=AppUtils.getCurrentDateForShopActi()
                                    obj.isUploaded=false

                                    var shopAll=AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityAll()
                                    if(shopAll.size == 1){
                                        obj.shop_revisit_uniqKey=shopAll.get(0).shop_revisit_uniqKey
                                    }else if(shopAll.size!=0){
                                        obj.shop_revisit_uniqKey=shopAll.get(shopAll.size-1).shop_revisit_uniqKey
                                    }
                                    if(shopAll.size!=0)
                                    AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.insert(obj)
                                    dialog.dismiss()
                                }else{
                                    submitRemarks.setError("Enter Remarks")
                                    submitRemarks.requestFocus()
                                }

                            })
                            dialog.show()
                        }
                    }
                }).show((mContext as DashboardActivity).supportFragmentManager, "")
            }

            5 -> {

                var orderText = ""
                orderText = if (Pref.isQuotationPopupShow)
                    "Quot. entry"
                else
                    "Order entry"

                CommonDialogSingleBtn.getInstanceNew(AppUtils.hiFirstNameText()+"!", "Select what would you like to do?", orderText, object : OnDialogClickListener {
                    override fun onOkClick() {
                        if (Pref.isQuotationPopupShow)
                            (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
                        else{
                            AddShopFragment.isOrderEntryPressed=true
                            ShopDetailFragment.isOrderEntryPressed=true
                            AddShopFragment.newShopID=addShopData?.shop_id
                            if(Pref.IsActivateNewOrderScreenwithSize){//13-09-2021
                                (mContext as DashboardActivity).loadFragment(FragType.NewOrderScrOrderDetailsFragment, true, addShopData!!.shop_id)
                            }else {
                                (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, addShopData)
                            }
                        }
                        //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                    }
                }, object : CommonDialogSingleBtn.OnCrossClickListener {
                    override fun onCrossClick() {
                        if(AppUtils.getSharedPreferenceslogOrderStatusRequired(mContext)){
                            val dialog = Dialog(mContext)
                            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setCancelable(false)
                            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                            dialog.setContentView(R.layout.dialog_cancel_order_status)

                            val user_name=dialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                            val order_status = dialog.findViewById(R.id.tv_cancel_order_status) as AppCustomTextView
                            val cancel_remarks = dialog.findViewById(R.id.et_cancel_order_remarks) as AppCustomEditText
                            val submitRemarks = dialog.findViewById(R.id.tv_cancel_order_submit_remarks) as AppCustomTextView

                            order_status.text="Failure"
                            user_name.text="Hi "+Pref.user_name+"!"

                            submitRemarks.setOnClickListener(View.OnClickListener { view ->
                                if(!TextUtils.isEmpty(cancel_remarks.text.toString().trim())){
                                    //Toast.makeText(mContext,cancel_remarks.text.toString(),Toast.LENGTH_SHORT).show()
                                    val obj=OrderStatusRemarksModelEntity()
                                    obj.shop_id= addShopData?.shop_id
                                    obj.user_id=Pref.user_id
                                    obj.order_status=order_status.text.toString()
                                    obj.order_remarks=cancel_remarks!!.text!!.toString()
                                    obj.visited_date_time=AppUtils.getCurrentDateTime()
                                    obj.visited_date=AppUtils.getCurrentDateForShopActi()
                                    obj.isUploaded=false

                                    var shopAll=AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityAll()
                                    if(shopAll.size == 1){
                                        obj.shop_revisit_uniqKey=shopAll.get(0).shop_revisit_uniqKey
                                    }else if(shopAll.size!=0){
                                        obj.shop_revisit_uniqKey=shopAll.get(shopAll.size-1).shop_revisit_uniqKey
                                    }
                                    if(shopAll.size!=0)
                                    AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.insert(obj)
                                    dialog.dismiss()
                                }else{
                                    submitRemarks.setError("Enter Remarks")
                                    submitRemarks.requestFocus()
                                }

                            })
                            dialog.show()
                        }
                    }

                }).show((mContext as DashboardActivity).supportFragmentManager, "CommonDialogSingleBtn")
            }

            6 -> CommonDialogSingleBtn.getInstanceNew(AppUtils.hiFirstNameText()+"!", "Select what would you like to do?", "Collection entry", object : OnDialogClickListener {
                override fun onOkClick() {
                    (mContext as DashboardActivity).loadFragment(FragType.CollectionDetailsFragment, true, addShopData)
                    //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                }
            }, object : CommonDialogSingleBtn.OnCrossClickListener {
                override fun onCrossClick() {
                    if(AppUtils.getSharedPreferenceslogOrderStatusRequired(mContext)){
                        val dialog = Dialog(mContext)
                        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                        dialog.setContentView(R.layout.dialog_cancel_order_status)

                        val user_name=dialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                        val order_status = dialog.findViewById(R.id.tv_cancel_order_status) as AppCustomTextView
                        val cancel_remarks = dialog.findViewById(R.id.et_cancel_order_remarks) as AppCustomEditText
                        val submitRemarks = dialog.findViewById(R.id.tv_cancel_order_submit_remarks) as AppCustomTextView

                        order_status.text="Failure"
                        user_name.text="Hi "+Pref.user_name+"!"

                        submitRemarks.setOnClickListener(View.OnClickListener { view ->
                            if(!TextUtils.isEmpty(cancel_remarks.text.toString().trim())){
                                //Toast.makeText(mContext,cancel_remarks.text.toString(),Toast.LENGTH_SHORT).show()
                                val obj=OrderStatusRemarksModelEntity()
                                obj.shop_id= addShopData?.shop_id
                                obj.user_id=Pref.user_id
                                obj.order_status=order_status.text.toString()
                                obj.order_remarks=cancel_remarks!!.text!!.toString()
                                obj.visited_date_time=AppUtils.getCurrentDateTime()
                                obj.visited_date=AppUtils.getCurrentDateForShopActi()
                                obj.isUploaded=false

                                var shopAll=AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityAll()
                                if(shopAll.size == 1){
                                    obj.shop_revisit_uniqKey=shopAll.get(0).shop_revisit_uniqKey
                                }else if(shopAll.size!=0){
                                    obj.shop_revisit_uniqKey=shopAll.get(shopAll.size-1).shop_revisit_uniqKey
                                }
                                if(shopAll.size!=0)
                                AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.insert(obj)
                                dialog.dismiss()
                            }else{
                                submitRemarks.setError("Enter Remarks")
                                submitRemarks.requestFocus()
                            }

                        })
                        dialog.show()
                    }
                }

            }).show((mContext as DashboardActivity).supportFragmentManager, "CommonDialogSingleBtn")
        }
    }


    private fun showRevisitActionDialog() {
        /*CommonDialog.getInstance("Action", "What you like to do?", "Order Entry", "Collection Entry", false, object : CommonDialogClickListener {
            override fun onLeftClick() {
                (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, addShopData)
                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
            }

            override fun onRightClick() {
                (mContext as DashboardActivity).loadFragment(FragType.CollectionDetailsFragment, true, addShopData)
                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")*/


        if (!Pref.isCustomerFeatureEnable) {
            if (Pref.isStockAvailableForPopup && Pref.isStockAvailableForAll) {

                if (Pref.isOrderAvailableForPopup && Pref.isCollectionAvailableForPopup)
                    showActionDialog(0)
                else if (Pref.isOrderAvailableForPopup)
                    showActionDialog(1)
                else if (Pref.isCollectionAvailableForPopup)
                    showActionDialog(2)
                else
                    showActionDialog(3)
            } else if (Pref.isStockAvailableForPopup && !Pref.isStockAvailableForAll) {

                if (addShopData.type == "4") {
                    if (Pref.isOrderAvailableForPopup && Pref.isCollectionAvailableForPopup)
                        showActionDialog(0)
                    else if (Pref.isOrderAvailableForPopup)
                        showActionDialog(1)
                    else if (Pref.isCollectionAvailableForPopup)
                        showActionDialog(2)
                    else
                        showActionDialog(3)
                } else {
                    if (Pref.isOrderAvailableForPopup && Pref.isCollectionAvailableForPopup)
                        showActionDialog(4)
                    else if (Pref.isOrderAvailableForPopup)
                        showActionDialog(5)
                    else if (Pref.isCollectionAvailableForPopup)
                        showActionDialog(6)
                    else
                        AppUtils.isRevisit = false
                }

            } else if (!Pref.isStockAvailableForPopup) {
                if (Pref.isOrderAvailableForPopup && Pref.isCollectionAvailableForPopup)
                    showActionDialog(4)
                else if (Pref.isOrderAvailableForPopup)
                    showActionDialog(5)
                else if (Pref.isCollectionAvailableForPopup)
                    showActionDialog(6)
                else
                    AppUtils.isRevisit = false
            } else
                AppUtils.isRevisit = false
        } else {
            if (Pref.isStockAvailableForPopup && Pref.isStockAvailableForAll) {

                if (Pref.isQuotationPopupShow && Pref.isCollectionAvailableForPopup)
                    showActionDialog(0)
                else if (Pref.isQuotationPopupShow)
                    showActionDialog(1)
                else if (Pref.isCollectionAvailableForPopup)
                    showActionDialog(2)
                else
                    showActionDialog(3)
            } else if (Pref.isStockAvailableForPopup && !Pref.isStockAvailableForAll) {

                if (addShopData.type == "4") {
                    if (Pref.isQuotationPopupShow && Pref.isCollectionAvailableForPopup)
                        showActionDialog(0)
                    else if (Pref.isQuotationPopupShow)
                        showActionDialog(1)
                    else if (Pref.isCollectionAvailableForPopup)
                        showActionDialog(2)
                    else
                        showActionDialog(3)
                } else {
                    if (Pref.isQuotationPopupShow && Pref.isCollectionAvailableForPopup)
                        showActionDialog(4)
                    else if (Pref.isQuotationPopupShow)
                        showActionDialog(5)
                    else if (Pref.isCollectionAvailableForPopup)
                        showActionDialog(6)
                    else
                        AppUtils.isRevisit = false
                }

            } else if (!Pref.isStockAvailableForPopup) {
                if (Pref.isQuotationPopupShow && Pref.isCollectionAvailableForPopup)
                    showActionDialog(4)
                else if (Pref.isQuotationPopupShow)
                    showActionDialog(5)
                else if (Pref.isCollectionAvailableForPopup)
                    showActionDialog(6)
                else
                    AppUtils.isRevisit = false
            } else
                AppUtils.isRevisit = false
        }
    }

    private fun showPPDDAlert(msg: String) {
        AppUtils.isRevisit = false
        CommonDialogSingleBtn.getInstance("Assign PP/DD Alert", msg, getString(R.string.ok), object : OnDialogClickListener {
            override fun onOkClick() {
                enabledEntry(true)
            }
        }).show((mContext as DashboardActivity).supportFragmentManager, "CommonDialogSingleBtn")
    }

    private fun setData(addShopData: AddShopDBModelEntity) {
        //Picasso.with(mContext).load(addShopData.shopImageLocalPath).into(shopImage)

        try {

            if (!TextUtils.isEmpty(addShopData.shopImageLocalPath)) {
                Picasso.get()
                        .load(addShopData.shopImageLocalPath)
                        .resize(800, 100)
                        .into(shopImage)
            }
            /* shopName.text = addShopData.shopName
         shopAddress.text = addShopData.address + ", " + addShopData.pinCode
         shopPin.text = addShopData.pinCode
         shopContactNumber.text = addShopData.ownerContactNumber
         shopOwnerEmail.text = addShopData.ownerEmailId
         ownwr_name_TV.text = addShopData.ownerName
         ownwr_dob_TV.text = addShopData.dateOfBirth
         ownwr_ani_TV.text = addShopData.dateOfAniversary
         shop_type_TV.text = AppUtils.getCategoryNameFromId(addShopData.type, mContext)*/

            if(Pref.IsAlternateNoForCustomer && !TextUtils.isEmpty(addShopData.alternateNoForCustomer))
                alternate_no_TV.setText(addShopData.alternateNoForCustomer)

            if(Pref.IsWhatsappNoForCustomer && !TextUtils.isEmpty(addShopData.whatsappNoForCustomer))
                whatsappp_no_TV.setText(addShopData.whatsappNoForCustomer)

            if(Pref.IsprojectforCustomer && !TextUtils.isEmpty(addShopData.project_name))
                project_name_TV.setText(addShopData.project_name)

            if(Pref.IslandlineforCustomer && !TextUtils.isEmpty(addShopData.landline_number))
                land_contact_no_TV.setText(addShopData.landline_number)

            if (!TextUtils.isEmpty(addShopData.shopName))
                shopName.setText(addShopData.shopName)

            if (!TextUtils.isEmpty(addShopData.address) /*&& !TextUtils.isEmpty(addShopData.pinCode)*/)
                shopAddress.setText(addShopData.address /*+ ", " + addShopData.pinCode*/)

            if (!TextUtils.isEmpty(addShopData.pinCode))
                shopPin.setText(addShopData.pinCode)


            if (Pref.IsGSTINPANEnableInShop && !TextUtils.isEmpty(addShopData.gstN_Number))
                shopGSTIN.setText(addShopData.gstN_Number)

            if (Pref.IsGSTINPANEnableInShop && !TextUtils.isEmpty(addShopData.shopOwner_PAN))
                shopPancard.setText(addShopData.shopOwner_PAN)

            if (!TextUtils.isEmpty(addShopData.ownerContactNumber)) {
                shopContactNumber.setText(addShopData.ownerContactNumber)

                if (addShopData.is_otp_verified.equals("true", ignoreCase = true)) {
                    //shopContactNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0)
                    iv_otp_check.visibility = View.VISIBLE
                    tv_verified.text = getString(R.string.verified)
                    tv_verified.setOnClickListener(null)
                    ll_verified.visibility = View.VISIBLE
                } else {
                    val list = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, AppUtils.getCurrentDateForShopActi())

                    if (list != null && list.isNotEmpty() && list[0].isVisited) {
                        iv_otp_check.visibility = View.GONE
                        tv_verified.text = "Enter OTP"
                        tv_verified.setOnClickListener(this)
                        ll_verified.visibility = View.VISIBLE
                    } else {
                        iv_otp_check.visibility = View.GONE
                        tv_verified.visibility = View.GONE
                    }
                }
            }

            if (!TextUtils.isEmpty(addShopData.ownerEmailId))
                shopOwnerEmail.setText(addShopData.ownerEmailId)



            if (!TextUtils.isEmpty(addShopData.ownerName))
                ownwr_name_TV.setText(addShopData.ownerName)

            if (!TextUtils.isEmpty(addShopData.dateOfBirth)) {
                ownwr_dob_TV.text = AppUtils.changeAttendanceDateFormat(addShopData.dateOfBirth)
            }

            if (!TextUtils.isEmpty(addShopData.dateOfAniversary)) {
                ownwr_ani_TV.text = AppUtils.changeAttendanceDateFormat(addShopData.dateOfAniversary)
            }

            if (!TextUtils.isEmpty(addShopData.type)) {
                type = addShopData.type
                val shopType = AppDatabase.getDBInstance()?.shopTypeDao()?.getSingleType(type)
                shop_type_TV.text = shopType?.shoptype_name  //AppUtils.getCategoryNameFromId(addShopData.type, mContext)
            }

            if (!TextUtils.isEmpty(addShopData.assigned_to_dd_id)) {
                val assignedToddObj = AppDatabase.getDBInstance()?.ddListDao()?.getSingleValue(addShopData.assigned_to_dd_id)
                if (assignedToddObj != null) {
                    rl_assigned_to_dd.visibility = View.VISIBLE
                    assigned_to_dd_TV.text = assignedToddObj?.dd_name + " (" + assignedToddObj?.dd_phn_no + ")"
                    assignedToDDId = addShopData.assigned_to_dd_id
                } else
                    rl_assigned_to_dd.visibility = View.GONE
            } else
                rl_assigned_to_dd.visibility = View.GONE


            if (!TextUtils.isEmpty(addShopData.assigned_to_pp_id)) {
                val assignedToppObj = AppDatabase.getDBInstance()?.ppListDao()?.getSingleValue(addShopData.assigned_to_pp_id)
                if (assignedToppObj != null) {
                    rl_assigned_to_pp.visibility = View.VISIBLE
                    assigned_to_pp_TV.text = assignedToppObj?.pp_name + " (" + assignedToppObj?.pp_phn_no + ")"
                    assignedToPPId = addShopData.assigned_to_pp_id
                } else
                    rl_assigned_to_pp.visibility = View.GONE
            } else
                rl_assigned_to_pp.visibility = View.GONE

            //order_amt_p_TV.text = " " + mContext.getString(R.string.zero_order_in_value)

            if (addShopData.type == "5") {
                rl_amount.visibility = View.VISIBLE

                if (!TextUtils.isEmpty(addShopData.amount))
                    amount_ET.setText(addShopData.amount)
            } else
                rl_amount.visibility = View.GONE


            if (!TextUtils.isEmpty(addShopData.area_id)) {
                val area = AppDatabase.getDBInstance()?.areaListDao()?.getSingleArea(addShopData.area_id)

                area?.run {
                    tv_area.text = area_name
                    areaId = area_id!!
                }
            }

            if (!TextUtils.isEmpty(addShopData.model_id)) {
                val model = AppDatabase.getDBInstance()?.modelListDao()?.getSingleType(addShopData.model_id)

                model?.run {
                    tv_model.text = model_name
                    modelId = model_id!!
                }
            }

            if (!TextUtils.isEmpty(addShopData.primary_app_id)) {
                val primary = AppDatabase.getDBInstance()?.primaryAppListDao()?.getSingleType(addShopData.primary_app_id)

                primary?.run {
                    tv_primary_application.text = primary_app_name
                    primaryAppId = primary_app_id!!
                }
            }

            if (!TextUtils.isEmpty(addShopData.secondary_app_id)) {
                val secondary = AppDatabase.getDBInstance()?.secondaryAppListDao()?.getSingleType(addShopData.secondary_app_id)

                secondary?.run {
                    tv_secondary_application.text = secondary_app_name
                    secondaryAppId = secondary_app_id!!
                }
            }

            if (!TextUtils.isEmpty(addShopData.lead_id)) {
                val lead = AppDatabase.getDBInstance()?.leadTypeDao()?.getSingleType(addShopData.lead_id)

                lead?.run {
                    tv_lead_type.text = lead_name
                    leadId = lead_id!!
                }
            }

            if (!TextUtils.isEmpty(addShopData.stage_id)) {
                val stage = AppDatabase.getDBInstance()?.stageDao()?.getSingleType(addShopData.stage_id)

                stage?.run {
                    tv_stage.text = stage_name
                    stageId = stage_id!!
                }
            }

            if (!TextUtils.isEmpty(addShopData.funnel_stage_id)) {
                val funnelStage = AppDatabase.getDBInstance()?.funnelStageDao()?.getSingleType(addShopData.funnel_stage_id)

                funnelStage?.run {
                    tv_funnel_stage.text = funnel_stage_name
                    funnelStageId = funnel_stage_id!!
                }
            }

            if (!TextUtils.isEmpty(addShopData.booking_amount))
                et_booking_amount.setText(addShopData.booking_amount)

            if (!TextUtils.isEmpty(shopId)) {

                val sList = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopId)
//                if(sList[0].totalVisitCount.equals("NULL")&& sList[0].totalVisitCount==null){
//                    total_visited_value_TV.visibility = View.GONE
//                    total_visited_RL.visibility = View.GONE
//                }else{
//                    total_visited_value_TV.text = " " + sList[0].totalVisitCount
//                }
                total_visited_value_TV.text = " " + sList[0].totalVisitCount

//        total_visited_value_TV.text = " " + AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId).totalVisitCount
//       last_visited_date_TV.text=" "+ AppDatabase.getDBInstance()!!.addShopEntryDao().getLastVisitedDate()
                last_visited_date_TV.text = " " + AppDatabase.getDBInstance()!!.addShopEntryDao().getLastVisitedDate(shopId)

                val orderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shopId) as ArrayList<OrderDetailsListEntity>

                if (orderList != null && orderList.isNotEmpty()) {

                    var amount = 0.0
                    for (i in orderList.indices) {
                        if (!TextUtils.isEmpty(orderList[i].amount))
                            amount += orderList[i].amount?.toDouble()!!
                    }
                    val finalAmount = String.format("%.2f", amount.toFloat())
                    order_amt_p_TV.text = " \u20B9 $finalAmount"
                    quot_amt_p_TV.text = " \u20B9 ${orderList[0].amount}"
                }


                if (Pref.isQuotationShow) {
                    val quoList = AppDatabase.getDBInstance()!!.quotDao().getSingleShopQuotation(shopId)
                    if (quoList != null && quoList.isNotEmpty()) {
                        var amount = 0.0
                        for (i in quoList.indices) {
                            if (!TextUtils.isEmpty(quoList[i].amount))
                                amount += quoList[i].amount?.toDouble()!!
                        }
                        val finalAmount = String.format("%.2f", amount.toFloat())
                        quot_amt_p_TV.text = " \u20B9 $finalAmount"
                    }
                }

                when (addShopData.type) {
                    "10" -> {
                        if (!TextUtils.isEmpty(addShopData.type_id)) {
                            val type = AppDatabase.getDBInstance()?.typeListDao()?.getSingleType(addShopData.type_id!!)

                            type?.run {
                                type_TV.text = name
                                typeId = type_id!!
                            }
                        }

                        rl_type.visibility = View.VISIBLE

                        if (Pref.isDDShowForMeeting)
                            rl_assigned_to_dd.visibility = View.VISIBLE
                        else
                            rl_assigned_to_dd.visibility = View.GONE

                        ll_doc_extra_info.visibility = View.GONE
                        ll_extra_info.visibility = View.GONE
                        rl_entity_main.visibility = View.GONE
                        rl_retailer.visibility = View.GONE
                        rl_dealer.visibility = View.GONE
                        rl_assign_to_shop.visibility = View.GONE

                        assigned_to_pp_header_TV.text = "Assigned to " + Pref.ppText
                        assigned_to_pp_TV.hint = "Select Assigned to " + Pref.ppText
                        view_on_map_label_TV.text = getString(R.string.owner_name)
                        owner_contact_no_label_TV.text = getString(R.string.owner_contact_number)
                        owner_email_label_TV.text = getString(R.string.owner_email)

                    }
                    "8" -> {
                        rl_type.visibility = View.GONE
                        ll_doc_extra_info.visibility = View.VISIBLE
                        ll_extra_info.visibility = View.GONE
                        rl_entity_main.visibility = View.GONE
                        rl_retailer.visibility = View.GONE
                        rl_dealer.visibility = View.GONE
                        rl_assign_to_shop.visibility = View.GONE

                        assigned_to_pp_header_TV.text = "Assigned to " + Pref.ppText
                        assigned_to_pp_TV.hint = "Select Assigned to " + Pref.ppText
                        view_on_map_label_TV.text = getString(R.string.contact_name)
                        owner_contact_no_label_TV.text = getString(R.string.contact_number)
                        owner_email_label_TV.text = getString(R.string.contact_email)
                    }
                    "7" -> {
                        rl_type.visibility = View.GONE
                        ll_doc_extra_info.visibility = View.GONE
                        rl_entity_main.visibility = View.GONE
                        rl_retailer.visibility = View.GONE
                        rl_dealer.visibility = View.GONE
                        rl_assign_to_shop.visibility = View.GONE
                        checkExtraInfoWillVisibleOrNot()

                        assigned_to_pp_header_TV.text = "Assigned to"
                        assigned_to_pp_TV.hint = "Select Assigned to"
                        view_on_map_label_TV.text = getString(R.string.contact_name)
                        owner_contact_no_label_TV.text = getString(R.string.contact_number)
                        owner_email_label_TV.text = getString(R.string.contact_email)
                    }
                    "6" -> {
                        rl_type.visibility = View.GONE
                        ll_doc_extra_info.visibility = View.GONE
                        rl_entity_main.visibility = View.GONE
                        rl_retailer.visibility = View.GONE
                        rl_dealer.visibility = View.GONE
                        rl_assign_to_shop.visibility = View.GONE
                        checkExtraInfoWillVisibleOrNot()

                        assigned_to_pp_header_TV.text = "Assigned to " + Pref.ppText
                        assigned_to_pp_TV.hint = "Select Assigned to " + Pref.ppText
                        view_on_map_label_TV.text = getString(R.string.contact_name)
                        owner_contact_no_label_TV.text = getString(R.string.contact_number)
                        owner_email_label_TV.text = getString(R.string.contact_email)
                    }
                    else -> {
                        rl_type.visibility = View.GONE
                        ll_doc_extra_info.visibility = View.GONE
                        ll_extra_info.visibility = View.GONE

                        if (addShopData.type == "1") {
                            rl_dealer.visibility = View.GONE
                            rl_assign_to_shop.visibility = View.GONE

                            if (Pref.willShowEntityTypeforShop)
                                rl_entity_main.visibility = View.VISIBLE
                            else
                                rl_entity_main.visibility = View.GONE

                            if (Pref.isShowRetailerEntity)
                                rl_retailer.visibility = View.VISIBLE
                            else
                                rl_retailer.visibility = View.GONE

                            if (Pref.isShowDealerForDD)
                                rl_dealer.visibility = View.VISIBLE
                            else
                                rl_dealer.visibility = View.GONE
                        } else if (addShopData.type == "4") {
                            rl_retailer.visibility = View.GONE
                            rl_entity_main.visibility = View.GONE
                            rl_assign_to_shop.visibility = View.GONE

                            if (Pref.isShowDealerForDD)
                                rl_dealer.visibility = View.VISIBLE
                            else
                                rl_dealer.visibility = View.GONE
                        } else if (addShopData.type == "11") {
                            rl_dealer.visibility = View.GONE
                            rl_assign_to_shop.visibility = View.VISIBLE
                            rl_entity_main.visibility = View.GONE

                            if (Pref.isShowRetailerEntity)
                                rl_retailer.visibility = View.VISIBLE
                            else
                                rl_retailer.visibility = View.GONE
                        } else {
                            rl_retailer.visibility = View.GONE
                            rl_dealer.visibility = View.GONE
                            rl_entity_main.visibility = View.GONE
                            rl_assign_to_shop.visibility = View.GONE
                        }

                        assigned_to_pp_header_TV.text = "Assigned to " + Pref.ppText
                        assigned_to_pp_TV.hint = "Select Assigned to " + Pref.ppText
                        view_on_map_label_TV.text = getString(R.string.owner_name)
                        owner_contact_no_label_TV.text = getString(R.string.owner_contact_number)
                        owner_email_label_TV.text = getString(R.string.owner_email)

                        /*10-12-2021*/
                        if (Pref.IsnewleadtypeforRuby && addShopData.type!!.toInt() == 16){
                            owner_name_RL.visibility = View.GONE
//                            view_on_map_label_TV.text = "Agency Name"
                            owner_contact_no_label_TV.text = "Contact Number"
                        }
                    }
                }

                /*AutoDDSelect Feature*/
                if(Pref.AutoDDSelect){
                    rl_assigned_to_dd.visibility = View.VISIBLE
                }
                else{
                    rl_assigned_to_dd.visibility = View.GONE
                }


                if (!TextUtils.isEmpty(addShopData.assigned_to_shop_id)) {
                    val shop = AppDatabase.getDBInstance()?.assignToShopDao()?.getSingleValue(addShopData.assigned_to_shop_id)
                    shop?.apply {
                        assignedToShopId = assigned_to_shop_id!!
                        tv_assign_to_shop.text = name
                    }
                }

                if (!TextUtils.isEmpty(addShopData.entity_id)) {
                    val entity = AppDatabase.getDBInstance()?.entityDao()?.getSingleItem(addShopData.entity_id)
                    entity?.apply {
                        entityId = entity_id!!
                        tv_entity.text = name
                    }
                }

                if (!TextUtils.isEmpty(addShopData.party_status_id)) {
                    val partyStatus = AppDatabase.getDBInstance()?.partyStatusDao()?.getSingleItem(addShopData.party_status_id)
                    partyStatus?.apply {
                        partyStatusId = party_status_id!!
                        tv_party.text = name
                    }
                }

                if (!TextUtils.isEmpty(addShopData.retailer_id)) {
                    val retailer = AppDatabase.getDBInstance()?.retailerDao()?.getSingleItem(addShopData.retailer_id)
                    retailer?.apply {
                        retailerId = retailer_id!!
                        retailer_TV.text = name
                    }
                }

                if (!TextUtils.isEmpty(addShopData.dealer_id)) {
                    val dealer = AppDatabase.getDBInstance()?.dealerDao()?.getSingleItem(addShopData.dealer_id)
                    dealer?.apply {
                        dealerId = dealer_id!!
                        dealer_TV.text = name
                    }
                }

                if (!TextUtils.isEmpty(addShopData.beat_id)) {
                    val beat = AppDatabase.getDBInstance()?.beatDao()?.getSingleItem(addShopData.beat_id)
                    beat?.apply {
                        beatId = beat_id!!
                        beat_TV.text = name
                    }
                }

                if (!TextUtils.isEmpty(addShopData.director_name))
                    et_dir_name_value.setText(addShopData.director_name)

                if (!TextUtils.isEmpty(addShopData.family_member_dob))
                    tv_family_dob.text = AppUtils.changeAttendanceDateFormat(addShopData.family_member_dob)

                if (!TextUtils.isEmpty(addShopData.person_name))
                    et_person_name_value.setText(addShopData.person_name)

                if (!TextUtils.isEmpty(addShopData.person_no))
                    et_person_no_value.setText(addShopData.person_no)

                if (!TextUtils.isEmpty(addShopData.add_dob))
                    tv_add_dob.text = AppUtils.changeAttendanceDateFormat(addShopData.add_dob)

                if (!TextUtils.isEmpty(addShopData.add_doa))
                    tv_add_doa.text = AppUtils.changeAttendanceDateFormat(addShopData.add_doa)

                if (!TextUtils.isEmpty(addShopData.doc_degree)) {
                    degreeImgLink = addShopData.doc_degree
                    tv_degree_img_link.text = addShopData.doc_degree
                }

                if (!TextUtils.isEmpty(addShopData.specialization))
                    et_specialization.setText(addShopData.specialization)

                if (!TextUtils.isEmpty(addShopData.patient_count))
                    et_patient_count.setText(addShopData.patient_count)

                if (!TextUtils.isEmpty(addShopData.category))
                    et_category.setText(addShopData.category)

                if (!TextUtils.isEmpty(addShopData.family_member_dob))
                    tv_doc_family_dob.text = AppUtils.changeAttendanceDateFormat(addShopData.family_member_dob)

                if (!TextUtils.isEmpty(addShopData.doc_address))
                    et_doc_add.setText(addShopData.doc_address)

                if (!TextUtils.isEmpty(addShopData.doc_pincode))
                    et_doc_pincode.setText(addShopData.doc_pincode)

                if (addShopData.chamber_status == 1) {
                    iv_yes.isSelected = true
                    iv_no.isSelected = false
                } else if (addShopData.chamber_status == 0) {
                    iv_no.isSelected = false
                    iv_no.isSelected = true
                }

                if (!TextUtils.isEmpty(addShopData.remarks))
                    et_remarks.setText(addShopData.remarks)

                if (!TextUtils.isEmpty(addShopData.chemist_name))
                    et_chemist_name.setText(addShopData.chemist_name)

                if (!TextUtils.isEmpty(addShopData.chemist_address))
                    et_chemist_add.setText(addShopData.chemist_address)

                if (!TextUtils.isEmpty(addShopData.chemist_pincode))
                    et_chemist_pincode.setText(addShopData.chemist_pincode)

                if (!TextUtils.isEmpty(addShopData.assistant_name))
                    et_assistant_name.setText(addShopData.assistant_name)

                if (!TextUtils.isEmpty(addShopData.assistant_no))
                    et_assistant_no.setText(addShopData.assistant_no)

                if (!TextUtils.isEmpty(addShopData.assistant_dob))
                    tv_assistant_dob.text = AppUtils.changeAttendanceDateFormat(addShopData.assistant_dob)

                if (!TextUtils.isEmpty(addShopData.assistant_doa))
                    tv_assistant_doa.text = AppUtils.changeAttendanceDateFormat(addShopData.assistant_doa)

                if (!TextUtils.isEmpty(addShopData.assistant_family_dob))
                    tv_assistant_family_dob.text = AppUtils.changeAttendanceDateFormat(addShopData.assistant_family_dob)


                /*14-12-2021*/
                if (!TextUtils.isEmpty(addShopData.agency_name))
                    agency_name_TV.setText(addShopData.agency_name)



                val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                if (addShopData.type == "8") {
                    shopImage.visibility = View.GONE
                    view_shop_image.visibility = View.GONE

                    params.setMargins(0, 0, 0, 0)
                } else {
                    shopImage.visibility = View.VISIBLE
                    view_shop_image.visibility = View.VISIBLE

                    params.addRule(RelativeLayout.BELOW, R.id.shop_img_IV)
                    params.setMargins(0, mContext.resources.getDimensionPixelOffset(R.dimen._minus12sdp), 0, 0)
                }
                shops_detail_CV.layoutParams = params

                if (Pref.IsprojectforCustomer) {
                    view_on_map_label_TV.text="Contact Name"
                }
                else {
                    view_on_map_label_TV.text = getString(R.string.owner_name)
                }

                if (Pref.IslandlineforCustomer) {
                    owner_contact_no_label_TV.text = "Contact Number"
                }
                else {
                    owner_contact_no_label_TV.text = getString(R.string.owner_contact_number)
                }
                if(sList[0].totalVisitCount==null){
                    total_visited_RL.visibility = View.GONE
                    view1.visibility = View.GONE
                }
                else{
                    total_visited_RL.visibility = View.VISIBLE
                    view1.visibility = View.VISIBLE
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            AppUtils.isRevisit = false
        }
    }

    private fun checkExtraInfoWillVisibleOrNot() {
        if (Pref.willMoreVisitUpdateCompulsory) {
            ll_extra_info.visibility = View.VISIBLE
            //ownerEmail.imeOptions = EditorInfo.IME_ACTION_NEXT
        } else {
            if (Pref.willMoreVisitUpdateOptional) {
                if (TextUtils.isEmpty(addShopData.director_name))
                    ll_extra_info.visibility = View.GONE
                else
                    ll_extra_info.visibility = View.VISIBLE
            }
            else
                ll_extra_info.visibility = View.GONE
            //ownerEmail.imeOptions = EditorInfo.IME_ACTION_DONE
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.call_shop_RL -> {
                IntentActionable.initiatePhoneCall(mContext, addShopData.ownerContactNumber)
            }

            R.id.email_RL -> {
                IntentActionable.sendMail(mContext, addShopData.ownerEmailId, "")
            }

            R.id.shop_img_IV -> {
                isDocDegree = 0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initPermissionCheck()
                else {
                    //showPictureDialog()
                    launchCamera()
                }
            }
            R.id.bg_blurred_iv -> {
                overlay_rl.visibility = View.GONE
            }
            R.id.address_RL -> {
                //(mContext as DashboardActivity).openLocationMap(addShopData.shopLat.toString(), addShopData.shopLong.toString())
            }
            R.id.assigned_to_TV -> {
                val mAssignedList: ArrayList<String> = ArrayList()
                for (i in 0..20) {
                    mAssignedList.add("test" + i)
                }
                callThemePopUp(assigned_to_TV, mAssignedList);
            }

            R.id.shop_type_TV -> {
                val shopTypeList = AppDatabase.getDBInstance()?.shopTypeDao()?.getAll()
                if (shopTypeList == null || shopTypeList.isEmpty())
                    getShopTypeListApi(shop_type_TV, false)
                else
                    initShopTypePopUp(shop_type_TV)
            }

            R.id.assigned_to_pp_TV -> {
                val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                if (assignPPList == null || assignPPList.isEmpty()) {
                    if (!TextUtils.isEmpty(Pref.profile_state)) {
                        if (AppUtils.isOnline(mContext))
                            getAssignedPPListApi(false, "")
                        else
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    } else {
                        showProfileAlert()
                    }
                } else {
                    showAssignedToPPDialog(assignPPList)
                }
            }

            R.id.assigned_to_dd_TV -> {
                val assignDDList = AppDatabase.getDBInstance()?.ddListDao()?.getAll()
                if (assignDDList == null || assignDDList.isEmpty()) {
                    if (!TextUtils.isEmpty(Pref.profile_state)) {
                        if (AppUtils.isOnline(mContext))
                            getAssignedDDListApi(false, "")
                        else
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    } else {
                        showProfileAlert()
                    }
                } else {
                    /*if (!TextUtils.isEmpty(assignedToPPId)) {
                        val list_ = AppDatabase.getDBInstance()?.ddListDao()?.getValuePPWise(assignedToPPId)
                        showAssignedToDDDialog(list_)
                    }
                    else {
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.select_pp))
                    }*/
                    if (dealerId.isNotEmpty()) {
                        val list_ = AppDatabase.getDBInstance()?.ddListDao()?.getValueTypeWise(dealerId)
                        if (list_ != null && list_.isNotEmpty())
                            showAssignedToDDDialog(list_)
                        else
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                    }
                    else
                        showAssignedToDDDialog(assignDDList)
                    //showAssignedToDDDialog(assignDDList)
                }
            }

            R.id.save_TV -> {
                if(Pref.IsGSTINPANEnableInShop){
                    checkValidation()
                }
                else if (!addShopData.isUploaded || addShopData.isEditUploaded == 0) {
                    (mContext as DashboardActivity).showSnackMessage("Please sync this shop first.")
                } else {
                    checkValidation()
                }
            }

            R.id.ownwr_ani_TV -> {
                isDOB = "0"
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                val aniDatePicker = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                aniDatePicker.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                aniDatePicker.show()
            }

            R.id.ownwr_dob_TV -> {
                isDOB = "1"
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                val datepickerDialog = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datepickerDialog.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datepickerDialog.show()
            }
            R.id.tv_family_dob -> {
                isDOB = "2"
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                val datepickerDialog = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datepickerDialog.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datepickerDialog.show()
            }
            R.id.tv_add_doa -> {
                isDOB = "4"
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                val datepickerDialog = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datepickerDialog.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datepickerDialog.show()
            }
            R.id.tv_add_dob -> {
                isDOB = "3"
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                val datepickerDialog = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datepickerDialog.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datepickerDialog.show()
            }
            R.id.tv_verified -> {
                showOtpVerificationDialog(shopId, true)
            }
            R.id.tv_area -> {
                val areaList = AppDatabase.getDBInstance()?.areaListDao()?.getAll() as ArrayList<AreaListEntity>

                if (areaList == null || areaList.isEmpty()) {
                    if (!TextUtils.isEmpty(Pref.profile_city)) {
                        if (AppUtils.isOnline(mContext))
                            getAreaListApi()
                        else
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    } else {
                        showProfileAlert()
                    }
                } else
                    showAreaDialog(areaList)
            }

            R.id.tv_model -> {
                val list = AppDatabase.getDBInstance()?.modelListDao()?.getAll() as ArrayList<ModelEntity>

                if (list == null || list.isEmpty())
                    getModelListApi()
                else
                    showModelDialog(list)
            }
            R.id.iv_model_dropdown -> {
                val list = AppDatabase.getDBInstance()?.modelListDao()?.getAll() as ArrayList<ModelEntity>

                if (list == null || list.isEmpty())
                    getModelListApi()
                else
                    showModelDialog(list)
            }
            R.id.tv_primary_application -> {
                val list = AppDatabase.getDBInstance()?.primaryAppListDao()?.getAll() as ArrayList<PrimaryAppEntity>

                if (list == null || list.isEmpty())
                    getPrimaryAppListApi()
                else
                    showPrimaryAppDialog(list)
            }
            R.id.iv_primary_application_dropdown -> {
                val list = AppDatabase.getDBInstance()?.primaryAppListDao()?.getAll() as ArrayList<PrimaryAppEntity>

                if (list == null || list.isEmpty())
                    getPrimaryAppListApi()
                else
                    showPrimaryAppDialog(list)
            }
            R.id.tv_secondary_application -> {
                val list = AppDatabase.getDBInstance()?.secondaryAppListDao()?.getAll() as ArrayList<SecondaryAppEntity>

                if (list == null || list.isEmpty())
                    geSecondaryAppListApi()
                else
                    showSecondaryyAppDialog(list)
            }
            R.id.iv_secondary_application_dropdown -> {
                val list = AppDatabase.getDBInstance()?.secondaryAppListDao()?.getAll() as ArrayList<SecondaryAppEntity>

                if (list == null || list.isEmpty())
                    geSecondaryAppListApi()
                else
                    showSecondaryyAppDialog(list)
            }
            R.id.tv_lead_type -> {
                val list = AppDatabase.getDBInstance()?.leadTypeDao()?.getAll() as ArrayList<LeadTypeEntity>

                if (list == null || list.isEmpty())
                    geLeadApi()
                else
                    showLeadDialog(list)
            }
            R.id.iv_lead_type_dropdown -> {
                val list = AppDatabase.getDBInstance()?.leadTypeDao()?.getAll() as ArrayList<LeadTypeEntity>

                if (list == null || list.isEmpty())
                    geLeadApi()
                else
                    showLeadDialog(list)
            }
            R.id.tv_stage -> {
                val list = AppDatabase.getDBInstance()?.stageDao()?.getAll() as ArrayList<StageEntity>

                if (list == null || list.isEmpty())
                    geStageApi()
                else
                    showStageDialog(list)
            }
            R.id.iv_stage_dropdown -> {
                val list = AppDatabase.getDBInstance()?.stageDao()?.getAll() as ArrayList<StageEntity>

                if (list == null || list.isEmpty())
                    geStageApi()
                else
                    showStageDialog(list)
            }
            R.id.tv_funnel_stage -> {
                val list = AppDatabase.getDBInstance()?.funnelStageDao()?.getAll() as ArrayList<FunnelStageEntity>

                if (list == null || list.isEmpty())
                    geFunnelStageApi()
                else
                    showFunnelStageDialog(list)
            }
            R.id.iv_funnel_stage_dropdown -> {
                val list = AppDatabase.getDBInstance()?.funnelStageDao()?.getAll() as ArrayList<FunnelStageEntity>

                if (list == null || list.isEmpty())
                    geFunnelStageApi()
                else
                    showFunnelStageDialog(list)
            }

            R.id.rl_type -> {
                val typeList = AppDatabase.getDBInstance()?.typeListDao()?.getAll() as ArrayList<TypeListEntity>
                if (typeList != null && typeList.isNotEmpty())
                    showTypeDialog(typeList)
                else
                    getTypeListApi(false)
            }
            R.id.ll_yes -> {
                if (!iv_yes.isSelected) {
                    iv_yes.isSelected = true
                    iv_no.isSelected = false
                }
            }
            R.id.ll_no -> {
                if (!iv_no.isSelected) {
                    iv_yes.isSelected = false
                    iv_no.isSelected = true
                }
            }
            R.id.tv_degree_img_link -> {
                if (isEnabled) {
                    isDocDegree = 1
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        initPermissionCheck()
                    else {
                        showPictureDialog()
                    }
                } else {
                    if (!TextUtils.isEmpty(tv_degree_img_link.text.toString().trim())) {
                        FullImageDialog.getInstance(tv_degree_img_link.text.toString().trim()).show((mContext as DashboardActivity).supportFragmentManager, "")
                    }
                }
            }
            R.id.tv_doc_family_dob -> {
                isDOB = "4"
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                val datepickerDialog = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datepickerDialog.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datepickerDialog.show()
            }
            R.id.tv_assistant_dob -> {
                isDOB = "5"
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                val datepickerDialog = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datepickerDialog.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datepickerDialog.show()
            }
            R.id.tv_assistant_doa -> {
                isDOB = "6"
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                val datepickerDialog = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datepickerDialog.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datepickerDialog.show()
            }
            R.id.tv_assistant_family_dob -> {
                isDOB = "7"
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                val datepickerDialog = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datepickerDialog.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datepickerDialog.show()
            }
            R.id.tv_entity, R.id.iv_entity_dropdown -> {
                val list = AppDatabase.getDBInstance()?.entityDao()?.getAll() as ArrayList<EntityTypeEntity>
                if (list != null && list.isNotEmpty())
                    showEntityDialog(list)
                else
                    getEntityTypeListApi(false)
            }
            R.id.tv_party, R.id.iv_party_dropdown -> {
                val list = AppDatabase.getDBInstance()?.partyStatusDao()?.getAll() as ArrayList<PartyStatusEntity>
                if (list != null && list.isNotEmpty())
                    showPartyStatusDialog(list)
                else
                    getPartyStatusListApi(false)
            }
            R.id.dealer_TV, R.id.iv_dealer_dropdown -> {
                val list = AppDatabase.getDBInstance()?.dealerDao()?.getAll() as ArrayList<DealerEntity>
                if (list != null && list.isNotEmpty())
                    showDealerListDialog(list)
                else
                    getDealerListApi(false)
            }
            R.id.beat_TV, R.id.iv_beat_dropdown -> {
                val list = AppDatabase.getDBInstance()?.beatDao()?.getAll() as ArrayList<BeatEntity>
                if (list != null && list.isNotEmpty())
                    showBeatListDialog(list)
                else
                    getBeatListApi(false)
            }
            R.id.retailer_TV, R.id.iv_retailer_dropdown -> {
                val list = AppDatabase.getDBInstance()?.retailerDao()?.getAll() as ArrayList<RetailerEntity>
                if (list != null && list.isNotEmpty()) {
                    if (addShopData.type != "11") {
                        if (dealerId.isNotEmpty()) {
                            val list_ = AppDatabase.getDBInstance()?.retailerDao()?.getItemTypeWise(dealerId) as java.util.ArrayList<RetailerEntity>
                            if (list_ != null && list_.isNotEmpty())
                                showRetailerListDialog(list_)
                            else
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                        } else
                            showRetailerListDialog(AppDatabase.getDBInstance()?.retailerDao()?.getAll() as ArrayList<RetailerEntity>)
                    }
                    else if (addShopData.type == "11") {
                        val list_ = AppDatabase.getDBInstance()?.retailerDao()?.getAll()?.filter {
                            it.retailer_id == "2"
                        }

                        if (list_ != null && list_.isNotEmpty())
                            showRetailerListDialog(list_ as ArrayList<RetailerEntity>)
                        else
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                    }
                    else
                        showRetailerListDialog(list)
                }
                else
                    getRetailerListApi(false)
            }
            R.id.tv_assign_to_shop, R.id.iv_assign_to_shop_dropdown -> {
                val list = AppDatabase.getDBInstance()?.assignToShopDao()?.getAll() as ArrayList<AssignToShopEntity>
                if (list != null && list.isNotEmpty()) {
                    if (retailerId.isNotEmpty()) {
                        val list_ = AppDatabase.getDBInstance()?.assignToShopDao()?.getValueTypeWise(retailerId) as ArrayList<AssignToShopEntity>
                        if (list_ != null && list_.isNotEmpty())
                            showAssignedToShopListDialog(list_)
                        else
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                    }
                    else
                        showAssignedToShopListDialog(list)
                }
                else {
                    if (!TextUtils.isEmpty(Pref.profile_state)) {
                        if (AppUtils.isOnline(mContext))
                            getAssignedToShopApi(false, "")
                        else
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    }
                    else
                        showProfileAlert()
                }
            }
            100 -> {
                /*sortAlphabatically()
                floating_fab.close(true)
                programFab1.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                programFab2.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab1.setImageResource(R.drawable.ic_tick_float_icon)
                programFab2.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3.setImageResource(R.drawable.ic_tick_float_icon_gray)*/
                floating_fab.close(true)
                programFab1?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                programFab2?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab1?.setImageResource(R.drawable.ic_tick_float_icon)
                programFab2?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3?.setImageResource(R.drawable.ic_tick_float_icon_gray)

                if (programFab4 != null) {
                    programFab4?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                    programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                }

                programFab5?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab5?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab6?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab6?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab7?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab7?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                if (addShopData.type != "8") {
                    if (Pref.isShopAddEditAvailable && Pref.isShopEditEnable)
                        enabledEntry(false)
                    else if (Pref.isOrderShow)
                        if(Pref.IsActivateNewOrderScreenwithSize){//13-09-2021
                            (mContext as DashboardActivity).loadFragment(FragType.NewOrderScrOrderDetailsFragment, true, addShopData!!.shop_id)
                        }else{
                            (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, addShopData)
                        }

                    else if (Pref.isCollectioninMenuShow)
                        (mContext as DashboardActivity).loadFragment(FragType.ShopBillingListFragment, true, addShopData)
                    else if (Pref.isQuotationShow) {
                        (mContext as DashboardActivity).isBack = true
                        (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
                    } else if (addShopData.type == "7" || Pref.willActivityShow) {
                        if (addShopData.type == "7") {
                            (mContext as DashboardActivity).isFromShop = true
                            (mContext as DashboardActivity).loadFragment(FragType.ChemistActivityListFragment, true, addShopData)
                        }
                        else {
                            (mContext as DashboardActivity).isFromMenu = false
                            (mContext as DashboardActivity).loadFragment(FragType.AddActivityFragment, true, addShopData)
                        }
                    } else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4")))
                        (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                }
                else {
                    if (Pref.isShopAddEditAvailable && Pref.isShopEditEnable)
                        enabledEntry(false)
                    else /*if (Pref.willActivityShow)*/ {
                        (mContext as DashboardActivity).isFromShop = true
                        (mContext as DashboardActivity).loadFragment(FragType.DoctorActivityListFragment, true, addShopData)
                    }
                }
            }
            101 -> {
                /*sortByVisitDate()
                floating_fab.close(true)
                programFab1.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                programFab3.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab1.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2.setImageResource(R.drawable.ic_tick_float_icon)
                programFab3.setImageResource(R.drawable.ic_tick_float_icon_gray)*/
                floating_fab.close(true)
                programFab1?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                programFab3?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab1?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2?.setImageResource(R.drawable.ic_tick_float_icon)
                programFab3?.setImageResource(R.drawable.ic_tick_float_icon_gray)

                if (programFab4 != null) {
                    programFab4?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                    programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                }

                programFab5?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab5?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab6?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab6?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab7?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab7?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))

                if (addShopData.type != "8") {
                    if (Pref.isShopAddEditAvailable && Pref.isShopEditEnable) {
                        if (Pref.isOrderShow)
                            if(Pref.IsActivateNewOrderScreenwithSize){//13-09-2021
                                (mContext as DashboardActivity).loadFragment(FragType.NewOrderScrOrderDetailsFragment, true, addShopData!!.shop_id)
                            }else {
                                (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, addShopData)
                            }
                        else if (Pref.isCollectioninMenuShow)
                            (mContext as DashboardActivity).loadFragment(FragType.ShopBillingListFragment, true, addShopData)
                        else if (Pref.isQuotationShow) {
                            (mContext as DashboardActivity).isBack = true
                            (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
                        }
                        else if (Pref.willActivityShow) {
                            if (addShopData.type == "7") {
                                (mContext as DashboardActivity).isFromShop = true
                                (mContext as DashboardActivity).loadFragment(FragType.ChemistActivityListFragment, true, addShopData)
                            }
                            else {
                                (mContext as DashboardActivity).isFromMenu = false
                                (mContext as DashboardActivity).loadFragment(FragType.AddActivityFragment, true, addShopData)
                            }
                        }
                        else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4")))
                            (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                    } else {
                        if (Pref.isCollectioninMenuShow)
                            (mContext as DashboardActivity).loadFragment(FragType.ShopBillingListFragment, true, addShopData)
                        else if (Pref.isQuotationShow) {
                            (mContext as DashboardActivity).isBack = true
                            (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
                        }
                        else if (addShopData.type == "7" || Pref.willActivityShow) {
                            if (addShopData.type == "7") {
                                (mContext as DashboardActivity).isFromShop = true
                                (mContext as DashboardActivity).loadFragment(FragType.ChemistActivityListFragment, true, addShopData)
                            }
                            else {
                                (mContext as DashboardActivity).isFromMenu = false
                                (mContext as DashboardActivity).loadFragment(FragType.AddActivityFragment, true, addShopData)
                            }
                        }
                        else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4")))
                            (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                    }
                }
                else {
                    if (Pref.isShopAddEditAvailable && Pref.isShopEditEnable)  {
                        //if (Pref.willActivityShow)
                        (mContext as DashboardActivity).isFromShop = true
                            (mContext as DashboardActivity).loadFragment(FragType.DoctorActivityListFragment, true, addShopData)
                    }
                }

            }
            102 -> {
                floating_fab.close(true)
                programFab1?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                programFab1?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3?.setImageResource(R.drawable.ic_tick_float_icon)

                if (programFab4 != null) {
                    programFab4?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                    programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                }

                programFab5?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab5?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab6?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab6?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab7?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab7?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                /*AddCollectionDialog.getInstance(shopName.text.toString().trim(),object : AddCollectionDialog.AddCollectionClickLisneter{
                    override fun onClick(collection: String) {
                    }
                }).show((mContext as DashboardActivity).supportFragmentManager, "AddCollectionDialog")*/
                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))

                if (Pref.isShopAddEditAvailable && Pref.isShopEditEnable) {
                    if (Pref.isOrderShow) {
                        if (Pref.isCollectioninMenuShow)
                            (mContext as DashboardActivity).loadFragment(FragType.ShopBillingListFragment, true, addShopData)
                        else if (Pref.isQuotationShow) {
                            (mContext as DashboardActivity).isBack = true
                            (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
                        }
                        else if (addShopData.type == "7" || Pref.willActivityShow) {
                            if (addShopData.type == "7") {
                                (mContext as DashboardActivity).isFromShop = true
                                (mContext as DashboardActivity).loadFragment(FragType.ChemistActivityListFragment, true, addShopData)
                            }
                            else {
                                (mContext as DashboardActivity).isFromMenu = false
                                (mContext as DashboardActivity).loadFragment(FragType.AddActivityFragment, true, addShopData)
                            }
                        }
                        else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4")))
                            (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                    } else {
                        if (Pref.isQuotationShow) {
                            (mContext as DashboardActivity).isBack = true
                            (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
                        }
                        else if (addShopData.type == "7" || Pref.willActivityShow) {
                            if (addShopData.type == "7") {
                                (mContext as DashboardActivity).isFromShop = true
                                (mContext as DashboardActivity).loadFragment(FragType.ChemistActivityListFragment, true, addShopData)
                            }
                            else {
                                (mContext as DashboardActivity).isFromMenu = false
                                (mContext as DashboardActivity).loadFragment(FragType.AddActivityFragment, true, addShopData)
                            }
                        }
                        else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4")))
                            (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                    }
                } else {
                    if (Pref.isOrderShow) {
                        if (Pref.isQuotationShow) {
                            (mContext as DashboardActivity).isBack = true
                            (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
                        }
                        else if (addShopData.type == "7" || Pref.willActivityShow) {
                            if (addShopData.type == "7") {
                                (mContext as DashboardActivity).isFromShop = true
                                (mContext as DashboardActivity).loadFragment(FragType.ChemistActivityListFragment, true, addShopData)
                            }
                            else {
                                (mContext as DashboardActivity).isFromMenu = false
                                (mContext as DashboardActivity).loadFragment(FragType.AddActivityFragment, true, addShopData)
                            }
                        }
                        else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4")))
                            (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                    } else {
                        if (addShopData.type == "7" || Pref.willActivityShow) {
                            if (addShopData.type == "7") {
                                (mContext as DashboardActivity).isFromShop = true
                                (mContext as DashboardActivity).loadFragment(FragType.ChemistActivityListFragment, true, addShopData)
                            }
                            else {
                                (mContext as DashboardActivity).isFromMenu = false
                                (mContext as DashboardActivity).loadFragment(FragType.AddActivityFragment, true, addShopData)
                            }
                        }
                        else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4")))
                            (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                    }
                }
            }
            103 -> {
                floating_fab.close(true)
                programFab1?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab1?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3?.setImageResource(R.drawable.ic_tick_float_icon_gray)

                programFab4?.setImageResource(R.drawable.ic_tick_float_icon)
                programFab4?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)

                programFab5?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab5?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab6?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab6?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab7?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab7?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                /*AddCollectionDialog.getInstance(shopName.text.toString().trim(),object : AddCollectionDialog.AddCollectionClickLisneter{
                    override fun onClick(collection: String) {
                    }
                }).show((mContext as DashboardActivity).supportFragmentManager, "AddCollectionDialog")*/

                if (Pref.isQuotationShow) {
                    (mContext as DashboardActivity).isBack = true
                    (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
                }
                else if (addShopData.type == "7" || Pref.willActivityShow) {
                    if (addShopData.type == "7") {
                        (mContext as DashboardActivity).isFromShop = true
                        (mContext as DashboardActivity).loadFragment(FragType.ChemistActivityListFragment, true, addShopData)
                    }
                    else {
                        (mContext as DashboardActivity).isFromMenu = false
                        (mContext as DashboardActivity).loadFragment(FragType.AddActivityFragment, true, addShopData)
                    }
                }
                else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4")))
                    (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)

                //(mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
            }
            104 -> {
                floating_fab.close(true)
                programFab1?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab1?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3?.setImageResource(R.drawable.ic_tick_float_icon_gray)

                programFab4?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab5?.setImageResource(R.drawable.ic_tick_float_icon)
                programFab5?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)

                programFab6?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab6?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab7?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab7?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                /*AddCollectionDialog.getInstance(shopName.text.toString().trim(),object : AddCollectionDialog.AddCollectionClickLisneter{
                    override fun onClick(collection: String) {
                    }
                }).show((mContext as DashboardActivity).supportFragmentManager, "AddCollectionDialog")*/

                if (addShopData.type == "7" || Pref.willActivityShow) {
                    if (addShopData.type == "7") {
                        (mContext as DashboardActivity).isFromShop = true
                        (mContext as DashboardActivity).loadFragment(FragType.ChemistActivityListFragment, true, addShopData)
                    }
                    else {
                        (mContext as DashboardActivity).isFromMenu = false
                        (mContext as DashboardActivity).loadFragment(FragType.AddActivityFragment, true, addShopData)
                    }
                }
                else if (Pref.willStockShow && (Pref.isStockAvailableForAll || (!Pref.isStockAvailableForAll && addShopData.type == "4")))
                    (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)

                //(mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                //(mContext as DashboardActivity).loadFragment(FragType.ViewStockFragment, true, addShopData.shop_id)
            }
            105 -> {
                floating_fab.close(true)
                programFab1?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab1?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3?.setImageResource(R.drawable.ic_tick_float_icon_gray)

                programFab4?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab5?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab5?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab6?.setImageResource(R.drawable.ic_tick_float_icon)
                programFab6?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)

                programFab7?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab7?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                /*AddCollectionDialog.getInstance(shopName.text.toString().trim(),object : AddCollectionDialog.AddCollectionClickLisneter{
                    override fun onClick(collection: String) {
                    }
                }).show((mContext as DashboardActivity).supportFragmentManager, "AddCollectionDialog")*/


                /////////
                if(programFab5?.labelText.equals("Current Stock") || programFab6?.labelText.equals("Current Stock")){
                    (mContext as DashboardActivity).loadFragment(FragType.UpdateShopStockFragment, true, addShopData?.shop_id)
                }else if(programFab5?.labelText.equals("Competitor Stock") || programFab6?.labelText.equals("Competitor Stock")){
                    (context as DashboardActivity).loadFragment(FragType.CompetetorStockFragment, true, addShopData?.shop_id)
                }else{
                    (mContext as DashboardActivity).loadFragment(FragType.StockListFragment, true, addShopData)
                }

            }
            106 -> {
                floating_fab.close(true)
                programFab1?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab1?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3?.setImageResource(R.drawable.ic_tick_float_icon_gray)

                programFab4?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab5?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab5?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab6?.setImageResource(R.drawable.ic_tick_float_icon)
                programFab6?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab7?.setImageResource(R.drawable.ic_tick_float_icon)
                programFab7?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)

                /*AddCollectionDialog.getInstance(shopName.text.toString().trim(),object : AddCollectionDialog.AddCollectionClickLisneter{
                    override fun onClick(collection: String) {
                    }
                }).show((mContext as DashboardActivity).supportFragmentManager, "AddCollectionDialog")*/


                (mContext as DashboardActivity).loadFragment(FragType.ViewStockFragment, true, addShopData.shop_id)


            }
            701 ->{
                floating_fab.close(true)
                programFab1?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab1?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3?.setImageResource(R.drawable.ic_tick_float_icon_gray)

                programFab4?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab5?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab5?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab6?.setImageResource(R.drawable.ic_tick_float_icon)
                programFab6?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab7?.setImageResource(R.drawable.ic_tick_float_icon)
                programFab7?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)

                (mContext as DashboardActivity).loadFragment(FragType.UpdateShopStockFragment, true, addShopData?.shop_id)
            }
            702 ->{
                floating_fab.close(true)
                programFab1?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2?.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab1?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3?.setImageResource(R.drawable.ic_tick_float_icon_gray)

                programFab4?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab4?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab5?.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab5?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab6?.setImageResource(R.drawable.ic_tick_float_icon)
                programFab6?.colorNormal = mContext.resources.getColor(R.color.colorAccent)

                programFab7?.setImageResource(R.drawable.ic_tick_float_icon)
                programFab7?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)

                (mContext as DashboardActivity).loadFragment(FragType.CompetetorStockFragment, true, addShopData?.shop_id)
            }
        }
    }

    private fun getModelListApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                //repository.getModelList()
                repository.getModelListNew()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            //val response = result as ModelListResponseModel
                            val response = result as ModelListResponse
                            XLog.d("GET MODEL DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.model_list != null && response.model_list!!.isNotEmpty()) {

                                    doAsync {

                                        AppDatabase.getDBInstance()?.modelListDao()?.insertAllLarge(response.model_list!!)

                                   /*     response.model_list?.forEach {
                                            val modelEntity = ModelEntity()
                                            AppDatabase.getDBInstance()?.modelListDao()?.insertAll(modelEntity.apply {
                                                model_id = it.id
                                                model_name = it.name
                                            })
                                        }*/

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            showModelDialog(AppDatabase.getDBInstance()?.modelListDao()?.getAll() as ArrayList<ModelEntity>)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET MODEL DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun showModelDialog(modelList: ArrayList<ModelEntity>) {
        ModelListDialog.newInstance(modelList, { model: ModelEntity ->
            tv_model.text = model.model_name
            modelId = model.model_id!!
            clearFocus()
        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }


    private fun getPrimaryAppListApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getPrimaryAppList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as PrimaryAppListResponseModel
                            XLog.d("GET PRIMARY APP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.primary_application_list != null && response.primary_application_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.primary_application_list?.forEach {
                                            val primaryEntity = PrimaryAppEntity()
                                            AppDatabase.getDBInstance()?.primaryAppListDao()?.insertAll(primaryEntity.apply {
                                                primary_app_id = it.id
                                                primary_app_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            showPrimaryAppDialog(AppDatabase.getDBInstance()?.primaryAppListDao()?.getAll() as ArrayList<PrimaryAppEntity>)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET PRIMARY APP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun showPrimaryAppDialog(primaryAppList: ArrayList<PrimaryAppEntity>) {
        PrimaryAppListDialog.newInstance(primaryAppList, { model: PrimaryAppEntity ->
            tv_primary_application.text = model.primary_app_name
            primaryAppId = model.primary_app_id!!
            clearFocus()
        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }


    private fun geSecondaryAppListApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getSecondaryAppList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as SecondaryAppListResponseModel
                            XLog.d("GET SECONDARY APP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.secondary_application_list != null && response.secondary_application_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.secondary_application_list?.forEach {
                                            val secondaryEntity = SecondaryAppEntity()
                                            AppDatabase.getDBInstance()?.secondaryAppListDao()?.insertAll(secondaryEntity.apply {
                                                secondary_app_id = it.id
                                                secondary_app_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            showSecondaryyAppDialog(AppDatabase.getDBInstance()?.secondaryAppListDao()?.getAll() as ArrayList<SecondaryAppEntity>)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET SECONDARY APP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun showSecondaryyAppDialog(secondaryAppList: ArrayList<SecondaryAppEntity>) {
        SecondaryAppListDialog.newInstance(secondaryAppList, { secondary: SecondaryAppEntity ->
            tv_secondary_application.text = secondary.secondary_app_name
            secondaryAppId = secondary.secondary_app_id!!
            clearFocus()
        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun geLeadApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getLeadTypeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as LeadListResponseModel
                            XLog.d("GET LEAD TYPE DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.lead_type_list != null && response.lead_type_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.lead_type_list?.forEach {
                                            val leadEntity = LeadTypeEntity()
                                            AppDatabase.getDBInstance()?.leadTypeDao()?.insertAll(leadEntity.apply {
                                                lead_id = it.id
                                                lead_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            showLeadDialog(AppDatabase.getDBInstance()?.leadTypeDao()?.getAll() as ArrayList<LeadTypeEntity>)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET LEAD TYPE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun showLeadDialog(leadList: ArrayList<LeadTypeEntity>) {
        LeadListDialog.newInstance(leadList, { lead: LeadTypeEntity ->
            tv_lead_type.text = lead.lead_name
            leadId = lead.lead_id!!
            clearFocus()
        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun geStageApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getStagList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as StageListResponseModel
                            XLog.d("GET STAGE DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.stage_list != null && response.stage_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.stage_list?.forEach {
                                            val stageEntity = StageEntity()
                                            AppDatabase.getDBInstance()?.stageDao()?.insertAll(stageEntity.apply {
                                                stage_id = it.id
                                                stage_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            showStageDialog(AppDatabase.getDBInstance()?.stageDao()?.getAll() as ArrayList<StageEntity>)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET STAGE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun showStageDialog(stageList: ArrayList<StageEntity>) {
        StageListDialog.newInstance(stageList, { stage: StageEntity ->
            tv_stage.text = stage.stage_name
            stageId = stage.stage_id!!
            clearFocus()
        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }


    private fun geFunnelStageApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getFunnelStageList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as FunnelStageListResponseModel
                            XLog.d("GET FUNNEL STAGE DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.funnel_stage_list != null && response.funnel_stage_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.funnel_stage_list?.forEach {
                                            val funnelStageEntity = FunnelStageEntity()
                                            AppDatabase.getDBInstance()?.funnelStageDao()?.insertAll(funnelStageEntity.apply {
                                                funnel_stage_id = it.id
                                                funnel_stage_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            showFunnelStageDialog(AppDatabase.getDBInstance()?.funnelStageDao()?.getAll() as ArrayList<FunnelStageEntity>)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET FUNNEL STAGE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun showFunnelStageDialog(funnelStageList: ArrayList<FunnelStageEntity>) {
        FunnelStageDialog.newInstance(funnelStageList, { funnelStage: FunnelStageEntity ->
            tv_funnel_stage.text = funnelStage.funnel_stage_name
            funnelStageId = funnelStage.funnel_stage_id!!
            clearFocus()
        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }


    private fun getShopTypeListApi(shop_type_TV: AppCustomTextView, isFromRefresh: Boolean) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        if (isFromRefresh)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.wait_msg), 1000)

        val repository = ShopListRepositoryProvider.provideShopListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getShopTypeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ShopTypeResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.Shoptype_list

                                if (list != null && list.isNotEmpty()) {

                                    if (isFromRefresh)
                                        AppDatabase.getDBInstance()?.shopTypeDao()?.deleteAll()


                                    doAsync {

                                        list.forEach {
                                            val shop = ShopTypeEntity()
                                            AppDatabase.getDBInstance()?.shopTypeDao()?.insertAll(shop.apply {
                                                shoptype_id = it.shoptype_id
                                                shoptype_name = it.shoptype_name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()

                                            if (!isFromRefresh)
                                                initShopTypePopUp(shop_type_TV)
                                            else
                                                getTypeListApi(isFromRefresh)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()

                                    if (!isFromRefresh)
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    else
                                        getTypeListApi(isFromRefresh)
                                }
                            } else if (response.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    getTypeListApi(isFromRefresh)
                            } else {
                                progress_wheel.stopSpinning()

                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()

                            if (!isFromRefresh)
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            else
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                        })
        )
    }


    private fun getAreaListApi() {
        val repository = AreaListRepoProvider.provideAreaListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.areaList(Pref.profile_city, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AreaListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.area_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        list.forEach {
                                            val area = AreaListEntity()
                                            AppDatabase.getDBInstance()?.areaListDao()?.insert(area.apply {
                                                area_id = it.area_id
                                                area_name = it.area_name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            showAreaDialog(AppDatabase.getDBInstance()?.areaListDao()?.getAll() as ArrayList<AreaListEntity>)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            } else {
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

    private fun showAreaDialog(areaList: ArrayList<AreaListEntity>) {
        AreaListDialog.newInstance(areaList) { area: AreaListEntity ->
            tv_area.text = area.area_name
            areaId = area.area_id!!
            clearFocus()
        }.show(fragmentManager!!, "")
    }

    private fun getTypeListApi(isFromRefresh: Boolean) {

        if (!isFromRefresh && !AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.typeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TypeListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.type_list

                                if (list != null && list.isNotEmpty()) {

                                    if (isFromRefresh)
                                        AppDatabase.getDBInstance()?.typeListDao()?.delete()


                                    doAsync {

                                        list.forEach {
                                            val type = TypeListEntity()
                                            AppDatabase.getDBInstance()?.typeListDao()?.insert(type.apply {
                                                type_id = it.id
                                                name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()

                                            if (!isFromRefresh)
                                                showTypeDialog(AppDatabase.getDBInstance()?.typeListDao()?.getAll() as ArrayList<TypeListEntity>)
                                            else
                                                getEntityTypeListApi(isFromRefresh)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()

                                    if (!isFromRefresh)
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    else
                                        getEntityTypeListApi(isFromRefresh)
                                }
                            } else if (response.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    getEntityTypeListApi(isFromRefresh)
                            } else {
                                progress_wheel.stopSpinning()

                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    getEntityTypeListApi(isFromRefresh)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()

                            if (!isFromRefresh)
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            else
                                getEntityTypeListApi(isFromRefresh)
                        })
        )
    }


    private fun showTypeDialog(typeList: ArrayList<TypeListEntity>) {
        TypeDialog.newInstance(typeList) { type: TypeListEntity ->
            type_TV.text = type.name
            typeId = type.type_id!!
        }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun getEntityTypeListApi(isFromRefresh: Boolean) {
        if (!isFromRefresh && !AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.entityList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as EntityResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.entity_type

                                if (list != null && list.isNotEmpty()) {

                                    if (isFromRefresh)
                                        AppDatabase.getDBInstance()?.entityDao()?.delete()

                                    doAsync {

                                        list.forEach {
                                            val entity = EntityTypeEntity()
                                            AppDatabase.getDBInstance()?.entityDao()?.insert(entity.apply {
                                                entity_id = it.id
                                                name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            if (!isFromRefresh)
                                                showEntityDialog(AppDatabase.getDBInstance()?.entityDao()?.getAll() as ArrayList<EntityTypeEntity>)
                                            else
                                                getPartyStatusListApi(isFromRefresh)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    if (!isFromRefresh)
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    else
                                        getPartyStatusListApi(isFromRefresh)
                                }
                            } else if (response.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    getPartyStatusListApi(isFromRefresh)
                            } else {
                                progress_wheel.stopSpinning()
                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    getPartyStatusListApi(isFromRefresh)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            if (!isFromRefresh)
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            else
                                getPartyStatusListApi(isFromRefresh)
                        })
        )
    }

    private fun showEntityDialog(list: ArrayList<EntityTypeEntity>) {
        EntityTypeDialog.newInstance(list) {
            tv_entity.text = it.name
            entityId = it.entity_id!!
        }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun getPartyStatusListApi(isFromRefresh: Boolean) {
        if (!isFromRefresh && !AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.partyStatusList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as PartyStatusResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.party_status

                                if (list != null && list.isNotEmpty()) {

                                    if (isFromRefresh)
                                        AppDatabase.getDBInstance()?.partyStatusDao()?.delete()

                                    doAsync {

                                        list.forEach {
                                            val party = PartyStatusEntity()
                                            AppDatabase.getDBInstance()?.partyStatusDao()?.insert(party.apply {
                                                party_status_id = it.id
                                                name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            if (!isFromRefresh)
                                                showPartyStatusDialog(AppDatabase.getDBInstance()?.partyStatusDao()?.getAll() as ArrayList<PartyStatusEntity>)
                                            else
                                                getRetailerListApi(isFromRefresh)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    if (!isFromRefresh)
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    else
                                        getRetailerListApi(isFromRefresh)
                                }
                            } else if (response.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    getRetailerListApi(isFromRefresh)
                            } else {
                                progress_wheel.stopSpinning()
                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    getRetailerListApi(isFromRefresh)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            if (!isFromRefresh)
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            else
                                getRetailerListApi(isFromRefresh)
                        })
        )
    }

    private fun showPartyStatusDialog(list: ArrayList<PartyStatusEntity>) {
        PartyStatusDialog.newInstance(list) {
            tv_party.text = it.name
            partyStatusId = it.party_status_id!!
        }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun getRetailerListApi(isFromRefresh: Boolean) {
        if (!isFromRefresh && !AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.retailerList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as RetailerListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.retailer_list

                                if (list != null && list.isNotEmpty()) {

                                    if (isFromRefresh)
                                        AppDatabase.getDBInstance()?.retailerDao()?.delete()

                                    doAsync {

                                        list.forEach {
                                            val retailer = RetailerEntity()
                                            AppDatabase.getDBInstance()?.retailerDao()?.insert(retailer.apply {
                                                retailer_id = it.id
                                                name = it.name
                                                type_id = it.type_id
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            if (!isFromRefresh) {
                                                if (addShopData.type != "11") {
                                                    if (dealerId.isNotEmpty()) {
                                                        val list_ = AppDatabase.getDBInstance()?.retailerDao()?.getItemTypeWise(dealerId) as java.util.ArrayList<RetailerEntity>
                                                        if (list_ != null && list_.isNotEmpty())
                                                            showRetailerListDialog(list_)
                                                        else
                                                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                                                    } else
                                                        showRetailerListDialog(AppDatabase.getDBInstance()?.retailerDao()?.getAll() as ArrayList<RetailerEntity>)
                                                }
                                                else if (addShopData.type == "11") {
                                                    val list_ = AppDatabase.getDBInstance()?.retailerDao()?.getAll()?.filter {
                                                        it.retailer_id == "2"
                                                    }

                                                    if (list_ != null && list_.isNotEmpty())
                                                        showRetailerListDialog(list_ as ArrayList<RetailerEntity>)
                                                    else
                                                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                                                }
                                                else
                                                    showRetailerListDialog(AppDatabase.getDBInstance()?.retailerDao()?.getAll() as ArrayList<RetailerEntity>)
                                            }
                                            else
                                                getDealerListApi(isFromRefresh)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    if (!isFromRefresh)
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    else
                                        getDealerListApi(isFromRefresh)
                                }
                            } else if (response.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    getDealerListApi(isFromRefresh)
                            } else {
                                progress_wheel.stopSpinning()
                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    getDealerListApi(isFromRefresh)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            if (!isFromRefresh)
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            else
                                getDealerListApi(isFromRefresh)
                        })
        )
    }

    private fun showRetailerListDialog(list: ArrayList<RetailerEntity>) {
        RetailerListDialog.newInstance(list) {
            retailer_TV.text = it.name
            retailerId = it.retailer_id!!

            if (retailerId == "1")
                rl_entity_main.visibility = View.VISIBLE
            else {
                entityId = ""
                tv_entity.text = ""
                rl_entity_main.visibility = View.GONE
            }

        }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun getDealerListApi(isFromRefresh: Boolean) {
        if (!isFromRefresh && !AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.dealerList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as DealerListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.dealer_list

                                if (list != null && list.isNotEmpty()) {

                                    if (isFromRefresh)
                                        AppDatabase.getDBInstance()?.dealerDao()?.delete()

                                    doAsync {

                                        list.forEach {
                                            val dealer = DealerEntity()
                                            AppDatabase.getDBInstance()?.dealerDao()?.insert(dealer.apply {
                                                dealer_id = it.id
                                                name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            if (!isFromRefresh)
                                                showDealerListDialog(AppDatabase.getDBInstance()?.dealerDao()?.getAll() as ArrayList<DealerEntity>)
                                            else
                                                getBeatListApi(isFromRefresh)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    if (!isFromRefresh)
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    else
                                        getBeatListApi(isFromRefresh)
                                }
                            } else if (response.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    getBeatListApi(isFromRefresh)
                            } else {
                                progress_wheel.stopSpinning()
                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    getBeatListApi(isFromRefresh)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            if (!isFromRefresh)
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            else
                                getBeatListApi(isFromRefresh)
                        })
        )
    }

    private fun showDealerListDialog(list: ArrayList<DealerEntity>) {
        DealerListDialog.newInstance(list) {
            dealer_TV.text = it.name
            dealerId = it.dealer_id!!
            retailerId = ""
            retailer_TV.text = ""
            assignedToDDId = ""
            assigned_to_dd_TV.text = ""
        }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun getBeatListApi(isFromRefresh: Boolean) {
        if (!isFromRefresh && !AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.beatList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BeatListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.beat_list

                                if (list != null && list.isNotEmpty()) {

                                    if (isFromRefresh)
                                        AppDatabase.getDBInstance()?.beatDao()?.delete()

                                    doAsync {

                                        list.forEach {
                                            val beat = BeatEntity()
                                            AppDatabase.getDBInstance()?.beatDao()?.insert(beat.apply {
                                                beat_id = it.id
                                                name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            if (!isFromRefresh)
                                                showBeatListDialog(AppDatabase.getDBInstance()?.beatDao()?.getAll() as ArrayList<BeatEntity>)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    if (!isFromRefresh)
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            } else if (response.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            } else {
                                progress_wheel.stopSpinning()
                                if (!isFromRefresh)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            if (!isFromRefresh)
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            else
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                        })
        )
    }

    private fun showBeatListDialog(list: ArrayList<BeatEntity>) {
        BeatListDialog.newInstance(list) {
            beat_TV.text = it.name
            beatId = it.beat_id!!
        }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }


    private fun clearFocus() {
        shopName.clearFocus()
        shopAddress.clearFocus()
        shopPin.clearFocus()
        shopContactNumber.clearFocus()
        ownwr_name_TV.clearFocus()
        shopOwnerEmail.clearFocus()
        et_booking_amount.clearFocus()
    }

    private fun showOtpVerificationDialog(shop_id: String, isShowTimer: Boolean) {
        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(shop_id)
        OTPVerificationDialog.getInstance(shop.ownerContactNumber, isShowTimer, shop.shopName, object : OTPVerificationDialog.OnOTPButtonClickListener {
            override fun onResentClick() {
                callOtpSentApi(shop_id)
            }

            override fun onCancelClick() {
                /*(mContext as DashboardActivity).onBackPressed()
                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, shop_id)*/
            }

            override fun onOkButtonClick(otp: String) {

                val distance = LocationWizard.getDistance(shop.shopLat, shop.shopLong, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())

                if (distance * 1000 <= 20)
                    callOtpVerifyApi(otp, shop_id)
                else
                    (mContext as DashboardActivity).showSnackMessage("OTP can be verified only from the shop.")


                //callOtpVerifyApi(otp, shop_id)
            }
        }).show((mContext as DashboardActivity).supportFragmentManager, "OTPVerificationDialog")
    }

    private fun callOtpSentApi(shop_id: String) {
        val repository = OtpSentRepoProvider.otpSentRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.otpSent(shop_id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val addShopResult = result as BaseResponse
                            progress_wheel.stopSpinning()
                            /*if (addShopResult.status == NetworkConstant.SUCCESS) {
                                (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                showOtpVerificationDialog(shop_id)

                            } else {
                                (mContext as DashboardActivity).showSnackMessage("OTP sent failed")
                                (mContext as DashboardActivity).onBackPressed()
                            }*/

                            showOtpVerificationDialog(shop_id, true)

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            /*(mContext as DashboardActivity).showSnackMessage("OTP sent failed")
                            (mContext as DashboardActivity).onBackPressed()*/
                            showOtpVerificationDialog(shop_id, true)
                        })
        )
    }

    private fun callOtpVerifyApi(otp: String, shop_id: String) {
        val repository = OtpVerificationRepoProvider.otpVerifyRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.otpVerify(shop_id, otp)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val addShopResult = result as BaseResponse
                            progress_wheel.stopSpinning()
                            if (addShopResult.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsOtpVerified("true", shop_id)
                                (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                //(mContext as DashboardActivity).onBackPressed()

                                iv_otp_check.visibility = View.VISIBLE
                                tv_verified.text = getString(R.string.verified)
                                tv_verified.setOnClickListener(null)
                                ll_verified.visibility = View.VISIBLE
                                shopContactNumber.isEnabled = false

                            } else {
                                (mContext as DashboardActivity).showSnackMessage("OTP verification failed.")
                                showOtpVerificationDialog(shop_id, false)
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("OTP verification failed.")
                            showOtpVerificationDialog(shop_id, false)
                        })
        )
    }


    private var permissionUtils: PermissionUtils? = null
    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                if (isDocDegree == 1)
                    showPictureDialog()
                else
                    launchCamera()
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkValidation() {
        val list = AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(shopContactNumber.text.toString().trim())
        if (TextUtils.isEmpty(shopName.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage("Please enter " + Pref.shopText + " name")
        else if (TextUtils.isEmpty(shopAddress.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage("Please enter " + Pref.shopText + " address")
        else if (TextUtils.isEmpty(shopPin.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage("Please enter " + Pref.shopText + " pin")
        else if (!Pref.isCustomerFeatureEnable && TextUtils.isEmpty(ownwr_name_TV.text.toString().trim())) {
            if (addShopData.type != "7")
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.ownername_error))
            else
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.contactname_error))
        }
        else if (TextUtils.isEmpty(shopContactNumber.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.numberblank_error))
        else if (!AppUtils.isValidateMobile(shopContactNumber.text.toString()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.numbervalid_error))
        else if (!shopContactNumber.text.toString().trim().startsWith("6") && !shopContactNumber.text.toString().trim().startsWith("7") &&
                !shopContactNumber.text.toString().trim().startsWith("8") && !shopContactNumber.text.toString().trim().startsWith("9"))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_phn_no), 3000)
        else if (!TextUtils.isEmpty(shopOwnerEmail.text.toString().trim()) && !AppUtils.isValidEmail(shopOwnerEmail.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.email_error))
        /*else if (TextUtils.isEmpty(shopName.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.shopname_error))
        else if (TextUtils.isEmpty(shopName.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.shopname_error))
        else if (TextUtils.isEmpty(shopName.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.shopname_error))*/
        else if (Pref.isShowDealerForDD && (addShopData.type == "1" || addShopData.type == "4") && dealerId.isEmpty())
            (mContext as DashboardActivity).showSnackMessage("Please select any GPTPL/Distributor")
        else if (Pref.isShowRetailerEntity && addShopData.type == "1" && retailerId.isEmpty())
            (mContext as DashboardActivity).showSnackMessage("Please select any retailer/entity")
        else if (Pref.isShowRetailerEntity && addShopData.type == "11" && retailerId.isEmpty())
            (mContext as DashboardActivity).showSnackMessage("Please select retailer")
        else if (Pref.willShowPartyStatus && partyStatusId.isEmpty())
            (mContext as DashboardActivity).showSnackMessage("Please select any party status")
        else if (Pref.willShowEntityTypeforShop && addShopData.type == "1" && retailerId == "1" && entityId.isEmpty())
            (mContext as DashboardActivity).showSnackMessage("Please select any entity type")
        else if ((addShopData.type == "1" || addShopData.type == "5")
                && TextUtils.isEmpty(assigned_to_pp_TV.text.toString().trim())) {
            //if (TextUtils.isEmpty(assigned_to_pp_TV.text.toString().trim())) {
            (mContext as DashboardActivity).showSnackMessage("Please select assigned to " + Pref.ppText)
            /*} else if (TextUtils.isEmpty(assigned_to_dd_TV.text.toString().trim()))
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.assigned_to_dd_error))*/
        }
        else if (addShopData.type == "11" && assignedToShopId.isEmpty())
            (mContext as DashboardActivity).showSnackMessage("Please select assigned to " + Pref.shopText)
        else if ((addShopData.type == "1" || addShopData.type == "5")
                && TextUtils.isEmpty(assigned_to_dd_TV.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage("Please select assigned to " + Pref.ddText)
        else if (addShopData.type == "4" && TextUtils.isEmpty(assigned_to_pp_TV.text.toString().trim())) {
            (mContext as DashboardActivity).showSnackMessage("Please select assigned to " + Pref.ppText)
        } else if (addShopData.type == "10" && Pref.isDDMandatoryForMeeting && TextUtils.isEmpty(assigned_to_dd_TV.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage("Please select assigned to " + Pref.ddText)
        else if (addShopData.type == "5" && TextUtils.isEmpty(amount_ET.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.amount_error))
        else if (addShopData.type == "5" && amount_ET.text.toString().trim().toInt() == 0)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.valid_amount_error))
        else if (Pref.isAreaVisible && (Pref.isAreaMandatoryInPartyCreation && TextUtils.isEmpty(areaId)))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_area))
        else if (Pref.isCustomerFeatureEnable && TextUtils.isEmpty(modelId))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_model))
        else if (Pref.isCustomerFeatureEnable && TextUtils.isEmpty(stageId))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_stage))
        else if ((addShopData.type == "6" || addShopData.type == "7") && ll_extra_info.visibility == View.VISIBLE &&
                TextUtils.isEmpty(et_dir_name_value.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_director_name))
        else if ((addShopData.type == "6" || addShopData.type == "7") && ll_extra_info.visibility == View.VISIBLE &&
                TextUtils.isEmpty(tv_family_dob.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_family_member_dob))
        else if ((addShopData.type == "6" || addShopData.type == "7") && ll_extra_info.visibility == View.VISIBLE &&
                TextUtils.isEmpty(et_person_name_value.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_person_name))
        else if ((addShopData.type == "6" || addShopData.type == "7") && ll_extra_info.visibility == View.VISIBLE &&
                TextUtils.isEmpty(et_person_no_value.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_phn_no))
        else if ((addShopData.type == "6" || addShopData.type == "7") && ll_extra_info.visibility == View.VISIBLE &&
                !AppUtils.isValidateMobile(et_person_no_value.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.numbervalid_error))
        else if ((addShopData.type == "6" || addShopData.type == "7") && ll_extra_info.visibility == View.VISIBLE &&
                !et_person_no_value.text.toString().trim().startsWith("6") && !et_person_no_value.text.toString().trim().startsWith("7") &&
                !et_person_no_value.text.toString().trim().startsWith("8") && !et_person_no_value.text.toString().trim().startsWith("9"))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_phn_no), 3000)
        else if ((addShopData.type == "8") && TextUtils.isEmpty(tv_degree_img_link.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_capture_doc_pic))
        else if ((addShopData.type == "8") && TextUtils.isEmpty(et_specialization.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_speciallization))
        else if ((addShopData.type == "8") && TextUtils.isEmpty(et_patient_count.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_patient_count))
        else if ((addShopData.type == "8") && TextUtils.isEmpty(et_category.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_cateogory))
        else if ((addShopData.type == "8") && TextUtils.isEmpty(tv_doc_family_dob.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_family_member_dob))
        else if ((addShopData.type == "8") && TextUtils.isEmpty(et_doc_add.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_location))
        else if ((addShopData.type == "8") && TextUtils.isEmpty(et_doc_pincode.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_pincode))
        else if ((addShopData.type == "8") && !iv_yes.isSelected && !iv_no.isSelected)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_chamber))
        else if ((addShopData.type == "8") && TextUtils.isEmpty(et_remarks.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_remarks))
        else if ((addShopData.type == "8") && TextUtils.isEmpty(et_chemist_name.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_chemist_name))
        else if ((addShopData.type == "8") && TextUtils.isEmpty(et_chemist_add.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_chemist_address))
        else if ((addShopData.type == "8") && TextUtils.isEmpty(et_chemist_pincode.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_chemist_pincode))
        else if (addShopData.type == "7" && TextUtils.isEmpty(assigned_to_pp_TV.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage("Please select assigned to")
        else if (addShopData.is_otp_verified.equals("false", ignoreCase = true) && list != null && list.size > 0) {

            for (i in list.indices) {
                if (list[i].shop_id != addShopData.shop_id) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.contact_number_exist))
                    break
                } else {
                    if (Pref.willMoreVisitUpdateOptional && ll_extra_info.visibility == View.GONE)
                        showAddMoreInfoAlertDialog()
                    else {
                        editShop()
                    }
                    break
                }
            }
        } else {
            //editShop()
            if (Pref.willMoreVisitUpdateOptional && ll_extra_info.visibility == View.GONE)
                showAddMoreInfoAlertDialog()
            else {
                editShop()
            }
        }
    }

    private fun showAddMoreInfoAlertDialog() {
        AppUtils.isShopAdded = true
        CommonDialogSingleBtn.getInstance("Action", "Wish to update more details for the visit?", "Confirm", object : OnDialogClickListener {
            override fun onOkClick() {
                ll_extra_info.visibility = View.VISIBLE
                scroll_view.smoothScrollTo(0, ll_extra_info.scrollY)
                //ownerEmail.imeOptions = EditorInfo.IME_ACTION_NEXT
            }
        }, object : CommonDialogSingleBtn.OnCrossClickListener {
            override fun onCrossClick() {
                editShop()
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "CommonDialogSingleBtn")
    }

    private fun editShop() {
        addShopData.shopName = shopName.text.toString().trim()
        addShopData.address = shopAddress.text.toString().trim()
        addShopData.pinCode = shopPin.text.toString().trim()
        addShopData.ownerContactNumber = shopContactNumber.text.toString().trim()
        addShopData.ownerEmailId = shopOwnerEmail.text.toString().trim()
        addShopData.ownerName = ownwr_name_TV.text.toString().trim()
        /*addShopData.dateOfBirth = ownwr_dob_TV.text.toString().trim()
        addShopData.dateOfAniversary = ownwr_ani_TV.text.toString().trim()*/
        addShopData.type = type

        if (type == "4") {
            assignedToDDId = ""
            addShopData.amount = ""
        } else if (type == "3" || type == "2") {
            assignedToDDId = ""
            assignedToPPId = ""
            addShopData.amount = ""
        } else if (type == "1")
            addShopData.amount = ""
        else
            addShopData.amount = amount_ET.text.toString().trim()

        /*if (!TextUtils.isEmpty(addShopData.assigned_to_dd_id)){
            if (TextUtils.isEmpty(assignedToDDId))
                AppDatabase.getDBInstance()?.ddListDao()?.deleteDDId(addShopData.assigned_to_dd_id)
            else if(!assignedToDDId.equals(addShopData.assigned_to_dd_id, ignoreCase = true)) {
                AppDatabase.getDBInstance()?.ddListDao()?.updateDDName(addShopData.assigned_to_dd_id, )
            }
        }*/

        addShopData.assigned_to_dd_id = assignedToDDId
        addShopData.assigned_to_pp_id = assignedToPPId
        addShopData.area_id = areaId

        addShopData.model_id = modelId
        addShopData.primary_app_id = primaryAppId
        addShopData.secondary_app_id = secondaryAppId
        addShopData.lead_id = leadId
        addShopData.funnel_stage_id = funnelStageId
        addShopData.stage_id = stageId

        if (TextUtils.isEmpty(et_booking_amount.text.toString().trim()))
            addShopData.booking_amount = ""
        else
            addShopData.booking_amount = et_booking_amount.text.toString().trim()

        addShopData.type_id = typeId

        if (!TextUtils.isEmpty(imagePath))
            addShopData.shopImageLocalPath = imagePath
        else
            addShopData.shopImageLocalPath = ""
        addShopData.isEditUploaded = 0


        addShopData.director_name = et_dir_name_value.text.toString().trim()
        addShopData.person_name = et_person_name_value.text.toString().trim()
        addShopData.person_no = et_person_no_value.text.toString().trim()

        addShopData.specialization = et_specialization.text.toString().trim()
        addShopData.category = et_category.text.toString().trim()
        addShopData.doc_address = et_doc_add.text.toString().trim()
        addShopData.doc_pincode = et_doc_pincode.text.toString().trim()

        if (iv_yes.isSelected)
            addShopData.chamber_status = 1
        else
            addShopData.chamber_status = 0

        addShopData.remarks = et_remarks.text.toString().trim()
        addShopData.chemist_name = et_chemist_name.text.toString().trim()
        addShopData.chemist_address = et_chemist_add.text.toString().trim()
        addShopData.chemist_pincode = et_chemist_pincode.text.toString().trim()
        addShopData.assistant_no = et_assistant_no.text.toString().trim()
        addShopData.patient_count = et_patient_count.text.toString().trim()
        addShopData.assistant_name = et_assistant_name.text.toString().trim()
        addShopData.doc_degree = degreeImgLink
        addShopData.entity_id = entityId
        addShopData.party_status_id = partyStatusId
        addShopData.retailer_id = retailerId
        addShopData.dealer_id = dealerId
        addShopData.beat_id = beatId
        addShopData.assigned_to_shop_id = assignedToShopId

        if (TextUtils.isEmpty(addShopData.actual_address)) {
            var address_ = LocationWizard.getAdressFromLatlng(mContext, addShopData.shopLat, addShopData.shopLong)
            XLog.e("Actual Shop address (Update address)======> $address_")

            if (address_.contains("http"))
                address_ = "Unknown"

            addShopData.actual_address = address_
        }

        if (!TextUtils.isEmpty(type) && type == "2") {
            val assignedToppObj = AppDatabase.getDBInstance()?.ppListDao()?.getSingleValue(addShopData.shop_id)
            if (assignedToppObj == null) {
                val assignToPP = AssignToPPEntity()
                assignToPP.pp_id = addShopData.shop_id
                assignToPP.pp_name = addShopData.shopName
                assignToPP.pp_phn_no = addShopData.ownerContactNumber
                AppDatabase.getDBInstance()?.ppListDao()?.insert(assignToPP)
            }
        } else if (!TextUtils.isEmpty(type) && type == "4") {
            val assignedToddObj = AppDatabase.getDBInstance()?.ddListDao()?.getSingleValue(addShopData.shop_id)
            if (assignedToddObj == null) {
                val assignToPP = AssignToDDEntity()
                assignToPP.dd_id = addShopData.shop_id
                assignToPP.dd_name = addShopData.shopName
                assignToPP.dd_phn_no = addShopData.ownerContactNumber
                //assignToPP.pp_id = addShopData.assigned_to_pp_id
                AppDatabase.getDBInstance()?.ddListDao()?.insert(assignToPP)
            } else {
                AppDatabase.getDBInstance()?.ddListDao()?.updatePPId(addShopData.shop_id, addShopData.assigned_to_pp_id)
            }
        }
        else if (!TextUtils.isEmpty(type) && type == "1") {
            val assignedToShopObj = AppDatabase.getDBInstance()?.assignToShopDao()?.getSingleValue(addShopData.shop_id)
            if (assignedToShopObj == null) {
                val assignToShop = AssignToShopEntity()
                AppDatabase.getDBInstance()?.assignToShopDao()?.insert(assignToShop.apply {
                    assigned_to_shop_id = addShopData.shop_id
                    name = addShopData.shopName
                    phn_no = addShopData.ownerContactNumber
                    type_id = addShopData.retailer_id
                })
            }
            else
                AppDatabase.getDBInstance()?.assignToShopDao()?.updateTypeId(addShopData.shop_id, addShopData.retailer_id)
        }
        /*14-12-2021*/
        addShopData.agency_name = agency_name_TV.text.toString().trim()

        /*10-02-2022*/
        addShopData.project_name = project_name_TV.text.toString().trim()
        addShopData.landline_number = land_contact_no_TV.text.toString().trim()
        addShopData.alternateNoForCustomer = alternate_no_TV.text.toString().trim()
        addShopData.whatsappNoForCustomer = whatsappp_no_TV.text.toString().trim()

       /*GSTIN & PAN NUMBER*/
        if(Pref.IsGSTINPANEnableInShop) {
            var gstinStr : String = shopGSTIN.text!!.trim().toString()
            if(!gstinStr.equals("N.A")) {
                if (!shopGSTIN.text!!.trim().isBlank()) {
                    if (AppUtils.isValidGSTINCardNo(shopGSTIN.text.toString())) {
                        addShopData.gstN_Number = shopGSTIN.text.toString().trim()
                    } else {
                        BaseActivity.isApiInitiated = false
                        openDialogPopup(
                            "Hi ${Pref.user_name} !",
                            "Please provide a valid GSTIN number as per the below format\n" +
                                    "GSTIN Format : 19ABCDE1234E1ZT"
                        )
//                (mContext as DashboardActivity).showSnackMessage("Please use valid GSTIN Number")
                        return
                    }
                }
            }
            var panStr : String = shopPancard.text!!.trim().toString()
            if(!panStr.equals("N.A")) {
                if (!(shopPancard.text!!.trim().isBlank()) ) {
                    if (AppUtils.isValidPanCardNo(shopPancard.text.toString()) && !shopPancard.text!!.trim().equals("N.A")) {
                        addShopData.shopOwner_PAN = shopPancard.text.toString().trim()
                    } else {
                        BaseActivity.isApiInitiated = false
                        openDialogPopup("Hi ${Pref.user_name} !","Please provide a valid PAN number as per the below format\n" +
                                "PAN Format : ADBCE1234G")
//                    (mContext as DashboardActivity).showSnackMessage("Please use valid PAN Number")
                        return
                    }
                }
            }

        }




        AppDatabase.getDBInstance()?.addShopEntryDao()?.updateShopDao(addShopData)

        convertToReqAndApiCall()
    }

    private fun convertToReqAndApiCall() {
        if (Pref.user_id == null || Pref.user_id == "" || Pref.user_id == " ") {
            (mContext as DashboardActivity).showSnackMessage("Please login again")
            BaseActivity.isApiInitiated = false
            return
        }

        val addShopReqData = AddShopRequestData()
        addShopReqData.session_token = Pref.session_token
        addShopReqData.address = addShopData.address
        addShopReqData.owner_contact_no = addShopData.ownerContactNumber
        addShopReqData.owner_email = addShopData.ownerEmailId
        addShopReqData.owner_name = addShopData.ownerName
        addShopReqData.pin_code = addShopData.pinCode
        addShopReqData.shop_lat = addShopData.shopLat.toString()
        addShopReqData.shop_long = addShopData.shopLong.toString()
        addShopReqData.shop_name = addShopData.shopName.toString()
        addShopReqData.shop_id = addShopData.shop_id
        addShopReqData.added_date = ""
        addShopReqData.user_id = Pref.user_id
        addShopReqData.type = addShopData.type
        addShopReqData.assigned_to_pp_id = addShopData.assigned_to_pp_id
        addShopReqData.assigned_to_dd_id = addShopData.assigned_to_dd_id
        addShopReqData.dob = dob
        addShopReqData.date_aniversary = doa
        addShopReqData.amount = addShopData.amount
        addShopReqData.area_id = addShopData.area_id
        /*val addShop = AddShopRequest()
        addShop.data = addShopReqData*/

        addShopReqData.model_id = addShopData.model_id
        addShopReqData.primary_app_id = addShopData.primary_app_id
        addShopReqData.secondary_app_id = addShopData.secondary_app_id
        addShopReqData.lead_id = addShopData.lead_id
        addShopReqData.stage_id = addShopData.stage_id
        addShopReqData.funnel_stage_id = addShopData.funnel_stage_id
        addShopReqData.booking_amount = addShopData.booking_amount
        addShopReqData.type_id = addShopData.type_id

        addShopReqData.family_member_dob = family_dob
        addShopReqData.addtional_doa = addl_doa
        addShopReqData.addtional_dob = addl_dob
        addShopReqData.director_name = addShopData.director_name
        addShopReqData.key_person_name = addShopData.person_name
        addShopReqData.phone_no = addShopData.person_no

        addShopReqData.specialization = addShopData.specialization
        addShopReqData.category = addShopData.category
        addShopReqData.doc_address = addShopData.doc_address
        addShopReqData.doc_pincode = addShopData.doc_pincode
        addShopReqData.is_chamber_same_headquarter = addShopData.chamber_status.toString()
        addShopReqData.is_chamber_same_headquarter_remarks = addShopData.remarks
        addShopReqData.chemist_name = addShopData.chemist_name
        addShopReqData.chemist_address = addShopData.chemist_address
        addShopReqData.chemist_pincode = addShopData.chemist_pincode
        addShopReqData.assistant_contact_no = addShopData.assistant_no
        addShopReqData.average_patient_per_day = addShopData.patient_count
        addShopReqData.assistant_name = addShopData.assistant_name
        addShopReqData.doc_family_member_dob = doc_family_dob
        addShopReqData.assistant_dob = assistant_dob
        addShopReqData.assistant_doa = assistant_doa
        addShopReqData.assistant_family_dob = assistant_family_dob
        addShopReqData.entity_id = addShopData.entity_id
        addShopReqData.party_status_id = addShopData.party_status_id
        addShopReqData.retailer_id = addShopData.retailer_id
        addShopReqData.dealer_id = addShopData.dealer_id
        addShopReqData.beat_id = addShopData.beat_id
        addShopReqData.assigned_to_shop_id = addShopData.assigned_to_shop_id
        addShopReqData.actual_address = addShopData.actual_address

        if (AppUtils.isOnline(mContext)) {

            if (BaseActivity.isApiInitiated)
                return

            BaseActivity.isApiInitiated = true


            /*14-12-2021*/
//            addShopReqData.agency_name =addShopData.agency_name!!
            if (addShopData.agency_name!=null && !addShopData.agency_name.equals(""))
                addShopReqData.agency_name =addShopData.agency_name!!
            else
                addShopReqData.agency_name = ""
           /*10-02-2022*/

            if (addShopData.project_name!=null && !addShopData.project_name.equals(""))
                addShopReqData.project_name =addShopData.project_name!!
            else
                addShopReqData.project_name = ""

            if (addShopData.landline_number!=null && !addShopData.landline_number.equals(""))
                addShopReqData.landline_number = addShopData.landline_number!!
            else
                addShopReqData.landline_number =  ""

            if (addShopData.alternateNoForCustomer!=null && !addShopData.alternateNoForCustomer.equals(""))
                addShopReqData.alternateNoForCustomer =addShopData.alternateNoForCustomer!!
            else
                addShopReqData.alternateNoForCustomer = ""

            if (addShopData.whatsappNoForCustomer!=null && !addShopData.whatsappNoForCustomer.equals(""))
                addShopReqData.whatsappNoForCustomer =addShopData.whatsappNoForCustomer!!
            else
                addShopReqData.whatsappNoForCustomer = ""

            /*GSTIN & PAN NUMBER*/
            if (addShopData.gstN_Number!=null && !addShopData.gstN_Number.equals(""))
                addShopReqData.GSTN_Number =addShopData.gstN_Number!!
            else
                addShopReqData.GSTN_Number = ""

            if (addShopData.shopOwner_PAN!=null && !addShopData.shopOwner_PAN.equals(""))
                addShopReqData.ShopOwner_PAN =addShopData.shopOwner_PAN!!
            else
                addShopReqData.ShopOwner_PAN = ""

            callEditShopApi(addShopReqData, addShopData.shopImageLocalPath)
        } else {
            (mContext as DashboardActivity).showSnackMessage("Shop edited successfully")
            (mContext as DashboardActivity).onBackPressed()
        }
    }

    private fun callEditShopApi(addShopReqData: AddShopRequestData, shopImageLocalPath: String?) {

        XLog.d("=====EditShop Input Params (Shop Details)======")
        XLog.d("shop id====> " + addShopReqData.shop_id)
        val index = addShopReqData.shop_id!!.indexOf("_")
        XLog.d("decoded shop id====> " + addShopReqData.user_id + "_" + AppUtils.getDate(addShopReqData.shop_id!!.substring(index + 1, addShopReqData.shop_id!!.length).toLong()))
        XLog.d("shop added date====> " + addShopReqData.added_date)
        XLog.d("shop address====> " + addShopReqData.address)
        XLog.d("assigned to dd id====> " + addShopReqData.assigned_to_dd_id)
        XLog.d("assigned to pp id=====> " + addShopReqData.assigned_to_pp_id)
        XLog.d("date aniversery=====> " + addShopReqData.date_aniversary)
        XLog.d("dob====> " + addShopReqData.dob)
        XLog.d("shop owner phn no===> " + addShopReqData.owner_contact_no)
        XLog.d("shop owner email====> " + addShopReqData.owner_email)
        XLog.d("shop owner name====> " + addShopReqData.owner_name)
        XLog.d("shop pincode====> " + addShopReqData.pin_code)
        XLog.d("session token====> " + addShopReqData.session_token)
        XLog.d("shop lat====> " + addShopReqData.shop_lat)
        XLog.d("shop long===> " + addShopReqData.shop_long)
        XLog.d("shop name====> " + addShopReqData.shop_name)
        XLog.d("shop type===> " + addShopReqData.type)
        XLog.d("user id====> " + addShopReqData.user_id)
        XLog.d("amount=======> " + addShopReqData.amount)
        XLog.d("area id=======> " + addShopReqData.area_id)
        XLog.d("model id=======> " + addShopReqData.model_id)
        XLog.d("primary app id=======> " + addShopReqData.primary_app_id)
        XLog.d("secondary app id=======> " + addShopReqData.secondary_app_id)
        XLog.d("lead id=======> " + addShopReqData.lead_id)
        XLog.d("stage id=======> " + addShopReqData.stage_id)
        XLog.d("funnel stage id=======> " + addShopReqData.funnel_stage_id)
        XLog.d("booking amount=======> " + addShopReqData.booking_amount)
        XLog.d("type id=======> " + addShopReqData.type_id)
        XLog.d("shop image path====> $shopImageLocalPath")
        XLog.d("director name=======> " + addShopReqData.director_name)
        XLog.d("family member dob=======> " + addShopReqData.family_member_dob)
        XLog.d("key person's name=======> " + addShopReqData.key_person_name)
        XLog.d("phone no=======> " + addShopReqData.phone_no)
        XLog.d("additional dob=======> " + addShopReqData.addtional_dob)
        XLog.d("additional doa=======> " + addShopReqData.addtional_doa)
        XLog.d("doctor family member dob=======> " + addShopReqData.doc_family_member_dob)
        XLog.d("specialization=======> " + addShopReqData.specialization)
        XLog.d("average patient count per day=======> " + addShopReqData.average_patient_per_day)
        XLog.d("category=======> " + addShopReqData.category)
        XLog.d("doctor address=======> " + addShopReqData.doc_address)
        XLog.d("doctor pincode=======> " + addShopReqData.doc_pincode)
        XLog.d("chambers or hospital under same headquarter=======> " + addShopReqData.is_chamber_same_headquarter)
        XLog.d("chamber related remarks=======> " + addShopReqData.is_chamber_same_headquarter_remarks)
        XLog.d("chemist name=======> " + addShopReqData.chemist_name)
        XLog.d("chemist name=======> " + addShopReqData.chemist_address)
        XLog.d("chemist pincode=======> " + addShopReqData.chemist_pincode)
        XLog.d("assistant name=======> " + addShopReqData.assistant_name)
        XLog.d("assistant contact no=======> " + addShopReqData.assistant_contact_no)
        XLog.d("assistant dob=======> " + addShopReqData.assistant_dob)
        XLog.d("assistant date of anniversary=======> " + addShopReqData.assistant_doa)
        XLog.d("assistant family dob=======> " + addShopReqData.assistant_family_dob)
        XLog.d("doctor degree image path=======> $degreeImgLink")
        XLog.d("entity id=======> " + addShopReqData.entity_id)
        XLog.d("party status id=======> " + addShopReqData.party_status_id)
        XLog.d("retailer id=======> " + addShopReqData.retailer_id)
        XLog.d("dealer id=======> " + addShopReqData.dealer_id)
        XLog.d("beat id=======> " + addShopReqData.beat_id)
        XLog.d("assigned to shop id=======> " + addShopReqData.assigned_to_shop_id)
        XLog.d("actual_address=======> " + addShopReqData.actual_address)
        XLog.d("================================================")

        progress_wheel.spin()

        if (TextUtils.isEmpty(shopImageLocalPath) && TextUtils.isEmpty(degreeImgLink)) {
            val repository = EditShopRepoProvider.provideEditShopWithoutImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.editShop(addShopReqData)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("Edit Shop : " + ", SHOP: " + addShopReqData.shop_name + ", RESPONSE:" + result.message)
                                when (addShopResult.status) {
                                    NetworkConstant.SUCCESS -> {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsEditUploaded(1, addShopReqData.shop_id)
                                        progress_wheel.stopSpinning()
                        //                                (mContext as DashboardActivity).showSnackMessage("SUCCESS")

                                        getAssignedPPListApi(true, addShopReqData.shop_id)


                                    }
                                    NetworkConstant.SESSION_MISMATCH -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).clearData()
                                        startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                        (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                        (mContext as DashboardActivity).finish()
                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage("Edited successfully")
                                        (mContext as DashboardActivity).onBackPressed()
                                        (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, addShopReqData.shop_id!!)
                                    }
                                }
                                BaseActivity.isApiInitiated = false
                            }, { error ->
                                BaseActivity.isApiInitiated = false
                                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                (mContext as DashboardActivity).showSnackMessage("Edited successfully")
                                (mContext as DashboardActivity).onBackPressed()
                                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, addShopReqData.shop_id!!)
                            })
            )
        }
        else {
            val repository = EditShopRepoProvider.provideEditShopRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShopWithImage(addShopReqData, shopImageLocalPath, degreeImgLink, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("Edit Shop : " + ", SHOP: " + addShopReqData.shop_name + ", RESPONSE:" + result.message)
                                when (addShopResult.status) {
                                    NetworkConstant.SUCCESS -> {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsEditUploaded(1, addShopReqData.shop_id)
                                        progress_wheel.stopSpinning()
                        //                                (mContext as DashboardActivity).showSnackMessage("SUCCESS")

                                        getAssignedPPListApi(true, addShopReqData.shop_id)


                                    }
                                    NetworkConstant.SESSION_MISMATCH -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).clearData()
                                        startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                        (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                        (mContext as DashboardActivity).finish()
                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage("Edited successfully")
                                        (mContext as DashboardActivity).onBackPressed()
                                        (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, addShopReqData.shop_id!!)
                                    }
                                }
                                BaseActivity.isApiInitiated = false
                            }, { error ->
                                BaseActivity.isApiInitiated = false
                                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                (mContext as DashboardActivity).showSnackMessage("Edited successfully")
                                (mContext as DashboardActivity).onBackPressed()
                                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, addShopReqData.shop_id!!)
                            })
            )
        }
    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems,
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        0 -> selectImageInAlbum()
                        1 -> launchCamera()
                    }
                })
        pictureDialog.show()
    }

    private fun launchCamera() {
        //if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity) && PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
        /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, (mContext as DashboardActivity).getPhotoFileUri(System.currentTimeMillis().toString() + ".png"))
        (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_CAMERA)*/
        //}

        (mContext as DashboardActivity).captureImage()
    }

    private fun selectImageInAlbum() {
        //if (PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
        val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_STORAGE)

        //}
    }

    private val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel()
    }

    private fun updateLabel() {
        when (isDOB) {
            "1" -> {
                addShopData.dateOfBirth = AppUtils.getFormattedDateForApi(myCalendar.time)
                ownwr_dob_TV.text = AppUtils.changeAttendanceDateFormat(AppUtils.getDobFormattedDate(myCalendar.time))
                dob = AppUtils.getDobFormattedDate(myCalendar.time)
            }
            "0" -> {
                addShopData.dateOfAniversary = AppUtils.getFormattedDateForApi(myCalendar.time)
                ownwr_ani_TV.text = AppUtils.changeAttendanceDateFormat(AppUtils.getDobFormattedDate(myCalendar.time))
                doa = AppUtils.getDobFormattedDate(myCalendar.time)
            }
            "2" -> {
                addShopData.family_member_dob = AppUtils.getFormattedDateForApi(myCalendar.time)
                tv_family_dob.text = AppUtils.changeAttendanceDateFormat(AppUtils.getDobFormattedDate(myCalendar.time))
                family_dob = AppUtils.getDobFormattedDate(myCalendar.time)
            }
            "3" -> {
                addShopData.add_dob = AppUtils.getFormattedDateForApi(myCalendar.time)
                tv_add_dob.text = AppUtils.changeAttendanceDateFormat(AppUtils.getDobFormattedDate(myCalendar.time))
                addl_dob = AppUtils.getFormattedDateForApi(myCalendar.time)
            }
            "4" -> {
                addShopData.add_doa = AppUtils.getFormattedDateForApi(myCalendar.time)
                tv_add_doa.text = AppUtils.changeAttendanceDateFormat(AppUtils.getDobFormattedDate(myCalendar.time))
                addl_doa = AppUtils.getDobFormattedDate(myCalendar.time)
            }
            "5" -> {
                addShopData.doc_family_dob = AppUtils.getFormattedDateForApi(myCalendar.time)
                tv_doc_family_dob.text = AppUtils.changeAttendanceDateFormat(AppUtils.getDobFormattedDate(myCalendar.time))
                doc_family_dob = AppUtils.getDobFormattedDate(myCalendar.time)
            }
            "6" -> {
                addShopData.assistant_dob = AppUtils.getFormattedDateForApi(myCalendar.time)
                tv_assistant_dob.text = AppUtils.changeAttendanceDateFormat(AppUtils.getDobFormattedDate(myCalendar.time))
                assistant_dob = AppUtils.getDobFormattedDate(myCalendar.time)
            }
            "7" -> {
                addShopData.assistant_doa = AppUtils.getFormattedDateForApi(myCalendar.time)
                tv_assistant_doa.text = AppUtils.changeAttendanceDateFormat(AppUtils.getDobFormattedDate(myCalendar.time))
                assistant_doa = AppUtils.getDobFormattedDate(myCalendar.time)
            }
            "8" -> {
                addShopData.assistant_family_dob = AppUtils.getFormattedDateForApi(myCalendar.time)
                tv_assistant_family_dob.text = AppUtils.changeAttendanceDateFormat(AppUtils.getDobFormattedDate(myCalendar.time))
                assistant_family_dob = AppUtils.getDobFormattedDate(myCalendar.time)
            }
        }
    }

    fun setImage(imgRealPath: Uri, fileSize: Long) {
        //Picasso.with(mContext).load(imgRealPath).into(shopLargeImg)

        if (isDocDegree == 0) {
            imagePath = imgRealPath.toString()
            Picasso.get()
                    .load(imgRealPath)
                    .resize(500, 100)
                    .into(shopImage)
        } else {
            if (fileSize <= 400) {
                degreeImgLink = imgRealPath.toString()
                tv_degree_img_link.text = imgRealPath.toString()
            } else
                (mContext as DashboardActivity).showSnackMessage("Image size can not be greater than 400 KB")
        }
    }

    private fun getAssignedPPListApi(isEdited: Boolean, shop_id: String?) {
        val repository = AssignToPPListRepoProvider.provideAssignPPListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.assignToPPList(Pref.profile_state)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AssignToPPListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.assigned_to_pp_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                                        if (assignPPList != null)
                                            AppDatabase.getDBInstance()?.ppListDao()?.delete()

                                        for (i in list.indices) {
                                            val assignToPP = AssignToPPEntity()
                                            assignToPP.pp_id = list[i].assigned_to_pp_id
                                            assignToPP.pp_name = list[i].assigned_to_pp_authorizer_name
                                            assignToPP.pp_phn_no = list[i].phn_no
                                            AppDatabase.getDBInstance()?.ppListDao()?.insert(assignToPP)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            if (!isEdited)
                                                showAssignedToPPDialog(AppDatabase.getDBInstance()?.ppListDao()?.getAll())
                                            else
                                                getAssignedDDListApi(isEdited, shop_id)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    if (!isEdited)
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    else {
                                        (mContext as DashboardActivity).updateFence()
                                        (mContext as DashboardActivity).showSnackMessage("Shop edited successfully")
                                        (mContext as DashboardActivity).onBackPressed()
                                        (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, shop_id!!)
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                if (!isEdited)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else {
                                    (mContext as DashboardActivity).updateFence()
                                    (mContext as DashboardActivity).showSnackMessage("Shop edited successfully")
                                    (mContext as DashboardActivity).onBackPressed()
                                    (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, shop_id!!)
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            if (!isEdited)
                                (mContext as DashboardActivity).showSnackMessage("ERROR")
                            else {
                                (mContext as DashboardActivity).updateFence()
                                (mContext as DashboardActivity).showSnackMessage("Shop edited successfully")
                                (mContext as DashboardActivity).onBackPressed()
                                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, shop_id!!)
                            }
                        })
        )
    }

    private fun showProfileAlert() {
        CommonDialog.getInstance(getString(R.string.app_name), "Please update your profile", getString(R.string.cancel), getString(R.string.ok), object : CommonDialogClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String) {
                (mContext as DashboardActivity).loadFragment(FragType.MyProfileFragment, false, "")
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }


    private fun showAssignedToPPDialog(mAssignedList: List<AssignToPPEntity>?) {
        AssignedToPPDialog.newInstance(mAssignedList, addShopData.type, object : AssignedToPPDialog.OnItemSelectedListener {
            override fun onItemSelect(pp: AssignToPPEntity?) {
                assigned_to_pp_TV.text = pp?.pp_name + " (" + pp?.pp_phn_no + ")"
                assignedToPPId = pp?.pp_id.toString()
            }
        }).show(fragmentManager!!, "")
    }

    private fun getAssignedDDListApi(edited: Boolean, shop_id: String?) {
        val repository = AssignToDDListRepoProvider.provideAssignDDListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.assignToDDList(Pref.profile_state)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AssignToDDListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.assigned_to_dd_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        val assignDDList = AppDatabase.getDBInstance()?.ddListDao()?.getAll()
                                        if (assignDDList != null)
                                            AppDatabase.getDBInstance()?.ddListDao()?.delete()

                                        for (i in list.indices) {
                                            val assignToDD = AssignToDDEntity()
                                            assignToDD.dd_id = list[i].assigned_to_dd_id
                                            assignToDD.dd_name = list[i].assigned_to_dd_authorizer_name
                                            assignToDD.dd_phn_no = list[i].phn_no
                                            assignToDD.pp_id = list[i].assigned_to_pp_id
                                            assignToDD.type_id = list[i].type_id
                                            assignToDD.dd_latitude = list[i].dd_latitude
                                            assignToDD.dd_longitude = list[i].dd_longitude
                                            AppDatabase.getDBInstance()?.ddListDao()?.insert(assignToDD)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            if (!edited) {

                                                /*if (!TextUtils.isEmpty(assignedToPPId)) {
                                                    val list_ = AppDatabase.getDBInstance()?.ddListDao()?.getValuePPWise(assignedToPPId)
                                                    showAssignedToDDDialog(list_)
                                                }
                                                else {
                                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.select_pp))
                                                }*/

                                                if (dealerId.isNotEmpty()) {
                                                    val list_ = AppDatabase.getDBInstance()?.ddListDao()?.getValueTypeWise(dealerId)
                                                    if (list_ != null && list_.isNotEmpty())
                                                        showAssignedToDDDialog(list_)
                                                    else
                                                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                                                }
                                                else
                                                    showAssignedToDDDialog(AppDatabase.getDBInstance()?.ddListDao()?.getAll())
                                            } else {
                                                getAssignedToShopApi(edited, shop_id)
                                            }
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    if (!edited)
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    else {
                                        getAssignedToShopApi(edited, shop_id)
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                if (!edited)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else {
                                    getAssignedToShopApi(edited, shop_id)
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            if (!edited)
                                (mContext as DashboardActivity).showSnackMessage("ERROR")
                            else {
                                getAssignedToShopApi(edited, shop_id)
                            }
                        })
        )
    }

    private fun showAssignedToDDDialog(mAssignedList: List<AssignToDDEntity>?) {
        AssignedToDDDialog.newInstance(mAssignedList, object : AssignedToDDDialog.OnItemSelectedListener {
            override fun onItemSelect(dd: AssignToDDEntity?) {
                assigned_to_dd_TV.text = dd?.dd_name + " (" + dd?.dd_phn_no + ")"
                assignedToDDId = dd?.dd_id.toString()
            }
        }).show(fragmentManager!!, "")
    }

    private fun getAssignedToShopApi(edited: Boolean, shop_id: String?) {
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.assignToShopList(Pref.profile_state)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AssignedToShopListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.shop_list

                                AppDatabase.getDBInstance()?.assignToShopDao()?.delete()

                                doAsync {
                                    list?.forEach {
                                        val shop = AssignToShopEntity()
                                        AppDatabase.getDBInstance()?.assignToShopDao()?.insert(shop.apply {
                                            assigned_to_shop_id = it.assigned_to_shop_id
                                            name = it.name
                                            phn_no = it.phn_no
                                            type_id = it.type_id
                                        })
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        if (!edited) {
                                            if (retailerId.isNotEmpty()) {
                                                val list_ = AppDatabase.getDBInstance()?.assignToShopDao()?.getValueTypeWise(retailerId) as ArrayList<AssignToShopEntity>
                                                if (list_ != null && list_.isNotEmpty())
                                                    showAssignedToShopListDialog(list_)
                                                else
                                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                                            }
                                            else
                                                showAssignedToShopListDialog(AppDatabase.getDBInstance()?.assignToShopDao()?.getAll() as ArrayList<AssignToShopEntity>)
                                        }
                                        else {
                                            (mContext as DashboardActivity).updateFence()
                                            (mContext as DashboardActivity).showSnackMessage("Shop edited successfully")
                                            (mContext as DashboardActivity).onBackPressed()
                                            (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, shop_id!!)
                                        }
                                    }
                                }
                            }
                            else {
                                progress_wheel.stopSpinning()
                                if (!edited)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else {
                                    (mContext as DashboardActivity).updateFence()
                                    (mContext as DashboardActivity).showSnackMessage("Shop edited successfully")
                                    (mContext as DashboardActivity).onBackPressed()
                                    (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, shop_id!!)
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            if (!edited)
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            else {
                                (mContext as DashboardActivity).updateFence()
                                (mContext as DashboardActivity).showSnackMessage("Shop edited successfully")
                                (mContext as DashboardActivity).onBackPressed()
                                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, shop_id!!)
                            }
                        })
        )
    }

    private fun showAssignedToShopListDialog(list: ArrayList<AssignToShopEntity>) {
        AssignedToShopDialog.newInstance(list, object : AssignedToShopDialog.OnItemSelectedListener {
            override fun onItemSelect(shop: AssignToShopEntity?) {
                tv_assign_to_shop.text = shop?.name + " (" + shop?.phn_no + ")"
                assignedToShopId = shop?.assigned_to_shop_id!!
            }
        }).show(fragmentManager!!, "")
    }

    private fun initShopTypePopUp(view: View) {
        val popup = PopupWindow(mContext)
        val layout = layoutInflater.inflate(R.layout.shop_type_dropdown, null)

        popup.contentView = layout
        popup.isOutsideTouchable = true
        popup.isFocusable = true

        var width = 900
        var height = 400
        try {
            var size = Point()
            (mContext as DashboardActivity).windowManager.defaultDisplay.getSize(size)
            width = size.x
            height = size.y

        } catch (e: Exception) {
            e.printStackTrace()
        }
        popup.width = width - 10
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT

        var tv_shop_type: AppCustomTextView = layout.findViewById(R.id.shop_type_TV)
        var distributor_tv: AppCustomTextView = layout.findViewById(R.id.distributor_tv)
        var pp_tv: AppCustomTextView = layout.findViewById(R.id.pp_tv)
        var new_party_tv: AppCustomTextView = layout.findViewById(R.id.new_party_tv)
        val diamond_tv: AppCustomTextView = layout.findViewById(R.id.diamond_tv)
        val rv_type_list: RecyclerView = layout.findViewById(R.id.rv_type_list)

        val list = AppDatabase.getDBInstance()?.shopTypeDao()?.getAll()

        rv_type_list.layoutManager = LinearLayoutManager(mContext)
        rv_type_list.adapter = ShopTypeAdapter(mContext, list) { shopType: ShopTypeEntity ->
            shop_type_TV.text = shopType.shoptype_name

            if (shopType.shoptype_id == "1" || shopType.shoptype_id == "5")
                shop_type_TV.hint = getString(R.string.shop_name)
            else
                shop_type_TV.hint = getString(R.string.company_name)

            addShopData.type = shopType.shoptype_id
            type = shopType.shoptype_id!!


            when (addShopData.type) {
                "1" -> {
                    rl_assigned_to_pp.visibility = View.VISIBLE
                    rl_assigned_to_dd.visibility = View.VISIBLE
                    rl_amount.visibility = View.GONE
                }
                "2" -> {
                    rl_assigned_to_pp.visibility = View.GONE
                    rl_assigned_to_dd.visibility = View.GONE
                    rl_amount.visibility = View.GONE
                }
                "3" -> {
                    rl_assigned_to_pp.visibility = View.GONE
                    rl_assigned_to_dd.visibility = View.GONE
                    rl_amount.visibility = View.GONE
                }
                "4" -> {
                    rl_assigned_to_pp.visibility = View.VISIBLE
                    rl_assigned_to_dd.visibility = View.GONE
                    rl_amount.visibility = View.GONE
                }
                "5" -> {
                    rl_assigned_to_pp.visibility = View.VISIBLE
                    rl_assigned_to_dd.visibility = View.VISIBLE
                    rl_amount.visibility = View.VISIBLE
                }
            }
        }

        tv_shop_type.setOnClickListener(View.OnClickListener {
            shop_type_TV.text = getString(R.string.shop_type)
            shop_type_TV.hint = getString(R.string.shop_name)
            addShopData.type = "1"
            type = "1"
            rl_assigned_to_pp.visibility = View.VISIBLE
            rl_assigned_to_dd.visibility = View.VISIBLE
            rl_amount.visibility = View.GONE
            popup.dismiss()
        })
        distributor_tv.setOnClickListener(View.OnClickListener {
            shop_type_TV.text = getString(R.string.distributor_type)
            shop_type_TV.hint = getString(R.string.company_name)
            addShopData.type = "4"
            type = "4"
            rl_assigned_to_pp.visibility = View.VISIBLE
            rl_assigned_to_dd.visibility = View.GONE
            rl_amount.visibility = View.GONE
            popup.dismiss()
        })
        pp_tv.setOnClickListener(View.OnClickListener {
            shop_type_TV.text = getString(R.string.pp_type)
            shop_type_TV.hint = getString(R.string.company_name)
            addShopData.type = "2"
            type = "2"
            rl_assigned_to_pp.visibility = View.GONE
            rl_assigned_to_dd.visibility = View.GONE
            rl_amount.visibility = View.GONE
            popup.dismiss()
        })
        new_party_tv.setOnClickListener(View.OnClickListener {
            shop_type_TV.text = getString(R.string.new_party_type)
            shop_type_TV.hint = getString(R.string.company_name)
            addShopData.type = "3"
            type = "3"
            rl_assigned_to_pp.visibility = View.GONE
            rl_assigned_to_dd.visibility = View.GONE
            rl_amount.visibility = View.GONE
            popup.dismiss()
        })
        diamond_tv.setOnClickListener(View.OnClickListener {
            shop_type_TV.text = getString(R.string.diamond_type)
            shop_type_TV.hint = getString(R.string.company_name)
            addShopData.type = "5"
            rl_assigned_to_pp.visibility = View.VISIBLE
            rl_assigned_to_dd.visibility = View.VISIBLE
            rl_amount.visibility = View.VISIBLE
            popup.dismiss()
        })

        /*AutoDDSelect Feature*/
        if(Pref.AutoDDSelect){
            rl_assigned_to_dd.visibility = View.VISIBLE
        }
        else{
            rl_assigned_to_dd.visibility = View.GONE
        }


        popup.setBackgroundDrawable(ColorDrawable(Color.WHITE))
//        popup.showAsDropDown(view)
        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        val OFFSET_X = resources.getDimensionPixelOffset(R.dimen._50sdp)
        val OFFSET_Y = resources.getDimensionPixelOffset(R.dimen._50sdp)
        popup.showAtLocation(view, Gravity.CENTER_VERTICAL, OFFSET_X, OFFSET_Y)
//        popup.update()
//        popup.showAtLocation(layout , Gravity.CENTER, 0, 0);
    }

    private var popupWindow: PopupWindow? = null

    private fun callThemePopUp(anchorView: View, arr_themes: ArrayList<String>) {

        popupWindow = PopupWindow(ThemedropDownMenu(R.layout.inflate_items_popup_window, arr_themes, anchorView), anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow!!.setBackgroundDrawable(BitmapDrawable())
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.showAsDropDown(anchorView)
        popupWindow!!.update()

    }

    private fun ThemedropDownMenu(layout: Int, arr_roomType: ArrayList<String>, textview: View): View {
        var view: View? = null
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(layout, null, false)
        themeListPopupWindowAdapter = InflateThemeListPopupWindowAdapter(mContext, arr_roomType, object : onPopupMenuClickListener {
            override fun onPopupMenuClick(name: String, position: Int) {
                assigned_to_TV.setText(name)
                popupWindow?.dismiss()
            }
        })

        val listView = view.findViewById<ListView>(R.id.lv_roomType)
        listView.adapter = themeListPopupWindowAdapter


        return view
    }


    private fun disabledEntry() {
        isEnabled = false
        shopImage.setOnClickListener(null)
        iv_category_dropdown_icon.visibility = View.GONE
        shopName.isEnabled = false
        agency_name_TV.isEnabled = false
        shopAddress.isEnabled = false
        shopPin.isEnabled = false
        shopContactNumber.isEnabled = false
        shopOwnerEmail.isEnabled = false
        amount_ET.isEnabled = false

        land_contact_no_TV.isEnabled = false
        alternate_no_TV.isEnabled = false
        whatsappp_no_TV.isEnabled = false
        project_name_TV.isEnabled = false



        /*val userId = shopId.substring(0, shopId.indexOf("_"))
        if (userId != Pref.user_id)
            floating_fab.visibility = View.GONE
        else {*/
            if (!Pref.isShopAddEditAvailable && !Pref.isOrderShow && !Pref.isCollectioninMenuShow && (!Pref.willStockShow || (!Pref.isStockAvailableForAll && addShopData.type != "4")))
                floating_fab.visibility = View.GONE
            else
                floating_fab.visibility = View.VISIBLE
        //}

        ownwr_name_TV.isEnabled = false
        ownwr_dob_TV.isEnabled = false
        ownwr_ani_TV.isEnabled = false
        shop_type_TV.isEnabled = false
        assigned_to_TV.isEnabled = false

        address_RL.isEnabled = false
        callShop.isEnabled = false
        sendEmail.isEnabled = false

        assigned_to_dd_TV.isEnabled = false
        assigned_to_pp_TV.isEnabled = false

        tv_area.isEnabled = false
        tv_model.isEnabled = false
        tv_primary_application.isEnabled = false
        tv_secondary_application.isEnabled = false
        tv_lead_type.isEnabled = false
        tv_stage.isEnabled = false
        tv_funnel_stage.isEnabled = false
        et_booking_amount.isEnabled = false
        rl_type.isEnabled = false

        save_TV.visibility = View.GONE

        tv_family_dob.isEnabled = false
        et_dir_name_value.isEnabled = false
        et_person_name_value.isEnabled = false
        et_person_no_value.isEnabled = false
        tv_add_dob.isEnabled = false
        tv_add_doa.isEnabled = false

        et_specialization.isEnabled = false
        et_patient_count.isEnabled = false
        et_category.isEnabled = false
        tv_doc_family_dob.isEnabled = false
        et_doc_add.isEnabled = false
        et_doc_pincode.isEnabled = false
        ll_yes.isEnabled = false
        ll_no.isEnabled = false
        et_remarks.isEnabled = false
        et_chemist_name.isEnabled = false
        et_chemist_add.isEnabled = false
        et_chemist_pincode.isEnabled = false
        et_assistant_name.isEnabled = false
        et_assistant_no.isEnabled = false
        tv_assistant_dob.isEnabled = false
        tv_assistant_doa.isEnabled = true
        tv_assistant_family_dob.isEnabled = false

        tv_entity.isEnabled = false
        tv_party.isEnabled = false
        retailer_TV.isEnabled = false
        dealer_TV.isEnabled = false
        beat_TV.isEnabled = false
        tv_assign_to_shop.isEnabled = false

        tv_shoptype_asterisk_mark.visibility = View.GONE
        tv_name_asterisk_mark.visibility = View.GONE
        tv_agency_asterisk_mark.visibility = View.GONE
        tv_address_asterisk_mark.visibility = View.GONE
        tv_pincode_asterisk_mark.visibility = View.GONE
        tv_owner_name_asterisk_mark.visibility = View.GONE
        tv_no_asterisk_mark.visibility = View.GONE
        tv_dd_asterisk_mark.visibility = View.GONE
        tv_pp_asterisk_mark.visibility = View.GONE
        tv_area_asterisk_mark.visibility = View.GONE
        tv_amount_asterisk_mark.visibility = View.GONE
        iv_area_dropdown.visibility = View.GONE
        iv_funnel_stage_dropdown.visibility = View.GONE
        iv_stage_dropdown.visibility = View.GONE
        iv_lead_type_dropdown.visibility = View.GONE
        iv_secondary_application_dropdown.visibility = View.GONE
        iv_primary_application_dropdown.visibility = View.GONE
        iv_model_dropdown.visibility = View.GONE
        tv_model_asterisk_mark.visibility = View.GONE
        tv_stage_asterisk_mark.visibility = View.GONE
        iv_type_dropdown_icon.visibility = View.GONE

        tv_dir_name_asterisk_mark.visibility = View.GONE
        tv_family_mem_dob_asterisk_mark.visibility = View.GONE
        tv_key_person_name_asterisk_mark.visibility = View.GONE
        tv_key_person_no_asterisk_mark.visibility = View.GONE
        tv_attachment_asterisk_mark.visibility = View.GONE
        tv_specalization_asterisk_mark.visibility = View.GONE
        tv_patient_asterisk_mark.visibility = View.GONE
        tv_category_asterisk_mark.visibility = View.GONE
        tv_doc_family_mem_dob_asterisk_mark.visibility = View.GONE
        tv_doc_address_asterisk_mark.visibility = View.GONE
        tv_doc_pincode_asterisk_mark.visibility = View.GONE
        tv_chamber_asterisk_mark.visibility = View.GONE
        tv_chemist_name_asterisk_mark.visibility = View.GONE
        tv_chemist_address_asterisk_mark.visibility = View.GONE
        tv_chemist_pincode_asterisk_mark.visibility = View.GONE

        tv_entity_asterisk_mark.visibility = View.GONE
        tv_party_asterisk_mark.visibility = View.GONE
        iv_entity_dropdown.visibility = View.GONE
        iv_party_dropdown.visibility = View.GONE
        iv_retailer_dropdown.visibility = View.GONE
        iv_dealer_dropdown.visibility = View.GONE
        iv_beat_dropdown.visibility = View.GONE
        iv_assign_to_shop_dropdown.visibility = View.GONE
        tv_assign_to_shop_asterisk_mark.visibility = View.GONE
        tv_retailer_asterisk_mark.visibility = View.GONE
        tv_dealer_asterisk_mark.visibility = View.GONE

        if (addShopData.is_otp_verified.equals("true", ignoreCase = true))
            ll_verified.visibility = View.VISIBLE
        else {
            val list = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, AppUtils.getCurrentDateForShopActi())

            if (list != null && list.isNotEmpty() && list[0].isVisited) {
                ll_verified.visibility = View.VISIBLE
            } else {
                iv_otp_check.visibility = View.GONE
                tv_verified.visibility = View.GONE
            }
        }
    }

    private fun enabledEntry(isShopRevisited: Boolean) {
        isEnabled = true

        shopImage.setOnClickListener(this)

        iv_pp_drop_down_icon.visibility = View.VISIBLE
        iv_dd_dropdown_icon.visibility = View.VISIBLE

        assigned_to_dd_TV.isEnabled = true
        assigned_to_pp_TV.isEnabled = true

        tv_area.isEnabled = true
        tv_model.isEnabled = true
        tv_primary_application.isEnabled = true
        tv_secondary_application.isEnabled = true
        tv_lead_type.isEnabled = true
        tv_stage.isEnabled = true
        tv_funnel_stage.isEnabled = true
        et_booking_amount.isEnabled = true

        iv_category_dropdown_icon.visibility = View.GONE
        shop_type_TV.isEnabled = false
        amount_ET.isEnabled = true

        tv_shoptype_asterisk_mark.visibility = View.VISIBLE
        tv_name_asterisk_mark.visibility = View.VISIBLE
        tv_agency_asterisk_mark.visibility = View.VISIBLE
        tv_address_asterisk_mark.visibility = View.VISIBLE
        tv_pincode_asterisk_mark.visibility = View.VISIBLE
        tv_owner_name_asterisk_mark.visibility = View.VISIBLE
        tv_no_asterisk_mark.visibility = View.VISIBLE
        tv_dd_asterisk_mark.visibility = View.VISIBLE
        tv_pp_asterisk_mark.visibility = View.VISIBLE
        tv_amount_asterisk_mark.visibility = View.VISIBLE
        iv_area_dropdown.visibility = View.VISIBLE
        iv_funnel_stage_dropdown.visibility = View.VISIBLE
        iv_stage_dropdown.visibility = View.VISIBLE
        iv_lead_type_dropdown.visibility = View.VISIBLE
        iv_secondary_application_dropdown.visibility = View.VISIBLE
        iv_primary_application_dropdown.visibility = View.VISIBLE
        iv_model_dropdown.visibility = View.VISIBLE
        tv_model_asterisk_mark.visibility = View.VISIBLE
        tv_stage_asterisk_mark.visibility = View.VISIBLE
        iv_type_dropdown_icon.visibility = View.VISIBLE

        if (et_booking_amount.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_booking_amount.setText("")

        if (Pref.isAreaMandatoryInPartyCreation)
            tv_area_asterisk_mark.visibility = View.VISIBLE
        else
            tv_area_asterisk_mark.visibility = View.GONE

        /*if (isShopRevisited) {
            iv_category_dropdown_icon.visibility = View.VISIBLE

            iv_pp_drop_down_icon.visibility = View.VISIBLE
            iv_dd_dropdown_icon.visibility = View.VISIBLE

            assigned_to_dd_TV.isEnabled = true
            assigned_to_pp_TV.isEnabled = true
            shop_type_TV.isEnabled = true
        } else {
            iv_category_dropdown_icon.visibility = View.GONE

            iv_pp_drop_down_icon.visibility = View.GONE
            iv_dd_dropdown_icon.visibility = View.GONE

            assigned_to_dd_TV.isEnabled = false
            assigned_to_pp_TV.isEnabled = false
            shop_type_TV.isEnabled = false
        }*/

        floating_fab.visibility = View.GONE
        shopName.isEnabled = false
        shopAddress.isEnabled = true
        shopPin.isEnabled = true

        if (addShopData.is_otp_verified.equals("true", ignoreCase = true)) {
            shopContactNumber.isEnabled = false
            tv_verified.visibility = View.VISIBLE
            iv_otp_check.visibility = View.VISIBLE
        } else {
            shopContactNumber.isEnabled = true
            tv_verified.visibility = View.GONE
            iv_otp_check.visibility = View.GONE
        }

        shopOwnerEmail.isEnabled = true
        if (shopOwnerEmail.text.toString().trim().equals("N.A.", ignoreCase = true))
            shopOwnerEmail.setText("")
        ownwr_name_TV.isEnabled = true
        ownwr_dob_TV.isEnabled = true
        ownwr_ani_TV.isEnabled = true
        assigned_to_TV.isEnabled = true

        address_RL.isEnabled = true
        callShop.isEnabled = true
        sendEmail.isEnabled = true

        save_TV.visibility = View.VISIBLE
        rl_type.isEnabled = true

        et_dir_name_value.isEnabled = true
        if (et_dir_name_value.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_dir_name_value.setText("")

        et_person_name_value.isEnabled = true
        if (et_person_name_value.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_person_name_value.setText("")

        et_person_no_value.isEnabled = true
        if (et_person_no_value.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_person_no_value.setText("")

        tv_family_dob.isEnabled = true
        tv_add_dob.isEnabled = true
        tv_add_doa.isEnabled = true

        et_specialization.isEnabled = true
        et_patient_count.isEnabled = true
        et_category.isEnabled = true
        tv_doc_family_dob.isEnabled = true
        et_doc_add.isEnabled = true
        et_doc_pincode.isEnabled = true
        ll_yes.isEnabled = true
        ll_no.isEnabled = true
        et_remarks.isEnabled = true
        et_chemist_name.isEnabled = true
        et_chemist_add.isEnabled = true
        et_chemist_pincode.isEnabled = true
        et_assistant_name.isEnabled = true
        et_assistant_no.isEnabled = true
        tv_assistant_dob.isEnabled = true
        tv_assistant_doa.isEnabled = true
        tv_assistant_family_dob.isEnabled = true

        tv_entity.isEnabled = true
        tv_party.isEnabled = true
        retailer_TV.isEnabled = true
        dealer_TV.isEnabled = true
        beat_TV.isEnabled = true
        tv_assign_to_shop.isEnabled = true


        land_contact_no_TV.isEnabled = true
        alternate_no_TV.isEnabled = true
        whatsappp_no_TV.isEnabled = true
        project_name_TV.isEnabled = true




        tv_dir_name_asterisk_mark.visibility = View.VISIBLE
        tv_family_mem_dob_asterisk_mark.visibility = View.VISIBLE
        tv_key_person_name_asterisk_mark.visibility = View.VISIBLE
        tv_key_person_no_asterisk_mark.visibility = View.VISIBLE
        tv_attachment_asterisk_mark.visibility = View.VISIBLE
        tv_specalization_asterisk_mark.visibility = View.VISIBLE
        tv_patient_asterisk_mark.visibility = View.VISIBLE
        tv_category_asterisk_mark.visibility = View.VISIBLE
        tv_doc_family_mem_dob_asterisk_mark.visibility = View.VISIBLE
        tv_doc_address_asterisk_mark.visibility = View.VISIBLE
        tv_doc_pincode_asterisk_mark.visibility = View.VISIBLE
        tv_chamber_asterisk_mark.visibility = View.VISIBLE
        tv_chemist_name_asterisk_mark.visibility = View.VISIBLE
        tv_chemist_address_asterisk_mark.visibility = View.VISIBLE
        tv_chemist_pincode_asterisk_mark.visibility = View.VISIBLE

        tv_entity_asterisk_mark.visibility = View.VISIBLE
        tv_party_asterisk_mark.visibility = View.VISIBLE
        iv_entity_dropdown.visibility = View.VISIBLE
        iv_party_dropdown.visibility = View.VISIBLE
        iv_retailer_dropdown.visibility = View.VISIBLE
        iv_dealer_dropdown.visibility = View.VISIBLE
        iv_beat_dropdown.visibility = View.VISIBLE
        iv_assign_to_shop_dropdown.visibility = View.VISIBLE
        tv_assign_to_shop_asterisk_mark.visibility = View.VISIBLE
        tv_retailer_asterisk_mark.visibility = View.VISIBLE
        tv_dealer_asterisk_mark.visibility = View.VISIBLE

        if (retailerId.isEmpty())
            retailer_TV.text = ""

        if (dealerId.isEmpty())
            dealer_TV.text = ""

        if (beatId.isEmpty())
            beat_TV.text = ""

        if (entityId.isEmpty())
            tv_entity.text = ""

        if (partyStatusId.isEmpty())
            tv_party.text = ""

        if (et_specialization.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_specialization.setText("")

        if (et_patient_count.text.toString().trim().toInt() == 0)
            et_patient_count.setText("")

        if (et_category.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_category.setText("")

        if (et_doc_add.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_doc_add.setText("")

        if (et_doc_pincode.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_doc_pincode.setText("")

        if (et_remarks.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_remarks.setText("")

        if (et_chemist_name.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_chemist_name.setText("")

        if (et_chemist_add.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_chemist_add.setText("")

        if (et_chemist_pincode.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_chemist_pincode.setText("")

        if (et_assistant_name.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_assistant_name.setText("")

        if (et_assistant_no.text.toString().trim().equals("N.A.", ignoreCase = true))
            et_assistant_no.setText("")

        if (tv_doc_family_dob.text.toString().trim().equals("N.A.", ignoreCase = true))
            tv_doc_family_dob.text = ""

        if (tv_family_dob.text.toString().trim().equals("N.A.", ignoreCase = true))
            tv_family_dob.text = ""

        if (!Pref.isCustomerFeatureEnable) {
            when (type) {
                "1" -> {
                    rl_assigned_to_pp.visibility = View.VISIBLE
                    rl_assigned_to_dd.visibility = View.VISIBLE
                    rl_amount.visibility = View.GONE
                    rl_type.visibility = View.GONE
                }
                "2" -> {
                    rl_assigned_to_pp.visibility = View.GONE
                    rl_assigned_to_dd.visibility = View.GONE
                    rl_amount.visibility = View.GONE
                    rl_type.visibility = View.GONE
                }
                "3" -> {
                    rl_assigned_to_pp.visibility = View.GONE
                    rl_assigned_to_dd.visibility = View.GONE
                    rl_amount.visibility = View.GONE
                    rl_type.visibility = View.GONE
                }
                "5" -> {
                    rl_assigned_to_pp.visibility = View.VISIBLE
                    rl_assigned_to_dd.visibility = View.VISIBLE
                    rl_amount.visibility = View.VISIBLE
                    rl_type.visibility = View.GONE
                }
                "6" -> {
                    rl_assigned_to_pp.visibility = View.GONE
                    rl_assigned_to_dd.visibility = View.GONE
                    rl_amount.visibility = View.GONE
                    rl_type.visibility = View.GONE
                }
                "7" -> {
                    rl_assigned_to_pp.visibility = View.VISIBLE
                    rl_assigned_to_dd.visibility = View.GONE
                    rl_amount.visibility = View.GONE
                    rl_type.visibility = View.GONE
                }
                "8" -> {
                    rl_assigned_to_pp.visibility = View.GONE
                    rl_assigned_to_dd.visibility = View.GONE
                    rl_amount.visibility = View.GONE
                    rl_type.visibility = View.GONE
                }
                "10" -> {
                    rl_assigned_to_pp.visibility = View.GONE

                    if (Pref.isDDShowForMeeting)
                        rl_assigned_to_dd.visibility = View.VISIBLE
                    else
                        rl_assigned_to_dd.visibility = View.GONE

                    if (Pref.isDDMandatoryForMeeting)
                        tv_dd_asterisk_mark.visibility = View.VISIBLE
                    else
                        tv_dd_asterisk_mark.visibility = View.GONE

                    rl_amount.visibility = View.GONE
                    rl_type.visibility = View.VISIBLE
                }
                "17","18","19","20","21","22","23","24","25" ->{
                    rl_assigned_to_pp.visibility = View.GONE
                    rl_assigned_to_dd.visibility = View.GONE
                    rl_amount.visibility = View.GONE
                    rl_type.visibility = View.GONE
                }
                else -> {
                    rl_assigned_to_pp.visibility = View.VISIBLE
                    rl_assigned_to_dd.visibility = View.GONE
                    rl_amount.visibility = View.GONE
                    rl_type.visibility = View.GONE
                }
            }
            /*AutoDDSelect Feature*/
            if(Pref.AutoDDSelect){
                rl_assigned_to_dd.visibility = View.VISIBLE
            }
            else{
                rl_assigned_to_dd.visibility = View.GONE
            }
        } else {
            rl_assigned_to_pp.visibility = View.GONE
            rl_assigned_to_dd.visibility = View.GONE
            rl_amount.visibility = View.GONE
            rl_type.visibility = View.GONE
        }
    }


    fun updateItem() {
        try {
            if (!TextUtils.isEmpty(shopId)) {

                val orderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shopId) as ArrayList<OrderDetailsListEntity>

                if (orderList != null && orderList.isNotEmpty()) {

                    var amount = 0.0
                    for (i in orderList.indices) {
                        if (!TextUtils.isEmpty(orderList[i].amount))
                            amount += orderList[i].amount?.toDouble()!!
                    }
                    val finalAmount = String.format("%.2f", amount.toFloat())
                    order_amt_p_TV.text = " \u20B9 $finalAmount"
                }


                val quoList = AppDatabase.getDBInstance()!!.quotDao().getSingleShopQuotation(shopId)

                if (quoList != null && quoList.isNotEmpty()) {
                    var amount = 0.0
                    for (i in quoList.indices) {
                        if (!TextUtils.isEmpty(quoList[i].amount))
                            amount += quoList[i].amount?.toDouble()!!
                    }
                    val finalAmount = String.format("%.2f", amount.toFloat())
                    quot_amt_p_TV.text = " \u20B9 $finalAmount"
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun refreshList() {
        getShopTypeListApi(shop_type_TV, true)
    }

    fun openDialogPopup(header:String,text:String){
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_ok_imei)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_yes_header) as AppCustomTextView
        val dialogBody = simpleDialog.findViewById(R.id.dialog_yes_body) as AppCustomTextView
        dialogHeader.text = header
        dialogBody.text = text
        val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes) as AppCustomTextView
        dialogYes.setOnClickListener({ view ->
            simpleDialog.cancel()
        })
        simpleDialog.show()
    }
}