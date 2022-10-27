package com.kcteam.features.newcollectionreport

import com.kcteam.app.domain.TeamAllShopDBModelEntity

interface TeamRepeatOrderShopsListClickListener {
    fun OnNearByShopsListClick(position: Int)
    fun mapClick(position: Int)
    fun orderClick(obj: TeamAllShopDBModelEntity)
    fun callClick(position: Int)
    fun syncClick(position: Int)
    fun updateLocClick(position: Int)
    fun onStockClick(position: Int)
    fun onUpdateStageClick(position: Int)
    fun onQuotationClick(position: Int)
    fun onActivityClick(position: Int)
    fun onShareClick(position: Int)
    fun onCollectionClick(position: Int)
    fun onWhatsAppClick(no: String)
    fun onSmsClick(no: String)
    fun onCreateQrClick(position: Int)
    fun onUpdatePartyStatusClick(position: Int)
    fun onUpdateBankDetailsClick(position: Int)
    fun onQuestionnarieClick(shopId:String)
    fun onReturnClick(position: Int)

    fun onHistoryClick(shop: Any)
}