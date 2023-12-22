package com.muar2d

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.sign
import kotlin.random.Random

class MainViewModel : ViewModel() {
    private val _errorMsg = MutableLiveData<String>()
    val errorMsg = _errorMsg as LiveData<String>

    private var screenHeight = 0.0f
    private var screenWidth = 0.0f

    private var isRendering = false

    private var paint = Paint()
    private var angle = 0.0

    private val vectorList = mutableListOf<Dx3Vector>()

    private fun initVectorList() {
        vectorList.clear()

        val vector = Dx3Vector().apply {
            start.x = screenWidth / 2
            start.y = screenHeight / 2
            end.x = screenHeight
            end.y = screenHeight / 2
        }

        for (angle in 0 until 360 step 2) {
            vectorList.add(vector.rotateVector(angle.toDouble()))
        }
    }

    fun setScreenDimensions(newHeight: Float, newWidth: Float) {
        this.screenHeight = newHeight
        this.screenWidth = newWidth

        initVectorList()
    }

    fun calculate() {
        angle += 0.0001

        for (vector in vectorList) {
            with(vector){
                end = vector.rotateVector(angle).end
            }
        }
    }

    fun drawOnHolder(canvas: Canvas) {
        with(paint) {
            color = Color.DKGRAY
            style = Paint.Style.FILL
        }
        canvas.drawRect(0.0f, 0.0f, screenWidth, screenHeight, paint)


        paint.color = Color.RED




        for (vector in vectorList) {
            canvas.drawPath(vector.createThickerPath(), paint)
        }
    }
}