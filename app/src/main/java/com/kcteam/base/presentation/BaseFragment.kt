package com.kcteam.base.presentation

import android.os.Bundle
//import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment

/**
 * Created by Pratishruti on 27-10-2017.
 */
open class BaseFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    open fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {}

    open fun updateUI(any:Any){}
}