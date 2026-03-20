package com.example.europos_scanner.data.repository

import com.example.europos_scanner.data.model.Student
import com.example.europos_scanner.data.remote.ApiService

class StudentRepository(private val apiService: ApiService) {

    suspend fun getStudents(grade: String?, className: String?): Result<List<Student>> {
        return try {
            val response = apiService.getStudents(grade, className)
            Result.success(response.children)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
