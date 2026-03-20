package bg.europos_scanner.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusResponse(
    val childrenResponse: ChildInfo,
    val status: String
) {
    val isUsed: Boolean get() = status == "USED"
}

@Serializable
data class ChildInfo(
    val id: Int,
    val name: String
)
