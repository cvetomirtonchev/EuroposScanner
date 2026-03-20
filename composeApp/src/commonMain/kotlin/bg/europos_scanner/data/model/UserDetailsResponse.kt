package bg.europos_scanner.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDetailsResponse(
    val id: Long,
    val email: String,
    val username: String,
    val name: String,
    val verified: Boolean = false,
    val linkedFacilities: LinkedFacilitiesResponse? = null,
    val childrenList: List<ChildResponse> = emptyList()
)

@Serializable
data class LinkedFacilitiesResponse(
    val id: Long,
    val school: SchoolResponse
)

@Serializable
data class SchoolResponse(
    val id: Long,
    val name: String,
    val city: CityResponse
)

@Serializable
data class CityResponse(
    val id: Long,
    val name: String
)

@Serializable
data class ChildResponse(
    val id: Long,
    val firstName: String? = null,
    val lastName: String? = null
)
