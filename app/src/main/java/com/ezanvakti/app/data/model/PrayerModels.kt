package com.ezanvakti.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Aladhan API response wrapper: /timings and /calendar endpoints
 */
data class AladhanResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: Any?
)

data class AladhanTimingsResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: TimingsData
)

data class AladhanCalendarResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<TimingsData>
)

data class TimingsData(
    @SerializedName("timings") val timings: Timings,
    @SerializedName("date") val date: DateInfo
)

data class Timings(
    @SerializedName("Fajr") val fajr: String,
    @SerializedName("Sunrise") val sunrise: String,
    @SerializedName("Dhuhr") val dhuhr: String,
    @SerializedName("Asr") val asr: String,
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String
)

data class DateInfo(
    @SerializedName("readable") val readable: String,
    @SerializedName("gregorian") val gregorian: CalendarDate,
    @SerializedName("hijri") val hijri: CalendarDate
)

data class CalendarDate(
    @SerializedName("date") val date: String,
    @SerializedName("day") val day: String,
    @SerializedName("month") val month: MonthInfo,
    @SerializedName("year") val year: String
)

data class MonthInfo(
    @SerializedName("number") val number: Int,
    @SerializedName("en") val en: String,
    @SerializedName("ar") val ar: String? = null
)

/**
 * Simplified, UI-friendly model used across the app once parsed from the API
 */
data class PrayerDay(
    val dateReadable: String,
    val hijriDay: String,
    val hijriMonth: String,
    val hijriMonthNumber: Int,
    val hijriYear: String,
    val gregorianDate: String, // dd-MM-yyyy
    val imsak: String,
    val gunes: String,
    val ogle: String,
    val ikindi: String,
    val aksam: String,
    val yatsi: String
) {
    fun asOrderedList(): List<Pair<String, String>> = listOf(
        "İmsak" to imsak,
        "Güneş" to gunes,
        "Öğle" to ogle,
        "İkindi" to ikindi,
        "Akşam" to aksam,
        "Yatsı" to yatsi
    )
}

data class LocationInfo(
    val city: String,
    val district: String,
    val latitude: Double,
    val longitude: Double
)
