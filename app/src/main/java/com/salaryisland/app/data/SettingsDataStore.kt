package com.salaryisland.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "salary_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        private val KEY_MONTHLY_SALARY = doublePreferencesKey("monthly_salary")
        private val KEY_WORK_START_HOUR = intPreferencesKey("work_start_hour")
        private val KEY_WORK_HOURS_PER_DAY = intPreferencesKey("work_hours_per_day")
        private val KEY_WORK_DAYS_PER_MONTH = intPreferencesKey("work_days_per_month")
        private val KEY_REFRESH_INTERVAL = intPreferencesKey("refresh_interval")
    }

    val monthlySalary: Flow<Double> = context.dataStore.data.map { it[KEY_MONTHLY_SALARY] ?: 0.0 }
    val workStartHour: Flow<Int> = context.dataStore.data.map { it[KEY_WORK_START_HOUR] ?: 9 }
    val workHoursPerDay: Flow<Int> = context.dataStore.data.map { it[KEY_WORK_HOURS_PER_DAY] ?: 8 }
    val workDaysPerMonth: Flow<Int> = context.dataStore.data.map { it[KEY_WORK_DAYS_PER_MONTH] ?: 22 }
    val refreshInterval: Flow<Int> = context.dataStore.data.map { it[KEY_REFRESH_INTERVAL] ?: 10 }

    suspend fun getMonthlySalary(): Double = context.dataStore.data.first()[KEY_MONTHLY_SALARY] ?: 0.0
    suspend fun getWorkStartHour(): Int = context.dataStore.data.first()[KEY_WORK_START_HOUR] ?: 9
    suspend fun getWorkHoursPerDay(): Int = context.dataStore.data.first()[KEY_WORK_HOURS_PER_DAY] ?: 8
    suspend fun getWorkDaysPerMonth(): Int = context.dataStore.data.first()[KEY_WORK_DAYS_PER_MONTH] ?: 22
    suspend fun getRefreshInterval(): Int = context.dataStore.data.first()[KEY_REFRESH_INTERVAL] ?: 10

    suspend fun setMonthlySalary(value: Double) {
        context.dataStore.edit { it[KEY_MONTHLY_SALARY] = value }
    }

    suspend fun setWorkStartHour(value: Int) {
        context.dataStore.edit { it[KEY_WORK_START_HOUR] = value }
    }

    suspend fun setWorkHoursPerDay(value: Int) {
        context.dataStore.edit { it[KEY_WORK_HOURS_PER_DAY] = value }
    }

    suspend fun setWorkDaysPerMonth(value: Int) {
        context.dataStore.edit { it[KEY_WORK_DAYS_PER_MONTH] = value }
    }

    suspend fun setRefreshInterval(value: Int) {
        context.dataStore.edit { it[KEY_REFRESH_INTERVAL] = value }
    }

    data class SettingsSnapshot(
        val monthlySalary: Double,
        val workStartHour: Int,
        val workHoursPerDay: Int,
        val workDaysPerMonth: Int,
        val refreshInterval: Int
    )

    suspend fun snapshot(): SettingsSnapshot = SettingsSnapshot(
        monthlySalary = getMonthlySalary(),
        workStartHour = getWorkStartHour(),
        workHoursPerDay = getWorkHoursPerDay(),
        workDaysPerMonth = getWorkDaysPerMonth(),
        refreshInterval = getRefreshInterval()
    )
}
