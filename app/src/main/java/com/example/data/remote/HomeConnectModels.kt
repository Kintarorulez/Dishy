package com.example.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeConnectHomeAppliancesResponse(
    val data: HomeAppliancesData?
)

@JsonClass(generateAdapter = true)
data class HomeAppliancesData(
    val homeappliances: List<HomeApplianceDto>?
)

@JsonClass(generateAdapter = true)
data class HomeApplianceDto(
    val haId: String,
    val name: String? = null,
    val brand: String? = null,
    val vib: String? = null,
    val enumber: String? = null,
    val type: String? = null,
    val connected: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class HomeConnectStatusResponse(
    val data: HomeConnectStatusData?
)

@JsonClass(generateAdapter = true)
data class HomeConnectStatusData(
    val status: List<HomeConnectStatusItem>?
)

@JsonClass(generateAdapter = true)
data class HomeConnectStatusItem(
    val key: String,
    val value: Any? = null,
    val unit: String? = null
)

@JsonClass(generateAdapter = true)
data class HomeConnectProgramsResponse(
    val data: HomeConnectProgramsData?
)

@JsonClass(generateAdapter = true)
data class HomeConnectProgramsData(
    val programs: List<HomeConnectProgramDto>?
)

@JsonClass(generateAdapter = true)
data class HomeConnectProgramDto(
    val key: String,
    val name: String? = null
)

@JsonClass(generateAdapter = true)
data class HomeConnectProgramSelectionRequest(
    val data: ProgramSelectionData
)

@JsonClass(generateAdapter = true)
data class ProgramSelectionData(
    val key: String,
    val options: List<ProgramOptionItem>? = null
)

@JsonClass(generateAdapter = true)
data class ProgramOptionItem(
    val key: String,
    val value: Any
)

@JsonClass(generateAdapter = true)
data class HomeConnectActiveProgramResponse(
    val data: ActiveProgramData?
)

@JsonClass(generateAdapter = true)
data class ActiveProgramData(
    val key: String,
    val options: List<ProgramOptionResponseItem>? = null
)

@JsonClass(generateAdapter = true)
data class ProgramOptionResponseItem(
    val key: String,
    val value: Any? = null,
    val unit: String? = null
)

@JsonClass(generateAdapter = true)
data class HomeConnectSettingsResponse(
    val data: HomeConnectSettingsData?
)

@JsonClass(generateAdapter = true)
data class HomeConnectSettingsData(
    val settings: List<HomeConnectStatusItem>?
)
