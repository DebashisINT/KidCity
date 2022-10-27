package com.kcteam.features.viewPPDDStock

/**
 * Created by Pratishruti on 30-10-2017.
 */
interface ViewPPDDListClickListener {
    fun OnNearByShopsListClick(position:Int)
    fun mapClick(position:Int)
    fun orderClick(position:Int)
    fun callClick(position:Int)
    fun syncClick(position:Int)
    fun updateLocClick(position: Int)
    fun viewStock(position: Int)
}