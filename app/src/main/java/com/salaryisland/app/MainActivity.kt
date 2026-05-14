package com.salaryisland.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import com.salaryisland.app.service.IslandService
import com.salaryisland.app.ui.SettingsScreen

class MainActivity : ComponentActivity() {

    private var isServiceRunning by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val app = application as SalaryIslandApp
                    SettingsScreen(
                        dataStore = app.settingsDataStore,
                        isServiceRunning = isServiceRunning,
                        onStartService = { startIslandService() },
                        onStopService = { stopIslandService() }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isServiceRunning = IslandService.isRunning
        checkPermissions()
    }

    private fun checkPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "请在设置中允许「显示悬浮窗」权限", Toast.LENGTH_LONG).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Toast.makeText(this, "请在设置中允许通知权限", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startIslandService() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "需要悬浮窗权限才能显示灵动岛", Toast.LENGTH_LONG).show()
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
            )
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Toast.makeText(this, "需要通知权限才能后台运行", Toast.LENGTH_LONG).show()
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
                return
            }
        }

        IslandService.start(this)
        isServiceRunning = true
        Toast.makeText(this, "灵动岛已启动", Toast.LENGTH_SHORT).show()
    }

    private fun stopIslandService() {
        IslandService.stop(this)
        isServiceRunning = false
        Toast.makeText(this, "灵动岛已停止", Toast.LENGTH_SHORT).show()
    }
}
