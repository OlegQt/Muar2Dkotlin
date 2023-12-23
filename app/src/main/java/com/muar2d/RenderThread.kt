package com.muar2d

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.SurfaceHolder

class RenderThread : Thread() {
    private var canvas: Canvas? = null
    private var renderTarget: SurfaceHolder? = null
    private var isRunning = false

    private var previousFrameTime: Long = 0

    private var paint = Paint()

    private var animationSpeed = 0

    private val pathList = mutableListOf<Path>()


    override fun run() {
        super.run()

        previousFrameTime = System.nanoTime()

        while (isRunning) {
            canvas = null
            val deltaTime = (System.nanoTime() - previousFrameTime)
            try {
                canvas = renderTarget?.lockCanvas()
                canvas?.let {
                    render(canvas = it, time = deltaTime)
                    shiftPoints(deltaTime = deltaTime)
                    renderTarget?.unlockCanvasAndPost(it)
                    previousFrameTime = System.nanoTime()
                }
            } catch (e: Exception) {
            }
        }
    }

    fun setHolder(newRenderHolder: SurfaceHolder) {
        renderTarget = newRenderHolder
    }

    fun setRunning(setUpIsRunning: Boolean) {
        isRunning = setUpIsRunning
    }

    fun setAnimationSpeed(speed: Int) {
        this.animationSpeed = speed
    }

    fun addFingerTouch(xPos:Float,yPos:Float){
        val path =Path().apply{
            addCircle(xPos,yPos,20.0f,Path.Direction.CCW)
            close()
        }
        this.pathList.add(path)
    }

    fun shiftPoints(deltaTime: Long) {
        val speed = 100 // Adjust the speed as desired
        val displacement = (speed * deltaTime / 1_000_000).toFloat() // Convert nanoseconds to seconds and calculate displacement

        for (path in pathList) {
            path.offset(0f, 0.1f*animationSpeed)
        }
    }

    private fun render(canvas: Canvas, time: Long) {
        with(paint) {
            color = Color.DKGRAY
            textSize = 30.0f
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        canvas.drawRect(0.0f, 0.0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)

        with(paint) {
            color = Color.RED
            textSize = 30.0f
            style = Paint.Style.FILL
        }

        canvas.drawText("Hello speed $animationSpeed", 50.0f, 50.0f, paint)
        canvas.drawText("Time $time", 50.0f, 80.0f, paint)


        for (path in pathList) {
            canvas.drawPath(path, paint)
        }
    }
}