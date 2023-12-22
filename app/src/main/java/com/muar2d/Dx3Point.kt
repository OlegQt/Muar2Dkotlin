package com.muar2d

import android.graphics.PointF

class Dx3Point(x: Float, y: Float, var z: Float) : PointF(x, y) {
    constructor(x: Float, y: Float) : this(x, y, 0.0f)
    constructor():this(0.0f,0.0f,0.0f)

    fun setPosition(newPos:Dx3Point){
        x = newPos.x
        y=newPos.y
        z=newPos.z
    }

    fun toPointF() = PointF(x,y)
}