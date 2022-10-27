package com.kcteam.features.shopdetail.presentation.api.addcollection

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.kcteam.app.FileUtils
import com.kcteam.base.BaseResponse
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.shopdetail.presentation.model.addcollection.AddCollectionInputParamsModel
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by Saikat on 13-11-2018.
 */
class AddCollectionRepo(val apiService: AddCollectionApi) {

    fun addCollection(addCollection: AddCollectionInputParamsModel): Observable<BaseResponse> {
        return apiService.addCollection(addCollection)
    }

    fun addCollection(addCollection: AddCollectionInputParamsModel, image: String?, context: Context): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null

        var profile_img_file: File? = null

        if (image?.startsWith("file")!!)
            profile_img_file = FileUtils.getFile(context, Uri.parse(image))
        else {
            profile_img_file = File(image)

            if (!profile_img_file?.exists()) {
                profile_img_file?.createNewFile()
            }
        }

        if (profile_img_file != null && profile_img_file.exists()) {
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
            profile_img_data = MultipartBody.Part.createFormData("doc", profile_img_file.name, profileImgBody)
        } else {
            var mFile: File
            mFile = (context as DashboardActivity).getShopDummyImageFile()
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
            profile_img_data = MultipartBody.Part.createFormData("doc", mFile.name, profileImgBody)
        }

        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(addCollection)
            //shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
            //shopObject = RequestBody.create(MediaType.parse("text/plain"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return apiService.addCollectionMultipart(jsonInString, profile_img_data)
        //return apiService.addCollectionMultipart(shopObject!!, profile_img_data)
    }
}