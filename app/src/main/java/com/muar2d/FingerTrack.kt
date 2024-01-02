package com.muar2d

class FingerTrack {
    private val points = ArrayList<Dx3Point>()

    fun start(startPoint:Dx3Point){
        points.add(startPoint)
    }

    fun add(newPoint: Dx3Point){
        if(points.last() != newPoint){
            points.add(newPoint)
        }
    }

    fun end(endPoint: Dx3Point){
        points.add(endPoint)
    }



}