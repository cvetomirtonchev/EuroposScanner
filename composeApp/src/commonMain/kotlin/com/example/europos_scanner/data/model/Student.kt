package com.example.europos_scanner.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val grade: Int,
    val className: String
) {
    val fullName: String get() = "$firstName $lastName"
}

@Serializable
data class StudentListResponse(
    val children: List<Student>
)
