package com.kcteam.features.marketing.api.marketingrequest

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.kcteam.app.FileUtils
import com.kcteam.base.BaseResponse
import com.kcteam.features.marketing.model.MarketingDetailImageData
import com.kcteam.features.marketing.model.MarketingDetailSubmitRequest
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by Pratishruti on 28-02-2018.
 */
class MarketingDetailSubmitRepo(val apiService: MarketingDetailSubmitApi) {
    fun submitMarketingDetails(marketingDetail: MarketingDetailSubmitRequest, marketing_img: List<MarketingDetailImageData>, context: Context): Observable<BaseResponse> {
        var profile_img_data: MutableList<MultipartBody.Part> = arrayListOf()
        for (i in 0 until marketing_img.size) {
            if (!TextUtils.isEmpty(marketing_img[i].image_url)) {
                val profile_img_file = FileUtils.getFile(context, Uri.parse(marketing_img[i].image_url))
                val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
                profile_img_data.add(MultipartBody.Part.createFormData("material_image", profile_img_file.name, profileImgBody))
            }
        }
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(marketingDetail)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        if (profile_img_data.isEmpty())
            return apiService.sendMarketingDetailReqWithoutImg(jsonInString)
        else
            return apiService.sendMarketingDetailReq(jsonInString, profile_img_data)
    }
}