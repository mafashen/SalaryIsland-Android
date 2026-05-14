package com.salaryisland.app.domain

import java.util.Calendar

data class SalarySettings(
    val monthlySalary: Double,
    val workStartHour: Int,
    val workHoursPerDay: Int,
    val workDaysPerMonth: Int
) {
    val hourlyRate: Double
        get() = if (monthlySalary > 0 && workDaysPerMonth > 0 && workHoursPerDay > 0)
            monthlySalary / (workDaysPerMonth * workHoursPerDay) else 0.0
}

data class SalaryResult(
    val earnedToday: Double,
    val workedHours: Double,
    val hourlyRate: Double
)

object SalaryCalculator {
    fun calculate(settings: SalarySettings): SalaryResult {
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        val currentSecond = now.get(Calendar.SECOND)

        val workEndHour = settings.workStartHour + settings.workHoursPerDay

        val hoursWorked = when {
            currentHour >= workEndHour -> settings.workHoursPerDay.toDouble()
            currentHour >= settings.workStartHour -> {
                var h = (currentHour - settings.workStartHour).toDouble()
                h += currentMinute / 60.0
                h += currentSecond / 3600.0
                minOf(h, settings.workHoursPerDay.toDouble())
            }
            else -> 0.0
        }

        return SalaryResult(
            earnedToday = hoursWorked * settings.hourlyRate,
            workedHours = hoursWorked,
            hourlyRate = settings.hourlyRate
        )
    }
}
