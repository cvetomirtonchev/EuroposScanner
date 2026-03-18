package com.example.europos_scanner.ui.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.europos_scanner.data.model.CityResponse
import com.example.europos_scanner.data.model.LinkedFacilitiesResponse
import com.example.europos_scanner.data.model.SchoolResponse
import com.example.europos_scanner.data.model.Student
import com.example.europos_scanner.data.model.UserDetailsResponse
import com.example.europos_scanner.scanner.CameraPreview
import com.example.europos_scanner.ui.components.GradeClassSelector
import com.example.europos_scanner.ui.components.ResultDialog
import com.example.europos_scanner.ui.components.StudentList
import com.example.europos_scanner.ui.theme.EuroposScannerTheme
import kotlinx.coroutines.launch

@Composable
fun ScannerScreen(viewModel: ScannerViewModel) {
    val state by viewModel.state.collectAsState()

    ScannerContent(
        state = state,
        onIntent = viewModel::onIntent,
        cameraSlot = {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onBarcodeScanned = { result ->
                    viewModel.onIntent(ScannerIntent.BarcodeScanned(result.value))
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerContent(
    state: ScannerState,
    onIntent: (ScannerIntent) -> Unit,
    cameraSlot: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                userDetails = state.userDetails,
                onLogout = {
                    scope.launch { drawerState.close() }
                    onIntent(ScannerIntent.Logout)
                }
            )
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text("Europos Scanner") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Меню"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Моля, сканирайте купон на дете",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        cameraSlot()
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Switch(
                            checked = state.isManualInput,
                            onCheckedChange = { onIntent(ScannerIntent.ToggleManualInput) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ръчно въвеждане",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    if (state.isManualInput) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = state.manualInputText,
                                onValueChange = { onIntent(ScannerIntent.UpdateManualInput(it)) },
                                label = { Text("Код на ученик") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onIntent(ScannerIntent.SubmitManualInput) },
                                enabled = state.manualInputText.isNotBlank() && !state.isProcessingScan
                            ) {
                                Text("Изпрати")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    GradeClassSelector(
                        selectedGrade = state.selectedGrade,
                        selectedSection = state.selectedSection,
                        onGradeSelected = { onIntent(ScannerIntent.SelectGrade(it)) },
                        onSectionSelected = { onIntent(ScannerIntent.SelectSection(it)) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        if (state.isLoadingStudents) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            StudentList(
                                students = state.students,
                                scannedIds = state.scannedIds,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                ResultDialog(
                    result = state.scanResult,
                    onDismiss = { onIntent(ScannerIntent.DismissResult) }
                )
            }
        }
    }
}

@Composable
private fun DrawerContent(
    userDetails: UserDetailsResponse?,
    onLogout: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = userDetails?.name ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            if (userDetails?.email != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userDetails.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        if (userDetails?.linkedFacilities != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = userDetails.linkedFacilities.school.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = userDetails.linkedFacilities.school.city.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        NavigationDrawerItem(
            label = { Text("Изход") },
            selected = false,
            onClick = onLogout,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
private fun CameraPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Camera Preview",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val sampleStudents = listOf(
    Student(id = 1, firstName = "Иван", lastName = "Петров", grade = "5", className = "А"),
    Student(id = 2, firstName = "Мария", lastName = "Иванова", grade = "5", className = "А"),
    Student(id = 3, firstName = "Георги", lastName = "Стоянов", grade = "5", className = "А"),
    Student(id = 4, firstName = "Елена", lastName = "Димитрова", grade = "5", className = "А"),
)

private val sampleUserDetails = UserDetailsResponse(
    id = 2,
    email = "cvetomirtonchev@gmail.com",
    username = "tsvetomir",
    name = "Tsvetomir",
    verified = true,
    linkedFacilities = LinkedFacilitiesResponse(
        id = 4,
        school = SchoolResponse(
            id = 4,
            name = "НУ \"Кирил и Методий\"",
            city = CityResponse(id = 5, name = "Долни Дъбник")
        )
    )
)

@Preview
@Composable
private fun ScannerContentPreview() {
    EuroposScannerTheme {
        ScannerContent(
            state = ScannerState(
                students = sampleStudents,
                scannedIds = setOf(1, 3),
                userDetails = sampleUserDetails
            ),
            onIntent = {},
            cameraSlot = { CameraPlaceholder() }
        )
    }
}

@Preview
@Composable
private fun ScannerContentManualInputPreview() {
    EuroposScannerTheme {
        ScannerContent(
            state = ScannerState(
                students = sampleStudents,
                scannedIds = setOf(1),
                isManualInput = true,
                manualInputText = "12345",
                userDetails = sampleUserDetails
            ),
            onIntent = {},
            cameraSlot = { CameraPlaceholder() }
        )
    }
}
