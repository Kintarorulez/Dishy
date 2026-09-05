package com.example.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface HomeConnectApi {

    @GET("/api/homeappliances")
    suspend fun getHomeAppliances(
        @Header("Authorization") authorization: String
    ): Response<HomeConnectHomeAppliancesResponse>

    @GET("/api/homeappliances/{haId}/status")
    suspend fun getStatus(
        @Header("Authorization") authorization: String,
        @Path("haId") haId: String
    ): Response<HomeConnectStatusResponse>

    @GET("/api/homeappliances/{haId}/programs/available")
    suspend fun getAvailablePrograms(
        @Header("Authorization") authorization: String,
        @Path("haId") haId: String
    ): Response<HomeConnectProgramsResponse>

    @GET("/api/homeappliances/{haId}/programs/active")
    suspend fun getActiveProgram(
        @Header("Authorization") authorization: String,
        @Path("haId") haId: String
    ): Response<HomeConnectActiveProgramResponse>

    @GET("/api/homeappliances/{haId}/settings")
    suspend fun getSettings(
        @Header("Authorization") authorization: String,
        @Path("haId") haId: String
    ): Response<HomeConnectSettingsResponse>

    @PUT("/api/homeappliances/{haId}/programs/active")
    suspend fun startProgram(
        @Header("Authorization") authorization: String,
        @Path("haId") haId: String,
        @Body request: HomeConnectProgramSelectionRequest
    ): Response<Unit>

    @DELETE("/api/homeappliances/{haId}/programs/active")
    suspend fun stopProgram(
        @Header("Authorization") authorization: String,
        @Path("haId") haId: String
    ): Response<Unit>
}
