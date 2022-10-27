package com.kcteam.features.localshops

/**
 * Created by riddhi on 2/1/18.
 */
interface LocalShopListClickListener {

    fun visitShop(shop: Any)

    fun onCallClick(shop: Any)

    fun onOrderClick(shop: Any)

    fun onLocationClick(shop: Any)

    fun onQuationClick(shop: Any)

    fun onReturnClick(position: Int)

    fun onHistoryClick(shop: Any)

    fun onDamageClick(shop_id: String)

    fun onSurveyClick(shop_id: String)


}