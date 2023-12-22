package com.muar2d

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _errorMsg = MutableLiveData<String>()
    val errorMsg = _errorMsg as LiveData<String>

    private var screenHeight = 0.0f
    private var screenWidth = 0.0f

    private var isRendering = false

    private var renderThread: Thread = Thread()

    init {

    }

    fun setScreenDimensions(newHeight: Float, newWidth: Float) {
        this.screenHeight = newHeight
        this.screenWidth = newWidth
    }


}