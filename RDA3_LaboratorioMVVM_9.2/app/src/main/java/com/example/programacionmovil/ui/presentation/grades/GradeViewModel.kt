package com.example.programacionmovil.ui.presentation.grades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.programacionmovil.domain.model.AcademicGrade
import com.example.programacionmovil.domain.usecase.AddGradeUseCase
import com.example.programacionmovil.domain.usecase.GetGradesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GradeViewModel(
    private val getGradesUseCase: GetGradesUseCase,
    private val addGradeUseCase: AddGradeUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<GradeUiState>(GradeUiState.Loading)
    val uiState: StateFlow<GradeUiState> = _uiState.asStateFlow()

    private val _destination = MutableStateFlow(GradeDestination.List)
    val destination: StateFlow<GradeDestination> = _destination.asStateFlow()

    private val _formState = MutableStateFlow(GradeFormState())
    val formState: StateFlow<GradeFormState> = _formState.asStateFlow()

    private var latestGrades: List<AcademicGrade> = emptyList()

    init {
        viewModelScope.launch {
            getGradesUseCase()
                .catch { throwable ->
                    emitError(throwable.message ?: "No se pudieron cargar las calificaciones.")
                }
                .collect { grades ->
                    latestGrades = grades
                    _uiState.value = GradeUiState.Success(
                        grades = grades,
                        average = grades.averageScore()
                    )
                }
        }
    }

    fun openForm() {
        _destination.value = GradeDestination.Form
        restoreLatestSuccess()
    }

    fun openList() {
        _destination.value = GradeDestination.List
        restoreLatestSuccess()
    }

    fun updateActivityName(value: String) {
        _formState.update { current -> current.copy(activityName = value) }
        restoreLatestSuccess()
    }

    fun updateSubject(value: String) {
        _formState.update { current -> current.copy(subject = value) }
        restoreLatestSuccess()
    }

    fun updateScore(value: String) {
        _formState.update { current -> current.copy(scoreText = value) }
        restoreLatestSuccess()
    }

    fun saveGrade() {
        val currentForm = _formState.value
        val score = currentForm.scoreText.toDoubleOrNull()

        if (score == null) {
            emitError("La nota debe ser un numero decimal valido.")
            return
        }

        viewModelScope.launch {
            try {
                addGradeUseCase(
                    activityName = currentForm.activityName,
                    subject = currentForm.subject,
                    score = score
                )
                _formState.value = GradeFormState()
                _destination.value = GradeDestination.List
            } catch (exception: IllegalArgumentException) {
                emitError(exception.message ?: "Los datos ingresados no son validos.")
            }
        }
    }

    private fun emitError(message: String) {
        _uiState.value = GradeUiState.Error(
            message = message,
            previousGrades = latestGrades,
            previousAverage = latestGrades.averageScore()
        )
    }

    private fun restoreLatestSuccess() {
        if (_uiState.value is GradeUiState.Error) {
            _uiState.value = GradeUiState.Success(
                grades = latestGrades,
                average = latestGrades.averageScore()
            )
        }
    }

    private fun List<AcademicGrade>.averageScore(): Double {
        if (isEmpty()) return 0.0
        return map { grade -> grade.score }.average()
    }

    companion object {
        fun factory(
            getGradesUseCase: GetGradesUseCase,
            addGradeUseCase: AddGradeUseCase
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(GradeViewModel::class.java)) {
                    return GradeViewModel(getGradesUseCase, addGradeUseCase) as T
                }
                throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
            }
        }
    }
}
