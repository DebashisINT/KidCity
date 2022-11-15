package com.kcteam.features.averageshop.presentation

import android.view.View

/**
 * Created by Pratishruti on 30-10-2017.
 */
interface AverageShopListClickListener {
    fun OnMenuClick(position: Int,view:View)
    fun OnItemClick(position: Int)
    fun onSyncClick(position: Int)
    fun onQuestionnarieClick(shopId:String)
    fun onReturnClick(position: Int)
    fun onDamageClick(shop_id: String)
    fun onSurveyClick(shop_id: String)
    fun onMultipleImageClick(shop: Any,position: Int)


}