package com.salaryisland.app.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.view.ContextThemeWrapper
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.salaryisland.app.R
import com.salaryisland.app.domain.SalaryResult
import com.salaryisland.app.ui.IslandContent

class IslandOverlayManager(private val context: Context) {

    private var composeView: ComposeView? = null
    private var windowManager: WindowManager? = null
    var isExpanded by mutableStateOf(false)
        private set
    var currentResult: SalaryResult? by mutableStateOf(null)
        private set

    @SuppressLint("ClickableViewAccessibility")
    fun show() {
        if (composeView != null) return

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val density = context.resources.displayMetrics.density
        val themedContext = ContextThemeWrapper(context, R.style.Theme_SalaryIsland)

        composeView = ComposeView(themedContext).apply {
            setContent {
                MaterialTheme {
                    OverlayComposable(
                        result = currentResult,
                        isExpanded = isExpanded,
                        onToggleExpand = { toggleExpand() }
                    )
                }
            }

            params = WindowManager.LayoutParams(
                (250 * density).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                y = (50 * density).toInt()
            }
        }

        windowManager?.addView(composeView, params)

        composeView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_OUTSIDE -> {
                    if (isExpanded) toggleExpand()
                    true
                }
                else -> false
            }
        }
    }

    fun update(result: SalaryResult) {
        currentResult = result
    }

    fun toggleExpand() {
        isExpanded = !isExpanded
    }

    fun hide() {
        composeView?.let { windowManager?.removeView(it) }
        composeView = null
        windowManager = null
    }
}

@Composable
fun OverlayComposable(
    result: SalaryResult?,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(if (isExpanded) 280.dp else 160.dp)
            .clip(RoundedCornerShape(if (isExpanded) 24.dp else 100.dp))
            .background(Color(0xFF1C1C1E))
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onToggleExpand
            )
            .padding(12.dp)
    ) {
        IslandContent(
            result = result,
            isExpanded = isExpanded
        )
    }
}
