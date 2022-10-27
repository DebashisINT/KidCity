package com.kcteam.features.quotation.model

import com.kcteam.base.BaseResponse
import java.io.Serializable

/**
 * Created by Saikat on 12-Jun-20.
 */
data class BSListResponseModel(var bs_list: ArrayList<BSListDataModel>? = null) : Serializable, BaseResponse()

data class BSListDataModel(var id: String = "",
                           var name: String = "") : Serializable

data class AddQuotInputModel(var session_token: String = "",
                             var user_id: String = "",
                             var shop_id: String = "",
                             var quo_id: String = "",
                             var quo_no: String = "",
                             var date: String = "",
                             var hypothecation: String = "",
                             var account_no: String = "",
                             var model_id: String = "",
                             var bs_id: String = "",
                             var gearbox: String = "",
                             var number1: String = "",
                             var value1: String = "",
                             var value2: String = "",
                             var tyres1: String = "",
                             var number2: String = "",
                             var value3: String = "",
                             var value4: String = "",
                             var tyres2: String = "",
                             var amount: String = "",
                             var discount: String = "",
                             var cgst: String = "",
                             var sgst: String = "",
                             var tcs: String = "",
                             var insurance: String = "",
                             var net_amount: String = "",
                             var remarks: String = "") : Serializable

data class QuotationListResponseModel(var quot_list: ArrayList<QuotationDataModel>? = null) : BaseResponse(), Serializable


data class QuotationDataModel(var quo_id: String = "",
                              var shop_id: String = "",
                              var quo_no: String = "",
                              var date: String = "",
                              var hypothecation: String = "",
                              var account_no: String = "",
                              var model_id: String = "",
                              var bs_id: String = "",
                              var gearbox: String = "",
                              var number1: String = "",
                              var value1: String = "",
                              var value2: String = "",
                              var tyres1: String = "",
                              var number2: String = "",
                              var value3: String = "",
                              var value4: String = "",
                              var tyres2: String = "",
                              var amount: String = "",
                              var discount: String = "",
                              var cgst: String = "",
                              var sgst: String = "",
                              var tcs: String = "",
                              var insurance: String = "",
                              var net_amount: String = "",
                              var remarks: String = "") : Serializable