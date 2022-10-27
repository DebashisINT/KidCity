package com.kcteam.features.permissionList

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.test.viewPermission.AdapterViewPermission
import com.elvishew.xlog.XLog
import kotlinx.android.synthetic.main.fragment_view_permission.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ViewPermissionFragment: BaseFragment(), View.OnClickListener {
    private lateinit var mContext: Context
    var permList = mutableListOf<PermissionDetails>()
    var permListDenied = mutableListOf<PermissionDetails>()

    data class PermissionDetails(var permissionName: String, var permissionTag: Int)

    private lateinit var iv_share: AppCompatImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_view_permission, container, false)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_frag_view_permission_phone_model.text = "Phone Model : " + Build.BRAND.toUpperCase() + " : " + Build.MODEL
        tv_frag_view_permission_android_version.text = "Android Version : " + android.os.Build.VERSION.RELEASE
        fab_frag_view_permission.setOnClickListener(this)

/*        val packageManager = activity!!.packageManager
        var info: PackageInfo = packageManager.getPackageInfo(activity?.packageName, PackageManager.GET_PERMISSIONS)
        var list = info.requestedPermissionsFlags
        var list1 = info.requestedPermissions
        info.lastUpdateTime

        for(i in 0..list.size-1){
            var obj:PermissionDetails= PermissionDetails(list1.get(i).replace("android.permission.",""), list.get(i))
            permList.add(obj)
        }
        showPermission()*/

      /*  var info: PackageInfo = mContext.packageManager.getPackageInfo(mContext.packageName, PackageManager.GET_PERMISSIONS)
        var list = info.requestedPermissionsFlags
        var list1 = info.requestedPermissions
        for(i in 0..list.size-1){
            if(list1.get(i)!="android.permission.ACCESS_GPS") {
                var obj: PermissionDetails = PermissionDetails(list1.get(i).replace("android.permission.", "").replace("_", " ")
                        .replace("com.google.android.c2dm.permission.RECEIVE", "Receive Data from Internet").
                        replace("com.kcteam.permission.C2D",""), list.get(i))

                if(list.get(i)==3){
                    permList.add(obj)
                }else{
                    permListDenied.add(obj)
                }


            }
        }
        val notifi:Boolean = NotificationManagerCompat.from(mContext).areNotificationsEnabled()

        if(notifi){
            permList.add(PermissionDetails("Notification", 3))
        }else{
            permListDenied.add(PermissionDetails("Notification", 1))
        }
        val manager: KeyguardManager = context!!.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val pm: PowerManager? = context!!.getSystemService(Context.POWER_SERVICE) as PowerManager?
        val lock:Boolean= manager.isDeviceSecure()

        permList= (permList+permListDenied).toMutableList()*/
        //showPermission()


        /* val appNameAndPermissions = StringBuffer()
         val appNameAndPermissionsTag = StringBuffer()
         val pmm: PackageManager = mContext.getPackageManager()
         val packages = pmm.getInstalledApplications(PackageManager.GET_META_DATA)

         val permiName:ArrayList<String>? = ArrayList()
         val permiTag:ArrayList<String>? = ArrayList()

         var applicationInfo:ApplicationInfo
         for ( applicationInfo in packages) {
             try {
                 var packageInfo: PackageInfo = pmm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

                 appNameAndPermissions.append(packageInfo.packageName + "*******:\n");
                 appNameAndPermissionsTag.append("-2\n")

                 //permiName?.add(packageInfo.packageName)
                 //permiTag?.add("-2")

                 var requestedPermissions: Array<String> = packageInfo.requestedPermissions
                 var requestedPermissionsTag = packageInfo.requestedPermissionsFlags

                 if (requestedPermissions != null) {
                     for (i in 1..requestedPermissions.size) {
                         appNameAndPermissions.append(requestedPermissions[i] + "\n");
                         appNameAndPermissionsTag.append(requestedPermissionsTag[i].toString() + "\n");

                         permiName?.add(requestedPermissions[i])
                         permiTag?.add(requestedPermissionsTag[i].toString())
                     }
                     appNameAndPermissions.append("\n");
                     appNameAndPermissionsTag.append("\n");
                 }
             }catch (e: java.lang.Exception){

             }
         }*/

        /*  val ss:Int= permiName!!.size
          val sss:Int=permiTag!!.size

          for(i in 0..permiName!!.size-1){
              permList.add(PermissionDetails("Notification", 3))
          }*/

        //showPermission()


        /*      var permissionLists: Array<String>? = null
              permissionLists = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                  arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                          Manifest.permission.INTERNET, Manifest.permission.WAKE_LOCK, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.SYSTEM_ALERT_WINDOW)
              else
                  arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                          Manifest.permission.INTERNET, Manifest.permission.WAKE_LOCK, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.SYSTEM_ALERT_WINDOW)

              for (permission in permissionLists) {
                  var str_permi: String = permission.toString().replace("android.permission.", "").replace("", "").replace("", "").replace("_", " ")
                  //str_permi=str_permi.replace("READ_","").replace("WRITE_","").replace("_"," ")
                  //permList.add(PermissionDetails(permission.toString().replace("android.permission.",""), ContextCompat.checkSelfPermission(context!!, permission)))
                  permList.add(PermissionDetails(str_permi, ContextCompat.checkSelfPermission(context!!, permission)))
              }

              if(notifi){
                  permList.add(PermissionDetails("Notification", 0))
              }else{
                  permList.add(PermissionDetails("Notification", -1))
              }*/


        //showPermission()


    }

    /*fun getPermiList(){
        rv_frag_view_permission.layoutManager= LinearLayoutManager(context)
        rv_frag_view_permission.adapter= activity?.let { AdapterViewPermission(it,permList) }
    }*/


    override fun onResume() {
        super.onResume()

        var strSub:String=""

        var shouldInsert = true
        permList.clear()

        var info: PackageInfo = mContext.packageManager.getPackageInfo(mContext.packageName, PackageManager.GET_PERMISSIONS)
        var list = info.requestedPermissionsFlags
        var list1 = info.requestedPermissions
        for (i in 0..list.size - 1) {
            if (list1.get(i) != "android.permission.ACCESS_GPS") {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && list1.get(i) == "android.permission.ACCESS_BACKGROUND_LOCATION"){
                    strSub=" (For Android 10 & Later)"
                }

                if ( list1.get(i) == "android.permission.USE_FULL_SCREEN_INTENT" || list1.get(i) == "android.permission.SYSTEM_ALERT_WINDOW"
                        || list1.get(i) == "android.permission.FOREGROUND_SERVICE"){
                    strSub=" (System Defined)"
                }

                var obj: PermissionDetails = PermissionDetails(list1.get(i).replace("android.permission.", "").replace("_", " ")
                        .replace("com.google.android.c2dm.permission.RECEIVE", "Receive Data from Internet").replace("com.rubyfood.permission.C2D", "")+strSub, list.get(i))

                strSub=""
                if (list.get(i) == 3) {
                    permList.add(obj)
                } else {
                    permListDenied.add(obj)
                }
            }
        }
        val notifi: Boolean = NotificationManagerCompat.from(mContext).areNotificationsEnabled()

        if (notifi) {
            permList.add(PermissionDetails("Notification", 3))
        } else {
            permListDenied.add(PermissionDetails("Notification", 1))
        }
        /*val manager: KeyguardManager = context!!.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val pm: PowerManager? = context!!.getSystemService(Context.POWER_SERVICE) as PowerManager?
        val lock:Boolean= manager.isDeviceSecure()*/

        permList = (permList + permListDenied).toMutableList()

        for(i in 0..permList.size-1){
            XLog.d("Permission Name"+permList.get(i).permissionName + " Status : ")
        }
        for(i in 0..permListDenied.size-1){
            XLog.d("Permission Name"+permListDenied.get(i).permissionName + " Status : ")
        }


        showPermission()
    }

    data class Permii(val packagename:String,val nameList:ArrayList<String>,val tag:ArrayList<Int>)
    var permiList:ArrayList<Permii> = ArrayList()

    fun allPackagePermission(packageName:String){
        val builder = StringBuilder()
        val perarr:ArrayList<String> = ArrayList()
        val perarrTag:ArrayList<Int> = ArrayList()
        try {
            val packageInfo: PackageInfo = mContext.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            var counter = 1
            for (i in packageInfo.requestedPermissions.indices) {
                //if (packageInfo.requestedPermissionsFlags[i] and REQUESTED_PERMISSION_GRANTED != 0) {
                val permission = packageInfo.requestedPermissions[i]
                val permissionTag = packageInfo.requestedPermissionsFlags[i]
                // To make permission name shorter
                //permission = permission.substring(permission.lastIndexOf(".")+1);
                builder.append("$counter. $permission\n")
                counter++
                perarr.add(permission)
                perarrTag.add(permissionTag)
                // }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //val ssa=builder.toString()
        //val asa = perarr
        permiList.add(Permii(packageName,perarr,perarrTag))

    }



    fun showPermission() {
        rv_frag_view_permission.layoutManager = LinearLayoutManager(context)
        rv_frag_view_permission.adapter = activity?.let { AdapterViewPermission(it, permList) }
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.fab_frag_view_permission -> {
                    val heading = "PERMISSION DETAILS"

                    var pdfBody = "\n\n\nDate: " + AppUtils.getFormattedDate(myCalendar.time) + "\n\n" +
                            "\n--------------------------------------------------------------------------------------------------------------------------------\n\n" +
                            "Model : " + Build.BRAND.toUpperCase() + " : " + Build.MODEL + "\n\nAndroid Version : " + android.os.Build.VERSION.RELEASE +
                            "\n--------------------------------------------------------------------------------------------------------------------------------\n\n"

                    var permiTag: String = ""

                    permList?.forEach {
                        if (it.permissionTag == 3) {
                            permiTag = "Permission Granted"
                        } else {
                            permiTag = "Permission Not Granted"
                        }

                        val name = "     " + it.permissionName + "\n\n        Status :   " + permiTag
                        pdfBody += name + "\n\n---------------------------------------------------------\n\n"
                    }

                    val image = BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher)

                    val path = FTStorageUtils.stringToPdf(pdfBody, mContext, "PermissionStatus_" +
                            "_" + Pref.user_id + ".pdf", image, heading, 3.7f)

                    if (!TextUtils.isEmpty(path)) {
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            val fileUrl = Uri.parse(path)

                            val file = File(fileUrl.path)
                            //val uri = Uri.fromFile(file)
                            //27-09-2021
                            val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
                            shareIntent.type = "image/png"
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                            startActivity(Intent.createChooser(shareIntent, "Share pdf using"));
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else
                        (mContext as DashboardActivity).showSnackMessage("Pdf can not be sent.")
                }
            }
        }
    }
}