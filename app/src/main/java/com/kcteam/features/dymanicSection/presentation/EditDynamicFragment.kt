package com.kcteam.features.dymanicSection.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.app.AlertDialog
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.dymanicSection.api.DynamicRepoProvider
import com.kcteam.features.dymanicSection.model.*
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EditDynamicFragment: BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var ll_dynamic_body: LinearLayout
    private lateinit var progress_wheel: ProgressWheel
    //private lateinit var tv_save_btn: AppCustomTextView
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var rl_add_dynamic_main: RelativeLayout

    private var selectedDate = ""
    private var dateText: View?= null
    private var permissionUtils: PermissionUtils? = null
    private var dataPath = ""
    private var attachments = ""
    private var attachmentText: View?= null
    private var dynamicListModel: DynamicListDataModel?= null

    private val editList by lazy {
        ArrayList<AppCustomEditText>()
    }

    private val textList by lazy {
        ArrayList<AppCustomTextView>()
    }

    private val radioList by lazy {
        ArrayList<RadioGroup>()
    }

    private val radioMap by lazy {
        HashMap<String, RadioButton>()
    }

    private val checkList by lazy {
        ArrayList<CheckBox>()
    }

    private val checkBoxModelList by lazy {
        ArrayList<checkBoxModel>()
    }


    private val spinnerList by lazy {
        ArrayList<Spinner>()
    }

    private val spinerMap by lazy {
        HashMap<String, String>()
    }

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    private val dateMap by lazy {
        HashMap<String, String>()
    }

    companion object {
        fun newInstance(dynamicListModel: Any): EditDynamicFragment {
            val fragment = EditDynamicFragment()

            if (dynamicListModel is DynamicListDataModel) {
                val bundle = Bundle()
                bundle.putSerializable("dynamicListModel", dynamicListModel)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        dynamicListModel = arguments?.getSerializable("dynamicListModel") as DynamicListDataModel?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_dynamic, container, false)

        initView(view)
        getDynamicData()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            ll_dynamic_body = findViewById(R.id.ll_dynamic_body)
            progress_wheel = findViewById(R.id.progress_wheel)
            //tv_save_btn = findViewById(R.id.tv_save_btn)
            tv_no_data = findViewById(R.id.tv_no_data)
            rl_add_dynamic_main = findViewById(R.id.rl_add_dynamic_main)
        }

        rl_add_dynamic_main.setOnClickListener(null)
        progress_wheel.stopSpinning()
    }

    private fun getDynamicData() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            tv_no_data.visibility = View.VISIBLE
            return
        }

        val repository = DynamicRepoProvider.dynamicRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getEditDynamicData(dynamicListModel)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            progress_wheel.stopSpinning()

                            val response = result as DynamicResponseModel

                            XLog.d("DYNAMIC RESPONSE=======> " + response.status)

                            if (response.status == NetworkConstant.SUCCESS) {
                                if (response.view_list != null && response.view_list!!.size > 0) {
                                    tv_no_data.visibility = View.GONE
                                    createDynamicView(response.view_list!!)

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
                            XLog.d("DYNAMIC ERROR=======> " + error.localizedMessage)
                        })
        )
    }

    private fun createDynamicView(view_list: ArrayList<DynamicDataModel>) {
        view_list.forEachIndexed { index, it ->
            if (it.type.equals("edit", ignoreCase = true)) {
                val til = TextInputLayout(mContext)
                til.hint = it.header
                val editText = AppCustomEditText(mContext)
                editText.apply {
                    setTextColor(resources.getColor(R.color.black))
                    setHintTextColor(resources.getColor(R.color.login_txt_color))
                    textSize = resources.getDimension(R.dimen._6sdp)
                    id = it.id.toInt() //View.generateViewId()
                    setText(it.value)
                    when {
                        it.text_type.equals("text", ignoreCase = true) -> inputType = InputType.TYPE_CLASS_TEXT
                        it.text_type.equals("number", ignoreCase = true) -> inputType = InputType.TYPE_CLASS_NUMBER
                        it.text_type.equals("numberdecimel", ignoreCase = true) -> inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                        it.text_type.equals("email", ignoreCase = true) -> inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    }
                    maxLines = 1
                    filters = arrayOf(InputFilter.LengthFilter(it.max_length.toInt()))
                }.let {
                    editList.add(it)
                }
                val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                if (index == 0)
                    p.topMargin = resources.getDimensionPixelOffset(R.dimen._10sdp)
                else
                    p.topMargin = resources.getDimensionPixelOffset(R.dimen._20sdp)
                til.apply {
                    layoutParams = p
                    addView(editText)
                    setPadding(10, 0, 10, 0)
                }.let {
                    ll_dynamic_body.addView(it)
                }
            } else if (it.type.equals("text", ignoreCase = true)) {
                val textView = AppCustomTextView(mContext)
                val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                if (index == 0)
                    p.topMargin = resources.getDimensionPixelOffset(R.dimen._10sdp)
                else
                    p.topMargin = resources.getDimensionPixelOffset(R.dimen._20sdp)

                textView.apply {
                    layoutParams = p
                    setPadding(10, 0, 10, 0)
                    setTextColor(resources.getColor(R.color.gray))
                    textSize = resources.getDimension(R.dimen._6sdp)
                    id = it.id.toInt() //View.generateViewId()
                    text = it.header
                }.let {
                    ll_dynamic_body.addView(it)
                    textList.add(it)
                }
            } else if (it.type.equals("radio", ignoreCase = true)) {
                val radioGrp = RadioGroup(mContext)
                radioGrp.orientation = RadioGroup.HORIZONTAL
                val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                p.topMargin = resources.getDimensionPixelOffset(R.dimen._5sdp)
                radioGrp.layoutParams = p
                radioGrp.id = it.id.toInt() //View.generateViewId()

                it.item_list?.let { item ->
                    item.forEach { it1 ->
                        val radioBtn = RadioButton(mContext)
                        radioBtn.apply {
                            id = it1.id.toInt() //View.generateViewId()
                            text = it1.items
                            textSize = resources.getDimension(R.dimen._6sdp)
                            setTextColor(resources.getColor(R.color.black))
                            val rbParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            rbParams.marginEnd = resources.getDimensionPixelOffset(R.dimen._15sdp)
                            layoutParams = rbParams
                            isChecked = it1.isSelected

                            if (isChecked)
                                radioMap.put(radioGrp.id.toString(), this)
                        }.let {
                            radioGrp.addView(it)
                        }
                    }
                }

                radioGrp.setOnCheckedChangeListener { group, checkedId ->
                    for (i in 0 until group?.childCount!!) {
                        val selectedRB = group.getChildAt(i) as RadioButton
                        if (selectedRB.id == checkedId) {
                            /*for (j in radioList.indices) {
                                if (radioList[j].id == checkedId) {
                                    radioList.removeAt(j)
                                    break
                                }
                            }*/
                            if (selectedRB.isChecked)
                                radioMap.put(group.id.toString(), selectedRB)
                        }
                    }
                }
                ll_dynamic_body.addView(radioGrp)
                radioList.add(radioGrp)

            } else if (it.type.equals("check", ignoreCase = true)) {
                val grp_id = it.id
                it.item_list?.let { item ->
                    item.forEach { it1 ->
                        val checkBox = CheckBox(mContext)
                        checkBox.apply {
                            setTextColor(resources.getColor(R.color.black))
                            text = it1.items
                            textSize = resources.getDimension(R.dimen._6sdp)
                            id = it1.id.toInt() //View.generateViewId()
                            val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            p.topMargin = resources.getDimensionPixelOffset(R.dimen._2sdp)
                            layoutParams = p
                            isChecked = it1.isSelected

                            if (isChecked)
                                checkList.add(this)

                        }.let {
                            it.setOnClickListener {
                                for (i in checkList.indices) {
                                    if (checkList[i].id == it.id) {
                                        checkList.removeAt(i)
                                        break
                                    }
                                }

                                if ((it as CheckBox).isChecked) {
                                    checkList.add(it as CheckBox)
                                }
                            }
                            checkBoxModelList.add(checkBoxModel(grp_id, it))
                            ll_dynamic_body.addView(it)
                        }
                    }
                }
            } else if (it.type.equals("dropdown", ignoreCase = true)) {
                val spinner = Spinner(mContext)
                val list = ArrayList<String>()
                it.item_list?.let { item ->
                    item.forEach { it1 ->
                        list.add(it1.items)
                    }
                }
                val arrayAdapter = ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, list)
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.apply {
                    adapter = arrayAdapter
                    for (i in it.item_list?.indices!!) {
                        if (it.item_list?.get(i)?.isSelected!!) {
                            setSelection(i, false)
                            spinerMap.put(it.id, it.item_list?.get(i)?.items!!)
                            break
                        }
                    }
                    //setSelection(0, false)
                    val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    p.topMargin = resources.getDimensionPixelOffset(R.dimen._5sdp)
                    layoutParams = p
                    id = it.id.toInt() //View.generateViewId()
                    background = resources.getDrawable(android.R.drawable.btn_dropdown)
                    setPadding(15, 15, 15, 15)
                    prompt = it.header

                }.let {
                    ll_dynamic_body.addView(it)
                    spinnerList.add(it)

                    it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            /*for (i in spinnerList.indices) {
                                if (spinnerList[i] == list[position]) {
                                    spinnerList.removeAt(i)
                                    break
                                }
                            }

                            spinnerList.add(list[position])*/
                            spinerMap.put(it.id.toString(), list[position])
                        }
                    }
                }
            }
            else if (it.type.equals("date", ignoreCase = true)) {
                val textView = AppCustomTextView(mContext)
                val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                p.topMargin = resources.getDimensionPixelOffset(R.dimen._5sdp)

                textView.apply {
                    layoutParams = p
                    setPadding(10, 0, 10, 0)
                    setTextColor(resources.getColor(R.color.colorPrimary))
                    textSize = resources.getDimension(R.dimen._6sdp)
                    id = it.id.toInt() //View.generateViewId()

                    if (TextUtils.isEmpty(it.value)) {
                        text = AppUtils.getFormattedDate(myCalendar.time)
                        selectedDate = AppUtils.getFormattedDateForApi(myCalendar.time)
                    }
                    else {
                        text = AppUtils.getFormattedDateFromDate(it.value)
                        selectedDate = it.value
                    }
                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_history_calendar_icon, 0, 0, 0)
                    compoundDrawablePadding = 10
                    setOnClickListener {
                        dateText = it
                        val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
                                myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH))

                        datePicker.show()
                    }
                }.let {
                    dateMap.put(it.id.toString(), selectedDate)
                    ll_dynamic_body.addView(it)
                }
            }
            else if (it.type.equals("attachment", ignoreCase = true)) {
                val textView = AppCustomTextView(mContext)
                val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                p.topMargin = resources.getDimensionPixelOffset(R.dimen._5sdp)

                textView.apply {
                    layoutParams = p
                    setPadding(10, 0, 10, 0)
                    setTextColor(resources.getColor(R.color.black))
                    textSize = resources.getDimension(R.dimen._6sdp)
                    id = it.id.toInt() //View.generateViewId()

                    if (!TextUtils.isEmpty(it.value)) {
                        text = it.value
                        attachments = text.toString().trim()
                    }
                    else
                        text = it.header

                    setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attachment, 0, 0, 0)
                    compoundDrawablePadding = 10
                    setOnClickListener {
                        attachmentText = it
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            initPermissionCheck()
                        else {
                            showPictureDialog()
                        }
                    }
                }.let {
                    ll_dynamic_body.addView(it)
                }
            }
        }

        val textView = AppCustomTextView(mContext)
        val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        p.setMargins(resources.getDimensionPixelOffset(R.dimen._60sdp),resources.getDimensionPixelOffset(R.dimen._30sdp),
                resources.getDimensionPixelOffset(R.dimen._60sdp),resources.getDimensionPixelOffset(R.dimen._20sdp))

        textView.apply {
            id = View.generateViewId()
            layoutParams = p
            setTextColor(resources.getColor(R.color.gray))
            textSize = resources.getDimension(R.dimen._6sdp)
            text = getString(R.string.save)
            background = resources.getDrawable(R.drawable.selector_blue_botton_bg)
            setPadding(resources.getDimensionPixelOffset(R.dimen._4sdp), resources.getDimensionPixelOffset(R.dimen._8sdp),
                    resources.getDimensionPixelOffset(R.dimen._4sdp), resources.getDimensionPixelOffset(R.dimen._8sdp))
            setTextColor(resources.getColor(R.color.white))
            textSize = resources.getDimension(R.dimen._5sdp)
            gravity = Gravity.CENTER
            setOnClickListener {
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                saveData()
            }
        }.let {
            ll_dynamic_body.addView(it)
        }
    }

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        (dateText as TextView).text = AppUtils.getFormattedDate(myCalendar.time)
        selectedDate = AppUtils.getFormattedDateForApi(myCalendar.time)
        dateMap.put((dateText as TextView).id.toString(), selectedDate)
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

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture Image", "Select file from file manager")
        pictureDialog.setItems(pictureDialogItems,
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        0 -> selectImageInAlbum()
                        1 -> {
                            //(mContext as DashboardActivity).openFileManager()
                            launchCamera()
                        }
                        2 -> {
                            (mContext as DashboardActivity).openFileManager()
                        }
                    }
                })
        pictureDialog.show()
    }

    private fun launchCamera() {
        (mContext as DashboardActivity).captureImage()
    }

    private fun selectImageInAlbum() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        (mContext as DashboardActivity).startActivityForResult(galleryIntent, PermissionHelper.REQUEST_CODE_STORAGE)
    }

    fun setImage(file: File) {
        (attachmentText as TextView).text = file.name
        dataPath = file.absolutePath
    }

    private fun saveData() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val dynamicList = ArrayList<DynamicSaveDataModel>()

        editList.forEach {
            dynamicList.add(DynamicSaveDataModel("", it.id.toString(), it.text.toString().trim()))
        }

        /*textList.forEach {
            dynamicList.add(DynamicSaveDataModel("", it.id.toString(), it.text.toString().trim()))
        }*/

        spinnerList.forEach {
            dynamicList.add(DynamicSaveDataModel("", it.id.toString(), spinerMap[it.id.toString()]!!))
        }

        radioList.forEach {
            val radioBtn = radioMap[it.id.toString()] as RadioButton
            dynamicList.add(DynamicSaveDataModel(it.id.toString(), radioBtn.id.toString(), radioBtn.isChecked.toString()))
        }

        checkList.forEach {
            for(i in checkBoxModelList.indices) {
                if (it.id == checkBoxModelList[i].checkBox?.id) {
                    dynamicList.add(DynamicSaveDataModel(checkBoxModelList[i].grp_id, checkBoxModelList[i].checkBox?.id.toString(),
                            checkBoxModelList[i].checkBox?.isChecked.toString()))
                }
            }
        }

        dateMap.forEach {
            dynamicList.add(DynamicSaveDataModel("", it.key, it.value))
        }

        if (TextUtils.isEmpty(dataPath)) {
            val dynamicInput = EditDynamicInputParams(Pref.session_token!!, Pref.user_id!!, dynamicListModel?.super_id!!, dynamicListModel?.id!!, dynamicList, attachments)

            val repository = DynamicRepoProvider.dynamicRepoProvider()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.editDynamicData(dynamicInput)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->

                                progress_wheel.stopSpinning()

                                val response = result as BaseResponse

                                XLog.d("ADD DYNAMIC RESPONSE=======> " + response.status)

                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                if (response.status == NetworkConstant.SUCCESS) {
                                    (mContext as DashboardActivity).isDynamicFormUpdated = true

                                    Handler().postDelayed(Runnable {
                                        (mContext as DashboardActivity).onBackPressed()
                                    }, 1000)
                                }

                            }, { error ->
                                BaseActivity.isApiInitiated = false
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                XLog.d("ADD DYNAMIC ERROR=======> " + error.localizedMessage)
                            })
            )
        }
        else {
            val dynamicInput = DynamicSubmitInputParams(Pref.session_token!!, Pref.user_id!!, dynamicListModel?.super_id!!, dynamicListModel?.id!!, dynamicList)

            val repository = DynamicRepoProvider.dynamicRepoProviderMultipart()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.editDynamicDataMultipart(dynamicInput, dataPath, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->

                                progress_wheel.stopSpinning()

                                val response = result as BaseResponse

                                XLog.d("ADD DYNAMIC RESPONSE=======> " + response.status)

                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                if (response.status == NetworkConstant.SUCCESS) {
                                    (mContext as DashboardActivity).isDynamicFormUpdated = true

                                    Handler().postDelayed(Runnable {
                                        (mContext as DashboardActivity).onBackPressed()
                                    }, 1000)
                                }

                            }, { error ->
                                BaseActivity.isApiInitiated = false
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                XLog.d("ADD DYNAMIC ERROR=======> " + error.localizedMessage)
                            })
            )
        }
    }
}