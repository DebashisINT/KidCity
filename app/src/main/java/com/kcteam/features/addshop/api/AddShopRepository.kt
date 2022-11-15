package com.kcteam.features.addshop.api

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.kcteam.app.FileUtils
import com.kcteam.app.Pref
import com.kcteam.base.BaseResponse
import com.kcteam.features.addshop.model.*
import com.kcteam.features.addshop.model.assigntopplist.AddShopUploadImg
import com.kcteam.features.addshop.model.assigntopplist.AddshopImageMultiReqbody1
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.google.gson.Gson
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by Pratishruti on 22-11-2017.
 */
class AddShopRepository(val apiService: AddShopApi) {

    fun addQues(questionSubmit: AddQuestionSubmitRequestData): Observable<BaseResponse> {
        return apiService.getAddQuestionSubmit(questionSubmit)
    }

    fun addQuesUpdate(questionSubmit: AddQuestionSubmitRequestData): Observable<BaseResponse> {
        return apiService.getAddQuestionUpdateSubmit(questionSubmit)
    }

    fun addShop(shop: AddShopRequestData): Observable<AddShopResponse> {
        return apiService.getAddShop(shop)
    }

    fun fetchData(shop_id:String): Observable<imageListResponse> {
        return apiService.geimagelist(shop_id,Pref.user_id!!,Pref.session_token!!)
    }

    fun addShopWithImage(shop: AddShopRequestData, shop_image: String, context: Context): Observable<AddShopResponse> {
        var profile_img_data: MultipartBody.Part? = null

        val profile_img_file = FileUtils.getFile(context, Uri.parse(shop_image))
        if (profile_img_file!=null && profile_img_file.exists()) {
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
            profile_img_data = MultipartBody.Part.createFormData("shop_image", profile_img_file.name, profileImgBody)
        } else {
            var mFile: File
            mFile = (context as DashboardActivity).getShopDummyImageFile()
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
            profile_img_data = MultipartBody.Part.createFormData("shop_image", mFile.name, profileImgBody)
        }


        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(shop)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return apiService.getAddShopWithImage(jsonInString, profile_img_data)
        // return apiService.getAddShopWithoutImage(jsonInString)
    }

    fun addShopWithImage(shop: AddShopRequestData, shop_image: String?, degree_image: String?, context: Context): Observable<AddShopResponse> {
        var profile_img_data: MultipartBody.Part? = null
        var degree_img_data: MultipartBody.Part? = null
        /*val multiPartArray: java.util.ArrayList<MultipartBody.Part> = arrayListOf()

        val profile_img_file = FileUtils.getFile(context, Uri.parse(shop_image))
        if (profile_img_file != null && profile_img_file.exists()) {
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
            profile_img_data = MultipartBody.Part.createFormData("shop_image", profile_img_file.name, profileImgBody)
        } else {
            var mFile: File
            mFile = (context as DashboardActivity).getShopDummyImageFile()
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
            profile_img_data = MultipartBody.Part.createFormData("shop_image", mFile.name, profileImgBody)
        }


        if (!TextUtils.isEmpty(degree_image)) {

            multiPartArray.add(profile_img_data)

            val degree_image_file = FileUtils.getFile(context, Uri.parse(degree_image))
            if (degree_image_file != null && degree_image_file.exists()) {
                val degreeImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), degree_image_file)
                degree_img_data = MultipartBody.Part.createFormData("degree_image", degree_image_file.name, degreeImgBody)
            } else {
                var mFile: File
                mFile = (context as DashboardActivity).getShopDummyImageFile()
                val degreeImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
                degree_img_data = MultipartBody.Part.createFormData("degree_image", mFile.name, degreeImgBody)
            }

            multiPartArray.add(degree_img_data)
        }


        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = Gson().toJson(shop)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return if (degree_img_data != null)
            apiService.getAddShopWithImage(jsonInString, multiPartArray)
        else
            apiService.getAddShopWithImage(jsonInString, profile_img_data)
        // return apiService.getAddShopWithoutImage(jsonInString)*/

        if (!TextUtils.isEmpty(shop_image)) {
            val profile_img_file = FileUtils.getFile(context, Uri.parse(shop_image))
            if (profile_img_file != null && profile_img_file.exists()) {
                val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
                profile_img_data = MultipartBody.Part.createFormData("shop_image", profile_img_file.name, profileImgBody)
            } else {
                var mFile: File
                mFile = (context as DashboardActivity).getShopDummyImageFile()
                val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
                profile_img_data = MultipartBody.Part.createFormData("shop_image", mFile.name, profileImgBody)
            }
        } else if (!TextUtils.isEmpty(degree_image)) {
            val degree_image_file = FileUtils.getFile(context, Uri.parse(degree_image))
            if (degree_image_file != null && degree_image_file.exists()) {
                val degreeImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), degree_image_file)
                degree_img_data = MultipartBody.Part.createFormData("degree", degree_image_file.name, degreeImgBody)
            } else {
                var mFile: File
                mFile = (context as DashboardActivity).getShopDummyImageFile()
                val degreeImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
                degree_img_data = MultipartBody.Part.createFormData("degree", mFile.name, degreeImgBody)
            }
        }


        var jsonInString = ""
        try {
            jsonInString = Gson().toJson(shop)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return if (degree_img_data != null)
            apiService.getAddShopWithDocImage(jsonInString, degree_img_data)
        else
            apiService.getAddShopWithImage(jsonInString, profile_img_data)
    }

    fun addShopWithImageCompetetorImg(shop: AddShopRequestCompetetorImg, shop_image: String?, context: Context): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        var degree_img_data: MultipartBody.Part? = null
        /*val multiPartArray: java.util.ArrayList<MultipartBody.Part> = arrayListOf()

        val profile_img_file = FileUtils.getFile(context, Uri.parse(shop_image))
        if (profile_img_file != null && profile_img_file.exists()) {
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
            profile_img_data = MultipartBody.Part.createFormData("shop_image", profile_img_file.name, profileImgBody)
        } else {
            var mFile: File
            mFile = (context as DashboardActivity).getShopDummyImageFile()
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
            profile_img_data = MultipartBody.Part.createFormData("shop_image", mFile.name, profileImgBody)
        }


        if (!TextUtils.isEmpty(degree_image)) {

            multiPartArray.add(profile_img_data)

            val degree_image_file = FileUtils.getFile(context, Uri.parse(degree_image))
            if (degree_image_file != null && degree_image_file.exists()) {
                val degreeImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), degree_image_file)
                degree_img_data = MultipartBody.Part.createFormData("degree_image", degree_image_file.name, degreeImgBody)
            } else {
                var mFile: File
                mFile = (context as DashboardActivity).getShopDummyImageFile()
                val degreeImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
                degree_img_data = MultipartBody.Part.createFormData("degree_image", mFile.name, degreeImgBody)
            }

            multiPartArray.add(degree_img_data)
        }


        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = Gson().toJson(shop)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return if (degree_img_data != null)
            apiService.getAddShopWithImage(jsonInString, multiPartArray)
        else
            apiService.getAddShopWithImage(jsonInString, profile_img_data)
        // return apiService.getAddShopWithoutImage(jsonInString)*/

        if (!TextUtils.isEmpty(shop_image)) {
            val profile_img_file = FileUtils.getFile(context, Uri.parse(shop_image))
            if (profile_img_file != null && profile_img_file.exists()) {
                val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
                profile_img_data = MultipartBody.Part.createFormData("competitor_img", profile_img_file.name.replace("cropped","").replace("5",""), profileImgBody)
            } else {
                var mFile: File
                mFile = (context as DashboardActivity).getShopDummyImageFile()
                val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
                profile_img_data = MultipartBody.Part.createFormData("competitor_img", mFile.name, profileImgBody)
            }
        } else{

        }

        var jsonInString = ""
        try {
            jsonInString = Gson().toJson(shop)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return  apiService.getAddShopCompetetorImage(jsonInString, profile_img_data)
    }


    /*9-12-2021*/
    fun addShopWithImageuploadImg1(image: AddShopUploadImg, upload_image: String?, context: Context): Observable<BaseResponse> {
        var img_data: MultipartBody.Part? = null
        if (!TextUtils.isEmpty(upload_image)) {
            val profile_img_file = FileUtils.getFile(context, Uri.parse(upload_image))
            if (profile_img_file != null && profile_img_file.exists()) {
                val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
                img_data = MultipartBody.Part.createFormData("rubylead_image1", profile_img_file.name.replace("cropped","").replace("5",""), profileImgBody)
            } else {
                var mFile: File
                mFile = (context as DashboardActivity).getShopDummyImageFile()
                val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
                img_data = MultipartBody.Part.createFormData("rubylead_image1", mFile.name, profileImgBody)
            }
        } else{

        }

        var jsonInString = ""
        try {
            jsonInString = Gson().toJson(image)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return  apiService.getAddShopUploadImage(jsonInString, img_data)
    }


    fun addShopWithImageuploadImg2(image: AddShopUploadImg, upload_image: String?, context: Context): Observable<BaseResponse> {
        var img_data: MultipartBody.Part? = null
        if (!TextUtils.isEmpty(upload_image)) {
            val profile_img_file = FileUtils.getFile(context, Uri.parse(upload_image))
            if (profile_img_file != null && profile_img_file.exists()) {
                val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
                img_data = MultipartBody.Part.createFormData("rubylead_image2", profile_img_file.name.replace("cropped","").replace("5",""), profileImgBody)
            } else {
                var mFile: File
                mFile = (context as DashboardActivity).getShopDummyImageFile()
                val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
                img_data = MultipartBody.Part.createFormData("rubylead_image2", mFile.name, profileImgBody)
            }
        } else{

        }

        var jsonInString = ""
        try {
            jsonInString = Gson().toJson(image)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return  apiService.getAddShopUploadImage2(jsonInString, img_data)
    }






    fun uploadImage(shop_image: String, context: Context): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        if (!TextUtils.isEmpty(shop_image)) {
            val profile_img_file = FileUtils.getFile(context, Uri.parse(shop_image))
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
            profile_img_data = MultipartBody.Part.createFormData("file", profile_img_file.name, profileImgBody)
        }

        return apiService.uploadImage(profile_img_data)
    }

    //02-11-2021
    fun getShopPhoneNumberAllStatus(new_shop_phone:String): Observable<BaseResponse> {
        return apiService.getDuplicationshopPhoneNumber(Pref.user_id!!,Pref.session_token!! ,new_shop_phone)
    }


    /*Mutiply Image*/
    fun addShopWithImageuploadMultipleImg1(image: AddshopImageMultiReqbody1, upload_image: String?, context: Context): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val profile_img_file = File(upload_image)
        val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
        val fileExt = File(upload_image).extension
        val imageName = "name_1"
        val fileName = imageName + "img_" + System.currentTimeMillis() + "." + fileExt
        profile_img_data = MultipartBody.Part.createFormData("attachment_image1", fileName, profileImgBody)

        var jsonInString = ""
        try {
            jsonInString = Gson().toJson(image)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return  apiService.UploadAttachImage1(jsonInString, profile_img_data)
    }

    fun addShopWithImageuploadMultipleImg2(image: AddshopImageMultiReqbody1, upload_image: String?, context: Context): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val profile_img_file = File(upload_image)
        val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
        val fileExt = File(upload_image).extension
        val imageName = "name_2"
        val fileName = imageName + "img_" + System.currentTimeMillis() + "." + fileExt
        profile_img_data = MultipartBody.Part.createFormData("attachment_image2", fileName, profileImgBody)

        var jsonInString = ""
        try {
            jsonInString = Gson().toJson(image)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return  apiService.UploadAttachImage2(jsonInString, profile_img_data)
    }

    fun addShopWithImageuploadMultipleImg3(image: AddshopImageMultiReqbody1, upload_image: String?, context: Context): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val profile_img_file = File(upload_image)
        val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
        val fileExt = File(upload_image).extension
        val imageName = "name_3"
        val fileName = imageName + "img_" + System.currentTimeMillis() + "." + fileExt
        profile_img_data = MultipartBody.Part.createFormData("attachment_image3", fileName, profileImgBody)

        var jsonInString = ""
        try {
            jsonInString = Gson().toJson(image)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return  apiService.UploadAttachImage3(jsonInString, profile_img_data)
    }

    fun addShopWithImageuploadMultipleImg4(image: AddshopImageMultiReqbody1, upload_image: String?, context: Context): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val profile_img_file = File(upload_image)
        val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
        val fileExt = File(upload_image).extension
        val imageName = "name_4"
        val fileName = imageName + "img_" + System.currentTimeMillis() + "." + fileExt
        profile_img_data = MultipartBody.Part.createFormData("attachment_image4", fileName, profileImgBody)

        var jsonInString = ""
        try {
            jsonInString = Gson().toJson(image)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return  apiService.UploadAttachImage4(jsonInString, profile_img_data)
    }



}