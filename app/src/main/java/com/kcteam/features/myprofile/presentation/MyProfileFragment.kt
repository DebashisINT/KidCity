package com.kcteam.features.myprofile.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.*
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.areaList.AreaListRepoProvider
import com.kcteam.features.addshop.api.assignToPPList.AssignToPPListRepoProvider
import com.kcteam.features.addshop.api.assignedToDDList.AssignToDDListRepoProvider
import com.kcteam.features.addshop.api.typeList.TypeListRepoProvider
import com.kcteam.features.addshop.model.AreaListResponseModel
import com.kcteam.features.addshop.model.AssignedToShopListResponseModel
import com.kcteam.features.addshop.model.assigntoddlist.AssignToDDListResponseModel
import com.kcteam.features.addshop.model.assigntopplist.AssignToPPListResponseModel
import com.kcteam.features.addshop.presentation.AccuracyIssueDialog
import com.kcteam.features.commonlistdialog.CommonListDialog
import com.kcteam.features.commonlistdialog.CommonListDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.myprofile.api.MyProfileRepoProvider
import com.kcteam.features.myprofile.api.citylist.CityListRepoProvider
import com.kcteam.features.myprofile.api.statelist.StateListRepoProvider
import com.kcteam.features.myprofile.model.ProfileUpdateRequestData
import com.kcteam.features.myprofile.model.citylist.CityListApiResponse
import com.kcteam.features.myprofile.model.statelist.StateListApiResponse
import com.kcteam.features.nearbyshops.model.StateCityResponseModel
import com.kcteam.widgets.AppCustomTextView
import com.squareup.picasso.Picasso
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Pratishruti on 12-02-2018.
 */
class MyProfileFragment : BaseFragment() {
    private lateinit var profilePicture: ImageView

    private lateinit var profile_name_TV: AppCustomTextView
    private lateinit var update_TV: AppCustomTextView

    private lateinit var user_address_RL: RelativeLayout
    private lateinit var city_RL: RelativeLayout
    private lateinit var state_RL: RelativeLayout
    private lateinit var country_RL: RelativeLayout
    private lateinit var pincode_RL: RelativeLayout
    private lateinit var address_EDT: AppCompatEditText
    private lateinit var city_EDT: AppCompatEditText
    private lateinit var state_EDT: AppCompatEditText
    private lateinit var country_EDT: AppCompatEditText
    private lateinit var pincode_EDT: AppCompatEditText
    private lateinit var edit_user_name_IV: ImageView
    private lateinit var iv_apply: AppCompatImageView


    private lateinit var mContext: Context
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private var dialog: AccuracyIssueDialog? = null
    private var profile_image_file = ""
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var stateId: String = ""
    private var cityId: String = ""
    private lateinit var state_list: List<String>
    private var isStateClicked = false
    private var permissionUtils: PermissionUtils? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_myprofile, container, false)
        initView(view)

        if (AppDatabase.getDBInstance()?.stateDao()?.getAll()!!.isEmpty()) {
            if (AppUtils.isOnline(mContext))
                getStateListApi()
            else {
                (mContext as DashboardActivity).showSnackMessage("Please check your internet connection")
                state_list = ArrayList()
            }
        } else {
            state_list = AppDatabase.getDBInstance()?.stateDao()?.getAllState()!!
            if (Pref.isProfileUpdated) {
                setDataFromPrefs()
            } else {
                //country_EDT.setText(LocationWizard.getCountry(mContext, Pref.latitude!!.toDouble(), Pref.longitude!!.toDouble()))
            }

        }
        return view
    }

    private fun getStateListApi() {
        val repository = StateListRepoProvider.provideStateListRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getShopList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            var response = result as StateListApiResponse
                            doAsync {
                                var list = response.state_list
                                for (i in 0 until (list?.size ?: 0)) {
                                    var state = StateListEntity()
                                    state.state_id = list!![i].state_id.toInt()
                                    state.state_name = list!![i].state_name
                                    AppDatabase.getDBInstance()?.stateDao()?.insertAll(state)
                                }
                                state_list = AppDatabase.getDBInstance()?.stateDao()?.getAllState()!!
//                            if (AppDatabase.getDBInstance()?.cityDao()?.getAll()!!.isEmpty())
                                uiThread {
                                    progress_wheel.stopSpinning()
                                    getCityListApi()
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                            state_list = ArrayList()
                        })
        )
    }

    private fun getCityListApi() {
        val repository = CityListRepoProvider.provideCityListRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getCityList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            var response = result as CityListApiResponse
                            doAsync {
                                var list = response.city_list
                                for (i in 0 until (list?.size ?: 0)) {
                                    var city = CityListEntity()
                                    city.state_id = list!![i].state_id
                                    city.city_id = list!![i].city_id.toInt()
                                    city.city_name = list!![i].city_name
                                    AppDatabase.getDBInstance()?.cityDao()?.insertAll(city)
                                }

                                uiThread {
                                    if (Pref.isProfileUpdated) {
                                        setDataFromPrefs()
                                    } else {
                                        country_EDT.setText(LocationWizard.getCountry(mContext, Pref.latitude!!.toDouble(), Pref.longitude!!.toDouble()))
                                    }
                                    progress_wheel.stopSpinning()
                                }
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )
    }

    private fun initView(view: View) {
        edit_user_name_IV = view.findViewById(R.id.edit_user_name_IV)
        profile_name_TV = view.findViewById(R.id.profile_name_TV)
        iv_apply = view.findViewById(R.id.iv_apply)
        profilePicture = view.findViewById(R.id.profile_picture_IV)
        profilePicture.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                initPermissionCheck()
            else
                showPictureDialog()
        }
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        address_EDT = view.findViewById(R.id.address_EDT)
        city_EDT = view.findViewById(R.id.city_EDT)
        state_EDT = view.findViewById(R.id.state_EDT)
//        state_EDT.isEnabled=false
        country_EDT = view.findViewById(R.id.country_EDT)
        pincode_EDT = view.findViewById(R.id.pincode_EDT)

        state_RL = view.findViewById(R.id.state_RL)
        city_RL = view.findViewById(R.id.city_RL)

        profile_name_TV.setText(Pref.user_name)
//        profile_name_TV.isEnabled=false

        update_TV = view.findViewById(R.id.update_TV)
        update_TV.setOnClickListener(View.OnClickListener {
            validateFieldAndApiCall()
        })

        state_RL.setOnClickListener(View.OnClickListener {
            //            var list: MutableList<String> = ArrayList()
//            list=state_list.toMutableList()

            //openCommonListDialog(state_list, true)
        })

        state_EDT.setOnClickListener(View.OnClickListener {
            //openCommonListDialog(state_list, true)
        })
        city_RL.setOnClickListener(View.OnClickListener {
            /*if (stateId.trim().isBlank()) {
                (mContext as DashboardActivity).showSnackMessage("Please select a state first")
                return@OnClickListener
            }

            var list = AppDatabase.getDBInstance()?.cityDao()?.getCityListFromState(stateId)!!
            openCommonListDialog(list, false)*/
        })
        city_EDT.setOnClickListener(View.OnClickListener {
            /*if (stateId.trim().isBlank()) {
                (mContext as DashboardActivity).showSnackMessage("Please select a state first")
                return@OnClickListener
            }

            var list = AppDatabase.getDBInstance()?.cityDao()?.getCityListFromState(stateId)!!
            openCommonListDialog(list, false)*/
        })

//        edit_user_name_IV.setOnClickListener(View.OnClickListener {
//            profile_name_TV.isEnabled=true
//
//        })

        iv_apply.setOnClickListener {
            callStateCityApi()
        }
    }

    private fun callStateCityApi() {
        AppUtils.hideSoftKeyboard(mContext as DashboardActivity)

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = MyProfileRepoProvider.provideStateCityRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getStateCityList(pincode_EDT.text.toString().trim())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as StateCityResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                city_EDT.setText(response.city)
                                state_EDT.setText(response.state)
                                country_EDT.setText(response.country)
                                cityId = response.city_id
                                stateId = response.state_id
                            } else {
                                city_EDT.setText("")
                                state_EDT.setText("")
                                country_EDT.setText("")
                                cityId = ""
                                stateId = ""
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            city_EDT.setText("")
                            state_EDT.setText("")
                            country_EDT.setText("")
                            cityId = ""
                            stateId = ""
                        })
        )
    }

    private fun openCommonListDialog(list: List<String>, isState: Boolean) {
        Collections.sort(list, object : Comparator<String> {
            override fun compare(p0: String?, p1: String?): Int {
                return p0!!.toUpperCase().compareTo(p1!!.toUpperCase())
            }
        })
        CommonListDialog.getInstance(list, object : CommonListDialogClickListener {
            override fun onItemClick(position: Int) {
                if (isState) {
                    state_EDT.setText(list[position])
                    stateId = AppDatabase.getDBInstance()?.stateDao()?.getIdFromName(state_list[position])!!.toString()
                    city_EDT.setText("")
                    address_EDT.setText("")
                    pincode_EDT.setText("")
                    cityId = ""
                } else {
                    city_EDT.setText(list[position])
                    cityId = AppDatabase.getDBInstance()?.cityDao()?.getIdFromName(list[position], stateId)!!.toString()
                    address_EDT.setText("")
                    pincode_EDT.setText("")
                }
            }
        }).show((mContext as DashboardActivity).supportFragmentManager, "CommonListDialog")
    }

    private fun validateFieldAndApiCall() {
        val profile_name = profile_name_TV.text.trim()
        val address = address_EDT.text?.trim()
        val city = city_EDT.text?.trim()
        val state = state_EDT.text?.trim()
        val country = country_EDT.text?.trim()
        val pincode = pincode_EDT.text?.trim()
        if (profile_image_file.isBlank()) {
            (context as DashboardActivity).showSnackMessage(getString(R.string.alert_no_profile_img))
            return
        }
        if (address!!.isBlank()) {
            (context as DashboardActivity).showSnackMessage(getString(R.string.alert_no_address))
            return
        }
        if (address!!.isBlank()) {
            (context as DashboardActivity).showSnackMessage(getString(R.string.alert_no_address))
            return
        }
        if (state!!.isBlank()) {
            (context as DashboardActivity).showSnackMessage(getString(R.string.alert_no_state))
            return
        }
        if (city!!.isBlank()) {
            (context as DashboardActivity).showSnackMessage(getString(R.string.alert_no_city))
            return
        }
        if (pincode!!.isBlank()) {
            (context as DashboardActivity).showSnackMessage(getString(R.string.alert_no_pincode))
            return
        }
        if (Pref.isProfileUpdated) {
            stateId = AppDatabase.getDBInstance()?.stateDao()?.getIdFromName(state.toString())!!.toString()
            cityId = AppDatabase.getDBInstance()?.cityDao()?.getIdFromName(city.toString(), stateId)!!.toString()
        }
        setDataInPrefs(profile_image_file, profile_name, address, stateId, cityId, country!!, pincode)

        callUpDateProfileApi(profile_name, address, cityId, stateId, country!!, pincode)
    }

    private fun setDataInPrefs(profile_image_file: String, profile_name: CharSequence, address: CharSequence, state: CharSequence, city: CharSequence, country: CharSequence, pincode: CharSequence) {
        Pref.user_name = profile_name.toString()
        Pref.profile_img = profile_image_file
        Pref.profile_state = state.toString()
        Pref.profile_city = city.toString()
        Pref.profile_pincode = pincode.toString()
        Pref.profile_country = country.toString()
        Pref.profile_address = address.toString()
    }

    private fun setDataFromPrefs() {
        stateId = Pref.profile_state
        cityId = Pref.profile_city
        profile_image_file = Pref.profile_img
        // Picasso.with(context).load(Pref.profile_img).into(profilePicture)
        /*Picasso.get()
                .load(Pref.profile_img)
                .resize(100, 100)
                .into(profilePicture)*/


        Glide.with(mContext)
                .load(Pref.profile_img)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_menu_profile_image).error(R.drawable.ic_menu_profile_image))
                .into(profilePicture)

        profile_name_TV.setText(Pref.user_name)
        address_EDT.setText(Pref.profile_address)
        if (Pref.profile_city.isNotBlank())
            city_EDT.setText(AppDatabase.getDBInstance()?.cityDao()?.getNameFromId(Pref.profile_city.toInt()))
        if (Pref.profile_state.isNotBlank())
            state_EDT.setText(AppDatabase.getDBInstance()?.stateDao()?.getNameFromId(Pref.profile_state.toInt()))
        country_EDT.setText(Pref.profile_country)
        pincode_EDT.setText(Pref.profile_pincode)
    }

    private fun callUpDateProfileApi(profile_name: CharSequence, address: CharSequence, city: CharSequence, state: CharSequence, country: CharSequence, pincode: CharSequence) {
        var updateProfileData = ProfileUpdateRequestData()
        updateProfileData.user_id = Pref.user_id
        updateProfileData.session_token = Pref.session_token
        updateProfileData.latitude = latitude.toString()
        updateProfileData.longitude = longitude.toString()
        updateProfileData.address = address.toString()
        updateProfileData.user_name = profile_name.toString()
        updateProfileData.city = cityId
        updateProfileData.state = stateId
        updateProfileData.country = country.toString()
        updateProfileData.pincode = pincode.toString()

        var profile_image_path = ""
        if (profile_image_file.contains("http://") || profile_image_file.contains("https://"))
            profile_image_path = ""
        else
            profile_image_path = profile_image_file

        AppUtils.hideSoftKeyboard(mContext as DashboardActivity)

        val repository = MyProfileRepoProvider.provideUpdateProfileRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.updateProfileWithImage(updateProfileData, profile_image_path, mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as BaseResponse
                            if (response.status == NetworkConstant.SUCCESS) {
                                Pref.profile_state = stateId
                                Pref.profile_city = cityId
                                /*(mContext as DashboardActivity).showSnackMessage(response.message!!)
                                (mContext as DashboardActivity).onBackPressed()
                                Pref.isProfileUpdated = true
                                (mContext as DashboardActivity).setProfileImg()*/

                                val assignDDList = AppDatabase.getDBInstance()?.ddListDao()?.getAll()
                                if (/*(assignDDList == null || assignDDList.isEmpty()) &&*/ !TextUtils.isEmpty(Pref.profile_state))
                                    getAssignedDDListApi(response.message!!)
                                else {
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    (mContext as DashboardActivity).onBackPressed()
                                    Pref.isProfileUpdated = true
                                    (mContext as DashboardActivity).setProfileImg()
                                }

                            } else {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )
    }


    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                showPictureDialog()
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    private fun getAssignedDDListApi(message: String) {
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
                                            val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                                            if (/*(assignPPList == null || assignPPList.isEmpty()) &&*/ !TextUtils.isEmpty(Pref.profile_state))
                                                getAssignedPPListApi(message)
                                            else {
                                                (mContext as DashboardActivity).showSnackMessage(message)
                                                (mContext as DashboardActivity).onBackPressed()
                                                Pref.isProfileUpdated = true
                                                (mContext as DashboardActivity).setProfileImg()
                                            }
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                                    if (/*(assignPPList == null || assignPPList.isEmpty()) &&*/ !TextUtils.isEmpty(Pref.profile_state))
                                        getAssignedPPListApi(message)
                                    else {
                                        (mContext as DashboardActivity).showSnackMessage(message)
                                        (mContext as DashboardActivity).onBackPressed()
                                        Pref.isProfileUpdated = true
                                        (mContext as DashboardActivity).setProfileImg()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                                if (/*(assignPPList == null || assignPPList.isEmpty()) &&*/ !TextUtils.isEmpty(Pref.profile_state))
                                    getAssignedPPListApi(message)
                                else {
                                    (mContext as DashboardActivity).showSnackMessage(message)
                                    (mContext as DashboardActivity).onBackPressed()
                                    Pref.isProfileUpdated = true
                                    (mContext as DashboardActivity).setProfileImg()
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                            if (/*(assignPPList == null || assignPPList.isEmpty()) &&*/ !TextUtils.isEmpty(Pref.profile_state))
                                getAssignedPPListApi(message)
                            else {
                                (mContext as DashboardActivity).showSnackMessage(message)
                                (mContext as DashboardActivity).onBackPressed()
                                Pref.isProfileUpdated = true
                                (mContext as DashboardActivity).setProfileImg()
                            }
                        })
        )
    }

    private fun getAssignedPPListApi(message: String) {
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

                                            if (!TextUtils.isEmpty(Pref.profile_city))
                                                getAssignedToShopApi(message)
                                            else {
                                                (mContext as DashboardActivity).showSnackMessage(message)
                                                (mContext as DashboardActivity).onBackPressed()
                                                Pref.isProfileUpdated = true
                                                (mContext as DashboardActivity).setProfileImg()
                                            }
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    if (!TextUtils.isEmpty(Pref.profile_city))
                                        getAssignedToShopApi(message)
                                    else {
                                        (mContext as DashboardActivity).showSnackMessage(message)
                                        (mContext as DashboardActivity).onBackPressed()
                                        Pref.isProfileUpdated = true
                                        (mContext as DashboardActivity).setProfileImg()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                if (!TextUtils.isEmpty(Pref.profile_city))
                                    getAssignedToShopApi(message)
                                else {
                                    (mContext as DashboardActivity).showSnackMessage(message)
                                    (mContext as DashboardActivity).onBackPressed()
                                    Pref.isProfileUpdated = true
                                    (mContext as DashboardActivity).setProfileImg()
                                }
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            if (!TextUtils.isEmpty(Pref.profile_city))
                                getAssignedToShopApi(message)
                            else {
                                (mContext as DashboardActivity).showSnackMessage(message)
                                (mContext as DashboardActivity).onBackPressed()
                                Pref.isProfileUpdated = true
                                (mContext as DashboardActivity).setProfileImg()
                            }
                        })
        )
    }

    private fun getAssignedToShopApi(message: String) {
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
                                        if (!TextUtils.isEmpty(Pref.profile_city))
                                            getAreaListApi(message)
                                        else {
                                            (mContext as DashboardActivity).showSnackMessage(message)
                                            (mContext as DashboardActivity).onBackPressed()
                                            Pref.isProfileUpdated = true
                                            (mContext as DashboardActivity).setProfileImg()
                                        }
                                    }
                                }
                            }
                            else {
                                progress_wheel.stopSpinning()
                                if (!TextUtils.isEmpty(Pref.profile_city))
                                    getAreaListApi(message)
                                else {
                                    (mContext as DashboardActivity).showSnackMessage(message)
                                    (mContext as DashboardActivity).onBackPressed()
                                    Pref.isProfileUpdated = true
                                    (mContext as DashboardActivity).setProfileImg()
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            if (!TextUtils.isEmpty(Pref.profile_city))
                                getAreaListApi(message)
                            else {
                                (mContext as DashboardActivity).showSnackMessage(message)
                                (mContext as DashboardActivity).onBackPressed()
                                Pref.isProfileUpdated = true
                                (mContext as DashboardActivity).setProfileImg()
                            }
                        })
        )
    }

    private fun getAreaListApi(message: String) {
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

                                        val areaList = AppDatabase.getDBInstance()?.areaListDao()?.getAll()
                                        if (areaList != null && areaList.isNotEmpty())
                                            AppDatabase.getDBInstance()?.areaListDao()?.deleteAll()


                                        list.forEach {
                                            val area = AreaListEntity()
                                            AppDatabase.getDBInstance()?.areaListDao()?.insert(area.apply {
                                                area_id = it.area_id
                                                area_name = it.area_name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            (mContext as DashboardActivity).showSnackMessage(message)
                                            (mContext as DashboardActivity).onBackPressed()
                                            Pref.isProfileUpdated = true
                                            (mContext as DashboardActivity).setProfileImg()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(message)
                                    (mContext as DashboardActivity).onBackPressed()
                                    Pref.isProfileUpdated = true
                                    (mContext as DashboardActivity).setProfileImg()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(message)
                                (mContext as DashboardActivity).onBackPressed()
                                Pref.isProfileUpdated = true
                                (mContext as DashboardActivity).setProfileImg()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(message)
                            (mContext as DashboardActivity).onBackPressed()
                            Pref.isProfileUpdated = true
                            (mContext as DashboardActivity).setProfileImg()
                        })
        )
    }


    fun launchCamera() {
        //if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity) && PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
        AppUtils.isProfile = true
        /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, (mContext as DashboardActivity).getPhotoFileUri(System.currentTimeMillis().toString() + ".png"))
        (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_CAMERA)*/

        (mContext as DashboardActivity).captureImage()
        //}
    }


    fun selectImageInAlbum() {
        //if (PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
        AppUtils.isProfile = true
        /*val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_STORAGE)*/

        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        (mContext as DashboardActivity).startActivityForResult(galleryIntent, PermissionHelper.REQUEST_CODE_STORAGE)
        //}
    }

    fun setImage(imgRealPath: Uri) {
        profile_image_file = imgRealPath.toString()
        //  Picasso.with(context).load(imgRealPath).into(profilePicture)
        Picasso.get()
                .load(imgRealPath)
                .resize(100, 100)
                .into(profilePicture)
    }

    fun setImage(imgRealPath: String) {
        profile_image_file = imgRealPath
        //  Picasso.with(context).load(imgRealPath).into(profilePicture)
        /*Picasso.get()
                .load(imgRealPath)
                .resize(100, 100)
                .into(profilePicture)*/

        Glide.with(mContext)
                .load(imgRealPath)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_menu_profile_image).error(R.drawable.ic_menu_profile_image))
                .into(profilePicture)
    }

    fun showPictureDialog() {
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

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}