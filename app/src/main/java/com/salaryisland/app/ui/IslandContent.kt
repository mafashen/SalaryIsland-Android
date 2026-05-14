package com.salaryisland.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salaryisland.app.R
import com.salaryisland.app.domain.SalaryResult
import java.text.NumberFormat
import java.util.Locale

@Composable
fun IslandContent(
    result: SalaryResult?,
    isExpanded: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title row always visible
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_notification),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF4CAF50)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "今日收入",
                fontSize = 12.sp,
                color = Color(0xFF8E8E93)
            )
        }

        Spacer(Modifier.height(4.dp))

        if (result == null || result.hourlyRate <= 0.0) {
            Text(
                text = "未设置月薪",
                fontSize = 12.sp,
                color = Color(0xFF8E8E93)
            )
        } else if (isExpanded) {
            ExpandedContent(result)
        } else {
            val earnedText = formatCurrency(result.earnedToday)
            Text(
                text = earnedText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
private fun ExpandedContent(result: SalaryResult) {
    val earnedText = formatCurrency(result.earnedToday)

    AnimatedContent(
        targetState = earnedText,
        transitionSpec = {
            ContentTransform(
                fadeIn(animationSpec = tween(400)),
                fadeOut(animationSpec = tween(400))
            ).togetherWith(fadeIn())
        },
        label = "earned_anim"
    ) { text ->
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )
    }

    Spacer(Modifier.height(4.dp))

    CoinRainAnimation()

    Spacer(Modifier.height(4.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        InfoItem("已工作", formatHours(result.workedHours))
        InfoItem("时薪", formatCurrency(result.hourlyRate))
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Color(0xFF8E8E93))
        Text(text = value, fontSize = 10.sp, color = Color(0xFFE5E5EA))
    }
}

fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.CHINA)
    formatter.maximumFractionDigits = 2
    return formatter.format(amount)
}

fun formatHours(hours: Double): String {
    val h = hours.toInt()
    val m = ((hours - h) * 60).toInt()
    return "${h}小时${m}分钟"
}
