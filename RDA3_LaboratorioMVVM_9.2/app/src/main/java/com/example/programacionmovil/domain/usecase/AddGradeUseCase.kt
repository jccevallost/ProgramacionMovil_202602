package com.example.programacionmovil.domain.usecase

import com.example.programacionmovil.domain.model.AcademicGrade
import com.example.programacionmovil.domain.repository.GradeRepository
import java.util.UUID

class AddGradeUseCase(
    private val repository: GradeRepository
) {
    suspend operator fun invoke(
        activityName: String,
        subject: String,
        score: Double
    ) {
        val cleanActivityName = activityName.trim()
        val cleanSubject = subject.trim()

        require(cleanActivityName.isNotEmpty()) {
            "El nombre de la actividad no puede estar vacio."
        }
        require(cleanSubject.isNotEmpty()) {
            "La asignatura no puede estar vacia."
        }
        require(score in MIN_SCORE..MAX_SCORE) {
            "La nota debe estar entre 0.0 y 10.0."
        }

        repository.addGrade(
            AcademicGrade(
                id = UUID.randomUUID().toString(),
                activityName = cleanActivityName,
                subject = cleanSubject,
                score = score
            )
        )
    }

    private companion object {
        const val MIN_SCORE = 0.0
        const val MAX_SCORE = 10.0
    }
}
