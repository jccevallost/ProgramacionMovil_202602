package com.example.programacionmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.programacionmovil.data.repository.InMemoryGradeRepository
import com.example.programacionmovil.domain.usecase.AddGradeUseCase
import com.example.programacionmovil.domain.usecase.GetGradesUseCase
import com.example.programacionmovil.ui.presentation.grades.GradeApp
import com.example.programacionmovil.ui.presentation.grades.GradeViewModel
import com.example.programacionmovil.ui.theme.ProgramacionMovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gradeRepository = InMemoryGradeRepository()
        val getGradesUseCase = GetGradesUseCase(gradeRepository)
        val addGradeUseCase = AddGradeUseCase(gradeRepository)
        val gradeViewModel = ViewModelProvider(
            this,
            GradeViewModel.factory(getGradesUseCase, addGradeUseCase)
        )[GradeViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            ProgramacionMovilTheme {
                GradeApp(viewModel = gradeViewModel)
            }
        }
    }
}
