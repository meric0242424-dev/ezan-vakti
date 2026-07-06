package com.ezanvakti.app.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.ezanvakti.app.data.model.LocationInfo
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationInfo? {
        if (!hasLocationPermission()) return null
        return suspendCancellableCoroutine { cont ->
            fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location == null) {
                        cont.resume(null)
                        return@addOnSuccessListener
                    }
                    val (city, district) = reverseGeocode(location.latitude, location.longitude)
                    cont.resume(
                        LocationInfo(
                            city = city,
                            district = district,
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
    }

    private fun reverseGeocode(lat: Double, lon: Double): Pair<String, String> {
        return try {
            val geocoder = Geocoder(context, Locale("tr", "TR"))
            @Suppress("DEPRECATION")
            val results = geocoder.getFromLocation(lat, lon, 1)
            val address = results?.firstOrNull()
            val city = address?.adminArea ?: "Konum"
            val district = address?.subAdminArea ?: address?.locality ?: ""
            city to district
        } catch (e: Exception) {
            "Konum" to ""
        }
    }
}
