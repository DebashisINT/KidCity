package com.kcteam.features.login.model.productlistmodel

import com.kcteam.app.domain.ProductRateEntity
import com.kcteam.base.BaseResponse
import java.io.Serializable

/**
 * Created by Saikat on 28-May-20.
 */
data class ProductListOfflineResponseModel(var product_rate_list: ArrayList<ProductRateOfflineDataModel>? = null) : BaseResponse(), Serializable


data class ProductListOfflineResponseModelNew(var product_rate_list: ArrayList<ProductRateEntity>? = null) : BaseResponse(), Serializable

data class ProductRateOfflineDataModel(var product_id: String = "",
                                       var rate1: String = "",
                                       var rate2: String = "",
                                       var rate3: String = "",
                                       var rate4: String = "",
                                       var rate5: String = "",
                                       var stock_amount: String = "",
                                       var stock_unit: String = "",
                                       var isStockShow: Boolean = false,
                                       var isRateShow: Boolean = false) : Serializable