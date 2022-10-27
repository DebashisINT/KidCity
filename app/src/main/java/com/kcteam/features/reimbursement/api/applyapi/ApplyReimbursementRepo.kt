package com.kcteam.features.reimbursement.api.applyapi

import android.util.Log
import com.kcteam.base.BaseResponse
import com.kcteam.features.reimbursement.model.AppliedReimbursementDeleteInputModel
import com.kcteam.features.reimbursement.model.ApplyReimbursementInputModel
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by Saikat on 25-01-2019.
 */
class ApplyReimbursementRepo(val apiService: ApplyReimbursementApi) {
    fun applyReimbursement(input: ApplyReimbursementInputModel): Observable<BaseResponse> {
        return apiService.applyReimbursement(input)
    }

    fun applyReimbursementImageUpload(applyrip: ApplyReimbursementInputModel, imgPathArray: ArrayList<String>): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val multiPartArray: java.util.ArrayList<MultipartBody.Part> = arrayListOf()

        for (item in imgPathArray) {

            Thread.sleep(200)

            val profile_img_file = File(item)
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
            val fileExt = File(item).extension
            val imageName = applyrip.user_id + "~" + applyrip.date + "~" + applyrip.Expense_mapId + "~" + applyrip.visit_type_id + "~" + applyrip.expense_details[0].expence_type_id + "~"
            val fileName = imageName + "img_" + System.currentTimeMillis() + "." + fileExt

            Log.e("Reimbursement Image", "File Name=========> $fileName")

            profile_img_data = MultipartBody.Part.createFormData("ImageName" + System.currentTimeMillis(), fileName, profileImgBody)
            multiPartArray.add(profile_img_data)

        }

        return apiService.applyReimbursementImage(multiPartArray)
    }

    fun deleteAppliedReimbursement(input: AppliedReimbursementDeleteInputModel): Observable<BaseResponse> {
        return apiService.deleteReimbursement(input)
    }
}