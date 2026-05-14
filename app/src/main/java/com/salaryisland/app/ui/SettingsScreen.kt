package com.salaryisland.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salaryisland.app.data.SettingsDataStore
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    dataStore: SettingsDataStore,
    isServiceRunning: Boolean,
    onStartService: () -> Unit,
    onStopService: () -> Unit
) {
    val monthlySalary by dataStore.monthlySalary.collectAsState(initial = 0.0)
    val workStartHour by dataStore.workStartHour.collectAsState(initial = 9)
    val workHoursPerDay by dataStore.workHoursPerDay.collectAsState(initial = 8)
    val workDaysPerMonth by dataStore.workDaysPerMonth.collectAsState(initial = 22)
    val refreshInterval by dataStore.refreshInterval.collectAsState(initial = 10)

    val scope = rememberCoroutineScope()

    var salaryInput by remember(monthlySalary) {
        mutableStateOf(if (monthlySalary > 0) monthlySalary.toLong().toString() else "")
    }

    val hourlyRate = if (monthlySalary > 0 && workDaysPerMonth > 0 && workHoursPerDay > 0)
        monthlySalary / (workDaysPerMonth * workHoursPerDay) else 0.0

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "工资设置",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            // Monthly salary
            SettingSection(title = "月薪 (元)") {
                OutlinedTextField(
                    value = salaryInput,
                    onValueChange = { newValue ->
                        salaryInput = newValue
                        val value = newValue.toDoubleOrNull()
                        if (value != null && value > 0) {
                            scope.launch { dataStore.setMonthlySalary(value) }
                        }
                    },
                    placeholder = { Text("请输入月薪") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(8.dp))

            // Work start hour
            SettingSection(title = "上班时间") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            if (workStartHour > 0)
                                scope.launch { dataStore.setWorkStartHour(workStartHour - 1) }
                        },
                        modifier = Modifier.width(48.dp)
                    ) { Text("-", fontSize = 16.sp) }

                    Text(
                        text = "${workStartHour}点",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Button(
                        onClick = {
                            if (workStartHour < 23)
                                scope.launch { dataStore.setWorkStartHour(workStartHour + 1) }
                        },
                        modifier = Modifier.width(48.dp)
                    ) { Text("+", fontSize = 16.sp) }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Work hours per day
            SettingSection(title = "每日工作时长 (小时)") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            if (workHoursPerDay > 1)
                                scope.launch { dataStore.setWorkHoursPerDay(workHoursPerDay - 1) }
                        },
                        modifier = Modifier.width(48.dp)
                    ) { Text("-") }

                    Text(
                        text = "${workHoursPerDay}小时",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Button(
                        onClick = {
                            if (workHoursPerDay < 24)
                                scope.launch { dataStore.setWorkHoursPerDay(workHoursPerDay + 1) }
                        },
                        modifier = Modifier.width(48.dp)
                    ) { Text("+") }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Work days per month
            SettingSection(title = "每月工作天数") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            if (workDaysPerMonth > 1)
                                scope.launch { dataStore.setWorkDaysPerMonth(workDaysPerMonth - 1) }
                        },
                        modifier = Modifier.width(48.dp)
                    ) { Text("-") }

                    Text(
                        text = "${workDaysPerMonth}天",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Button(
                        onClick = {
                            if (workDaysPerMonth < 31)
                                scope.launch { dataStore.setWorkDaysPerMonth(workDaysPerMonth + 1) }
                        },
                        modifier = Modifier.width(48.dp)
                    ) { Text("+") }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Refresh interval
            SettingSection(title = "刷新频率") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            if (refreshInterval > 3)
                                scope.launch { dataStore.setRefreshInterval(refreshInterval - 1) }
                        },
                        modifier = Modifier.width(48.dp)
                    ) { Text("-") }

                    Text(
                        text = "${refreshInterval}秒",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Button(
                        onClick = {
                            if (refreshInterval < 120)
                                scope.launch { dataStore.setRefreshInterval(refreshInterval + 1) }
                        },
                        modifier = Modifier.width(48.dp)
                    ) { Text("+") }
                }
            }

            Spacer(Modifier.height(8.dp))

            Divider()

            Spacer(Modifier.height(8.dp))

            // Rate preview
            if (monthlySalary > 0) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "时薪: ¥${"%.2f".format(hourlyRate)}",
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "日薪: ¥${"%.2f".format(hourlyRate * workHoursPerDay)}",
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(12.dp))
            }

            // Start/Stop service button
            Button(
                onClick = {
                    if (isServiceRunning) onStopService() else onStartService()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isServiceRunning) Color(0xFFE53935) else Color(0xFF4CAF50)
                ),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(
                    text = if (isServiceRunning) "停止灵动岛" else "启动灵动岛",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SettingSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 4.dp)
        )
        content()
    }
}
