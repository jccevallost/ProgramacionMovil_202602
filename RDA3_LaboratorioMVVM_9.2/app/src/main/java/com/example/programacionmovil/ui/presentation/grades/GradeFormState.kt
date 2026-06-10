package com.example.programacionmovil.ui.presentation.grades

data class GradeFormState(
    val activityName: String = "",
    val subject: String = "",
    val scoreText: String = ""
) {
    val canSubmit: Boolean
        get() = activityName.isNotBlank() && subject.isNotBlank() && scoreText.isNotBlank()
}
