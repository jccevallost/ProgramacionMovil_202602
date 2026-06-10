package com.example.programacionmovil.domain.usecase

import com.example.programacionmovil.domain.model.AcademicGrade
import com.example.programacionmovil.domain.repository.GradeRepository
import kotlinx.coroutines.flow.Flow

class GetGradesUseCase(
    private val repository: GradeRepository
) {
    operator fun invoke(): Flow<List<AcademicGrade>> = repository.observeGrades()
}
