package com.example.programacionmovil.ui.presentation.grades

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.programacionmovil.domain.model.AcademicGrade
import java.util.Locale

@Composable
fun GradeApp(
    viewModel: GradeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val destination by viewModel.destination.collectAsState()
    val formState by viewModel.formState.collectAsState()

    when (destination) {
        GradeDestination.List -> GradeListScreen(
            uiState = uiState,
            onAddClick = viewModel::openForm,
            modifier = modifier
        )

        GradeDestination.Form -> GradeFormScreen(
            uiState = uiState,
            formState = formState,
            onActivityNameChange = viewModel::updateActivityName,
            onSubjectChange = viewModel::updateSubject,
            onScoreChange = viewModel::updateScore,
            onSaveClick = viewModel::saveGrade,
            onBackClick = viewModel::openList,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GradeListScreen(
    uiState: GradeUiState,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "GradeTracker")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        when (uiState) {
            GradeUiState.Loading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            is GradeUiState.Success -> GradeSuccessContent(
                grades = uiState.grades,
                average = uiState.average,
                contentPadding = innerPadding
            )

            is GradeUiState.Error -> GradeSuccessContent(
                grades = uiState.previousGrades,
                average = uiState.previousAverage,
                contentPadding = innerPadding,
                errorMessage = uiState.message
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GradeFormScreen(
    uiState: GradeUiState,
    formState: GradeFormState,
    onActivityNameChange: (String) -> Unit,
    onSubjectChange: (String) -> Unit,
    onScoreChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val errorMessage = (uiState as? GradeUiState.Error)?.message

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Nueva calificacion")
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(text = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (errorMessage != null) {
                ErrorMessageCard(message = errorMessage)
            }

            OutlinedTextField(
                value = formState.activityName,
                onValueChange = onActivityNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Actividad") },
                singleLine = true
            )

            OutlinedTextField(
                value = formState.subject,
                onValueChange = onSubjectChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Asignatura") },
                singleLine = true
            )

            OutlinedTextField(
                value = formState.scoreText,
                onValueChange = onScoreChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Nota 0.0 - 10.0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onBackClick) {
                    Text(text = "Cancelar")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onSaveClick,
                    enabled = formState.canSubmit
                ) {
                    Text(text = "Registrar")
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun GradeSuccessContent(
    grades: List<AcademicGrade>,
    average: Double,
    contentPadding: PaddingValues,
    errorMessage: String? = null
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            end = 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 96.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AverageCard(average = average)
        }

        if (errorMessage != null) {
            item {
                ErrorMessageCard(message = errorMessage)
            }
        }

        items(
            items = grades,
            key = { grade -> grade.id }
        ) { grade ->
            GradeItemCard(grade = grade)
        }
    }
}

@Composable
private fun AverageCard(average: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Promedio acumulado",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = average.twoDecimals(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun GradeItemCard(grade: AcademicGrade) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = grade.activityName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = grade.subject,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = grade.score.twoDecimals(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ErrorMessageCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

private fun Double.twoDecimals(): String = String.format(Locale.US, "%.2f", this)
