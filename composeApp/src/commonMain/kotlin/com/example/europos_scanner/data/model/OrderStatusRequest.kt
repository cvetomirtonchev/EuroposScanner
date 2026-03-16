package com.example.europos_scanner.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusRequest(
    val childrenId: Int
)
