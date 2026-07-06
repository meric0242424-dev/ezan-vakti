package com.ezanvakti.app.data.remote

import com.ezanvakti.app.data.model.AladhanCalendarResponse
import com.ezanvakti.app.data.model.AladhanTimingsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Aladhan API (https://aladhan.com/prayer-times-api) - free, no API key required.
 * method=13 corresponds to the "Diyanet İşleri Başkanlığı, Türkiye" calculation method.
 */
interface AladhanApi {

    @GET("v1/timings/{date}")
    suspend fun getTimingsByDate(
        @Path("date") date: String, // dd-MM-yyyy
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 13
    ): AladhanTimingsResponse

    @GET("v1/calendar/{year}/{month}")
    suspend fun getMonthlyCalendar(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 13
    ): AladhanCalendarResponse
}
