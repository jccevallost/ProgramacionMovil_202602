package com.example.programacionmovil.data.repository

import com.example.programacionmovil.domain.model.AcademicGrade
import com.example.programacionmovil.domain.repository.GradeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryGradeRepository : GradeRepository {
    private val grades = MutableStateFlow(
        listOf(
            AcademicGrade(
                id = "seed-architecture",
                activityName = "Laboratorio MVVM",
                subject = "Programacion Movil",
                score = 9.2
            ),
            AcademicGrade(
                id = "seed-functional",
                activityName = "Reto Null Safety",
                subject = "Kotlin Avanzado",
                score = 8.7
            )
        )
    )

    override fun observeGrades(): Flow<List<AcademicGrade>> = grades.asStateFlow()

    override suspend fun addGrade(grade: AcademicGrade) {
        grades.update { currentGrades -> currentGrades + grade }
    }
}
