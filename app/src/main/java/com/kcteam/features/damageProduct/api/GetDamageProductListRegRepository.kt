package com.kcteam.features.damageProduct.api

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.kcteam.app.FileUtils
import com.kcteam.base.BaseResponse
import com.kcteam.features.NewQuotation.model.*
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.damageProduct.model.DamageProductResponseModel
import com.kcteam.features.damageProduct.model.delBreakageReq
import com.kcteam.features.damageProduct.model.viewAllBreakageReq
import com.kcteam.features.login.model.userconfig.UserConfigResponseModel
import com.kcteam.features.myjobs.model.WIPImageSubmit
import com.kcteam.features.photoReg.model.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class GetDamageProductListRegRepository(val apiService : GetDamageProductListApi) {

    fun viewBreakage(req: viewAllBreakageReq): Observable<DamageProductResponseModel> {
        return apiService.viewBreakage(req)
    }

    fun delBreakage(req: delBreakageReq): Observable<BaseResponse>{
        return apiService.BreakageDel(req.user_id!!,req.breakage_number!!,req.session_token!!)
    }

}