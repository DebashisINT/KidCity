package com.kcteam.features.document.api

import android.content.Context
import android.net.Uri
import android.util.Log
import com.kcteam.app.FileUtils
import com.kcteam.app.Pref
import com.kcteam.app.domain.DocumentListEntity
import com.kcteam.base.BaseResponse
import com.kcteam.features.activities.model.ActivityImage
import com.kcteam.features.activities.model.AddActivityInputModel
import com.kcteam.features.document.model.AddEditDocumentInputParams
import com.kcteam.features.document.model.DocumentAttachmentModel
import com.kcteam.features.document.model.DocumentListResponseModel
import com.kcteam.features.document.model.DocumentTypeResponseModel
import com.kcteam.features.dymanicSection.api.DynamicApi
import com.kcteam.features.dymanicSection.model.DynamicResponseModel
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class DocumentRepo(val apiService: DocumentApi) {
    fun getDocType(): Observable<DocumentTypeResponseModel> {
        return apiService.getDocType(Pref.session_token!!, Pref.user_id!!)
    }

    fun getDocList(typeId:String): Observable<DocumentListResponseModel> {
        return apiService.getDocList(Pref.session_token!!, Pref.user_id!!,typeId)
    }

    fun addEditDoc(docInput: AddEditDocumentInputParams, documentList: ArrayList<DocumentAttachmentModel>, context: Context): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val multiPartArray: java.util.ArrayList<MultipartBody.Part> = arrayListOf()

        for (item in documentList) {

            Thread.sleep(500)

            var profile_img_file: File? = null
            if (item.link.startsWith("file"))
                profile_img_file = FileUtils.getFile(context, Uri.parse(item.link))
            else {
                profile_img_file = File(item.link)

                if (!profile_img_file?.exists()) {
                    profile_img_file?.createNewFile()
                }
            }

            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
            //val fileExt = FileUtils.getFile(context, Uri.parse(item.link)).extension //File(item.link).extension

            var fileExt = ""
            fileExt = if (item.link.startsWith("file"))
                FileUtils.getFile(context, Uri.parse(item.link)).extension
            else
                File(item.link).extension

            val imageName = docInput.user_id + "~" + item.list_id + "~" + item.type_id + "~" + item.date_time + "~"
            val fileName = imageName + "_" + System.currentTimeMillis() + "." + fileExt

            Log.e("Document Image", "File Name=========> $fileName")

            profile_img_data = MultipartBody.Part.createFormData("attachment", fileName, profileImgBody)
            multiPartArray.add(profile_img_data)

        }

        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(docInput)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return apiService.addEditDocument(jsonInString, multiPartArray)
        // return apiService.getAddShopWithoutImage(jsonInString)
    }

    fun deleteDoc(id: String): Observable<BaseResponse> {
        return apiService.deleteDocument(Pref.session_token!!, Pref.user_id!!, id)
    }
}