package bg.europos_scanner.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class OrderedItemStatus {
    PENDING_PAYMENT,
    NOT_USED,
    USED,
    REFUNDED,
    UNKNOWN
}

object OrderedItemStatusSerializer : KSerializer<OrderedItemStatus> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("OrderedItemStatus", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OrderedItemStatus {
        val raw = decoder.decodeString()
        return OrderedItemStatus.entries.find { it.name == raw } ?: OrderedItemStatus.UNKNOWN
    }

    override fun serialize(encoder: Encoder, value: OrderedItemStatus) {
        encoder.encodeString(value.name)
    }
}

@Serializable
data class GetOrdersResponse(
    val orders: List<OrderedItemResponse>,
    val pageMeta: PageMeta
)

@Serializable
data class OrderedItemResponse(
    val orderedItemId: Long,
    val orderedOn: String? = null,
    val forDate: String? = null,
    val statusUpdatedAt: String? = null,
    val menuName: String? = null,
    val menuId: Long? = null,
    @Serializable(with = OrderedItemStatusSerializer::class)
    val status: OrderedItemStatus,
    val childrenResponse: OrderChildrenResponse? = null,
) {
    val childName: String
        get() {
            val first = childrenResponse?.firstName ?: ""
            val last = childrenResponse?.lastName ?: ""
            return "$first $last".trim()
        }
}

@Serializable
data class OrderChildrenResponse(
    val id: Long,
    val firstName: String? = null,
    val lastName: String? = null,
    val grade: Int,
    val className: String
)

@Serializable
data class PageMeta(
    val currentPage: Int,
    val pages: Int,
    val totalElements: Long
)
