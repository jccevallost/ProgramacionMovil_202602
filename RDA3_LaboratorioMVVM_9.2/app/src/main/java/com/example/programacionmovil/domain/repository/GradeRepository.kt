package com.example.programacionmovil.domain.repository

import com.example.programacionmovil.domain.model.AcademicGrade
import kotlinx.coroutines.flow.Flow

interface GradeRepository {
    fun observeGrades(): Flow<List<AcademicGrade>>

    suspend fun addGrade(grade: AcademicGrade)
}
