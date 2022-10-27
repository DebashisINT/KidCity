package com.kcteam.features.newcollection.model

import com.kcteam.base.BaseResponse
import java.io.Serializable

data class CollectionDetailsResponseModel(var total_pending: String = "",
                                          var total_paid: String = "",
                                          var today_pending: String = "",
                                          var today_paid: String = "") : BaseResponse(), Serializable

data class CollectionShopListResponseModel(var amount_list: ArrayList<CollectionShopListDataModel>?= null): BaseResponse(), Serializable

data class CollectionShopListDataModel(var shop_id: String = "",
                                       var shop_name: String = "",
                                       var shop_image: String = "",
                                       var total_amount: String = "",
                                       var total_collection: String = "",
                                       var total_bal: String = ""): Serializable

data class PaymentModeResponseModel(var paymemt_mode_list: ArrayList<PaymentModeDataModel>?= null): BaseResponse(), Serializable

data class PaymentModeDataModel(var id: String = "",
                                var name: String = ""): Serializable