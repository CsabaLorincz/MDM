package com.mdm.app.extension

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.forEach
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.mdm.app.R

public fun View.setLayoutWaiting(value: Boolean){
    var forProgress=View.INVISIBLE
    var forElse=View.VISIBLE
    if(value){
        forProgress=View.VISIBLE
        forElse=View.INVISIBLE

        this.hideKeyboard()
    }
    var viewGroup=this as ViewGroup
    if(this is NestedScrollView)
        viewGroup=this.findViewById(R.id.ScrollingConstraint)
    viewGroup.forEach {
        if(it is ProgressBar){
            it.visibility=forProgress
        }
        else
            it.visibility=forElse
    }



}