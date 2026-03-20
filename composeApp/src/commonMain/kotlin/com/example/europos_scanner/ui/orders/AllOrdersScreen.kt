package com.example.europos_scanner.ui.orders

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.europos_scanner.data.model.OrderedItemResponse

private val STATUS_OPTIONS = listOf("", "NOT_USED", "USED")
private val STATUS_LABELS = listOf("Всички", "Неизползвани", "Използвани")
private val GRADE_OPTIONS = listOf("") + (1..12).map { it.toString() }
private val GRADE_LABELS = listOf("Всички") + (1..12).map { "$it Клас" }
private val CLASS_OPTIONS = listOf("", "А", "Б", "В", "Г", "Д", "Е", "Ж", "З")
private val CLASS_LABELS = listOf("Всички", "А", "Б", "В", "Г", "Д", "Е", "Ж", "З")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllOrdersScreen(
    viewModel: AllOrdersViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Всички поръчки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Филтри"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (showFilters) {
                FiltersSection(
                    state = state,
                    onIntent = viewModel::onIntent
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = "Общо: ${state.totalElements} поръчки",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                if (state.isLoading && state.orders.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    OrdersListContent(
                        orders = state.orders,
                        isLoadingMore = state.isLoading,
                        onLoadMore = { viewModel.onIntent(AllOrdersIntent.LoadMore) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun FiltersSection(
    state: AllOrdersState,
    onIntent: (AllOrdersIntent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                OutlinedTextField(
//                    value = state.filterDateFrom,
//                    onValueChange = { onIntent(AllOrdersIntent.UpdateDateFrom(it)) },
//                    label = { Text("От дата") },
//                    placeholder = { Text("2024-11-11") },
//                    singleLine = true,
//                    modifier = Modifier.weight(1f)
//                )
//                OutlinedTextField(
//                    value = state.filterDateTo,
//                    onValueChange = { onIntent(AllOrdersIntent.UpdateDateTo(it)) },
//                    label = { Text("До дата") },
//                    placeholder = { Text("2024-11-11") },
//                    singleLine = true,
//                    modifier = Modifier.weight(1f)
//                )
//            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterDropdown(
                    values = STATUS_OPTIONS,
                    labels = STATUS_LABELS,
                    selectedValue = state.filterStatus,
                    onValueSelected = { onIntent(AllOrdersIntent.UpdateStatus(it)) },
                    label = "Статус",
                    modifier = Modifier.weight(1f)
                )
                FilterDropdown(
                    values = GRADE_OPTIONS,
                    labels = GRADE_LABELS,
                    selectedValue = state.filterGrade,
                    onValueSelected = { onIntent(AllOrdersIntent.UpdateGrade(it)) },
                    label = "Клас",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = state.filterName,
                    onValueChange = { onIntent(AllOrdersIntent.UpdateName(it)) },
                    label = { Text("Име") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                FilterDropdown(
                    values = CLASS_OPTIONS,
                    labels = CLASS_LABELS,
                    selectedValue = state.filterClassName,
                    onValueSelected = { onIntent(AllOrdersIntent.UpdateClassName(it)) },
                    label = "Паралелка",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onIntent(AllOrdersIntent.ApplyFilters) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Приложи филтри")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    values: List<String>,
    labels: List<String>,
    selectedValue: String,
    onValueSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedIndex = values.indexOf(selectedValue).coerceAtLeast(0)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = labels[selectedIndex],
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            labels.forEachIndexed { index, labelText ->
                DropdownMenuItem(
                    text = { Text(labelText) },
                    onClick = {
                        onValueSelected(values[index])
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun OrdersListContent(
    orders: List<OrderedItemResponse>,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    val reachedEnd by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 3
        }
    }

    LaunchedEffect(reachedEnd) {
        if (reachedEnd) onLoadMore()
    }

    LazyColumn(state = listState, modifier = modifier) {
        items(orders, key = { it.orderedItemId }) { order ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.childName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row {
                        if (order.menuName != null) {
                            Text(
                                text = order.menuName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (order.forDate != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = order.forDate,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (order.childrenResponse != null) {
                        val info = buildString {
                            order.childrenResponse.grade?.let { append("${it} клас") }
                            order.childrenResponse.className?.let {
                                if (isNotEmpty()) append(" ")
                                append(it)
                            }
                        }
                        if (info.isNotEmpty()) {
                            Text(
                                text = info,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                val statusColor = when (order.status) {
                    "USED" -> MaterialTheme.colorScheme.primary
                    "NOT_USED" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                Text(
                    text = when (order.status) {
                        "USED" -> "Използвана"
                        "NOT_USED" -> "Неизползвана"
                        else -> order.status
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
            if (order != orders.last()) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
