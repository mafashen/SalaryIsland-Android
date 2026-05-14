package com.salaryisland.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.salaryisland.app.MainActivity
import com.salaryisland.app.R
import com.salaryisland.app.SalaryIslandApp
import com.salaryisland.app.domain.SalaryCalculator
import com.salaryisland.app.domain.SalaryResult
import com.salaryisland.app.domain.SalarySettings
import com.salaryisland.app.overlay.IslandOverlayManager
import com.salaryisland.app.ui.formatCurrency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class IslandService : android.app.Service() {

    companion object {
        const val CHANNEL_ID = "salary_island_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_STOP = "com.salaryisland.app.action.STOP"

        @Volatile
        var isRunning = false
            private set

        fun start(context: Context) {
            val intent = Intent(context, IslandService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            isRunning = true
        }

        fun stop(context: Context) {
            val intent = Intent(context, IslandService::class.java).apply { action = ACTION_STOP }
            context.startService(intent)
        }
    }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var overlayManager: IslandOverlayManager
    private var currentResult: SalaryResult? = null
    private var lastNotificationUpdate = 0L

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        overlayManager = IslandOverlayManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            isRunning = false
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, createNotification())
        overlayManager.show()
        startRefreshLoop()
        isRunning = true

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        isRunning = false
        scope.cancel()
        overlayManager.hide()
        super.onDestroy()
    }

    private fun startRefreshLoop() {
        scope.launch {
            while (isActive) {
                val app = applicationContext as SalaryIslandApp
                val settings = app.settingsDataStore.snapshot()

                if (settings.monthlySalary > 0) {
                    val salarySettings = SalarySettings(
                        monthlySalary = settings.monthlySalary,
                        workStartHour = settings.workStartHour,
                        workHoursPerDay = settings.workHoursPerDay,
                        workDaysPerMonth = settings.workDaysPerMonth
                    )
                    currentResult = SalaryCalculator.calculate(salarySettings)
                    currentResult?.let { result ->
                        overlayManager.update(result)
                        updateNotification(result)
                    }
                }

                val interval = settings.refreshInterval.coerceIn(3, 120).toLong()
                delay(interval * 1000L)
            }
        }
    }

    private fun updateNotification(result: SalaryResult) {
        if (System.currentTimeMillis() - lastNotificationUpdate < 5000) return
        lastNotificationUpdate = System.currentTimeMillis()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("工资岛运行中")
            .setContentText("今日收入: ${formatCurrency(result.earnedToday)}")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setContentIntent(PendingIntent.getActivity(
                this, 0, Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            ))
            .addAction(R.drawable.ic_notification, "停止",
                PendingIntent.getService(
                    this, 1,
                    Intent(this, IslandService::class.java).apply { action = ACTION_STOP },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "工资岛通知", NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "显示工资计算运行状态"
                setShowBadge(false)
            }
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, IslandService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("工资岛运行中")
            .setContentText("正在计算...")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setContentIntent(PendingIntent.getActivity(
                this, 0, Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            ))
            .addAction(R.drawable.ic_notification, "停止", stopIntent)
            .build()
    }
}
