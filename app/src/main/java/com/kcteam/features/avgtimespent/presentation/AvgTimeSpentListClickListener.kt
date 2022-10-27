package com.kcteam.features.avgtimespent.presentation

import android.view.View

/**
 * Created by Pratishruti on 30-10-2017.
 */
interface AvgTimeSpentListClickListener {
    fun OnImageClick(position:Int)
    fun OnTimeSpentListClick(position:Int)
    fun menuClick(position:Int,view: View)
    fun onSyncClick(position:Int)
}