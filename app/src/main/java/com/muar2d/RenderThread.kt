package com.muar2d

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.SurfaceHolder

class RenderThread(private val errorHandler: ErrorHandler) : Thread() {
    private var canvas: Canvas? = null
    private var renderTarget: SurfaceHolder? = null
    private var isRunning = false

    private var previousFrameTime: Long = 0
    private var paint = Paint()
    private var animationSpeed = 0
    private var isTouchingScreen:Boolean = false

    private val pathList = mutableListOf<Path>()
    private val fingerTrack = FingerTrack()


    override fun run() {
        super.run()

        previousFrameTime = System.nanoTime()

        while (isRunning) {
            canvas = null
            val deltaTime = (System.nanoTime() - previousFrameTime)
            try {
                canvas = renderTarget?.lockCanvas()
                canvas?.let {
                    synchronized(renderTarget!!){
                        render(canvas = it, time = deltaTime)
                        shiftPoints(deltaTime = deltaTime)
                    }

                    previousFrameTime = System.nanoTime()
                }
            } catch (e: Exception) {
                errorHandler.sendMsg(e.message.toString())
            }
            finally {
                renderTarget?.unlockCanvasAndPost(canvas)
            }
        }
    }

    fun setScreenTouchingFlag(flag:Boolean){
        this.isTouchingScreen = flag
    }

    fun setHolder(newRenderHolder: SurfaceHolder) {
        renderTarget = newRenderHolder
        errorHandler.sendMsg("HolderCreated")
    }

    fun setRunning(setUpIsRunning: Boolean) {
        isRunning = setUpIsRunning
    }

    fun setAnimationSpeed(speed: Int) {
        this.animationSpeed = speed
    }

    fun addFingerTouch(xPos: Float, yPos: Float) {
        if (isTouchingScreen){

        }
    }

    private fun shiftPoints(deltaTime: Long) {

    }

    private fun render(canvas: Canvas, time: Long) {
        val bitmap = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
        val bitmapCanvas = Canvas(bitmap)

        with(paint) {
            color = Color.DKGRAY
            textSize = 30.0f
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        bitmapCanvas.drawRect(0.0f, 0.0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)

        with(paint) {
            color = Color.RED
            textSize = 30.0f
            style = Paint.Style.FILL
        }

        bitmapCanvas.drawText("Hello speed $animationSpeed", 50.0f, 50.0f, paint)
        bitmapCanvas.drawText("Time $time", 50.0f, 80.0f, paint)

        for (path in pathList) {
            bitmapCanvas.drawPath(path, paint)
        }

        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null)
    }

    fun interface ErrorHandler{
        fun sendMsg(errorMsg:String)
    }
}