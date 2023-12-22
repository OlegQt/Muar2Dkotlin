package com.muar2d

import android.graphics.Path
import android.graphics.PointF
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Dx3Vector {
    var start = Dx3Point()
    var end = Dx3Point()

    fun rotateVector(alpha: Double): Dx3Vector {

        val dx = end.x - start.x
        val dy = end.y - start.y

        val alphaRadians = Math.toRadians(alpha)
        val cosAlpha = cos(alphaRadians)
        val sinAlpha = sin(alphaRadians)

        val newX = start.x + dx * cosAlpha - dy * sinAlpha
        val newY = start.y + dx * sinAlpha + dy * cosAlpha

        val newVectorEnd = Dx3Point(newX.toFloat(),newY.toFloat())
        val newVectorStart = Dx3Point(start.x,start.y)

        return Dx3Vector().apply {
            end = newVectorEnd
            start = newVectorStart
        }
    }

    fun createThickerPath(): Path {
        val path = Path()

        val dx = end.x - start.x
        val dy = end.y - start.y

        // Вычисляем вектор нормали к вектору
        val normal = PointF(-dy, dx)
        val length = sqrt(normal.x * normal.x + normal.y * normal.y)

        // Масштабируем вектор нормали на 2.0 пикселя
        val scaledNormal = PointF(normal.x / length * 2.0f, normal.y / length * 2.0f)

        // Создаем точки для пути
        val point1 = PointF(start.x + scaledNormal.x, start.y + scaledNormal.y)
        val point2 = PointF(end.x + scaledNormal.x, end.y + scaledNormal.y)
        val point3 = PointF(end.x - scaledNormal.x, end.y - scaledNormal.y)
        val point4 = PointF(start.x - scaledNormal.x, start.y - scaledNormal.y)

        // Добавляем точки в путь
        path.moveTo(point1.x, point1.y)
        path.lineTo(point2.x, point2.y)
        path.lineTo(point3.x, point3.y)
        path.lineTo(point4.x, point4.y)
        path.close()

        return path
    }
}