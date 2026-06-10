package com.example.programacionmovil.ui.presentation.grades

import com.example.programacionmovil.domain.model.AcademicGrade

sealed class GradeUiState {
    object Loading : GradeUiState()

    data class Success(
        val grades: List<AcademicGrade>,
        val average: Double
    ) : GradeUiState()

    data class Error(
        val message: String,
        val previousGrades: List<AcademicGrade> = emptyList(),
        val previousAverage: Double = 0.0
    ) : GradeUiState()
}
