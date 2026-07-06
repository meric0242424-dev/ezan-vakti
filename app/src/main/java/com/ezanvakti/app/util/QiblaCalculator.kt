package com.ezanvakti.app.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

object QiblaCalculator {

    // Coordinates of the Kaaba, Mecca
    private const val KAABA_LAT = 21.4225
    private const val KAABA_LON = 39.8262

    private fun toRadians(deg: Double): Double = deg * PI / 180.0
    private fun toDegrees(rad: Double): Double = rad * 180.0 / PI

    /**
     * Returns the compass bearing (0-360, 0 = true north) from the given
     * location to the Kaaba, using the great-circle bearing formula.
     */
    fun bearingToQibla(lat: Double, lon: Double): Double {
        val lat1 = toRadians(lat)
        val lat2 = toRadians(KAABA_LAT)
        val deltaLon = toRadians(KAABA_LON - lon)

        val y = sin(deltaLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLon)
        var bearing = toDegrees(atan2(y, x))
        bearing = (bearing + 360) % 360
        return bearing
    }
}
