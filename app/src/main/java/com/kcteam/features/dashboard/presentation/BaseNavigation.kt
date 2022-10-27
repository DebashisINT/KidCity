package com.kcteam.features.dashboard.presentation

import com.kcteam.app.types.FragType

/**
 * Created by rp : 27-10-2017:18:06
 */
interface BaseNavigation {

    /*fragment transaction and navigation */
    abstract fun loadFragment(mFragType: FragType, addToStack: Boolean, initializeObject: Any)
}