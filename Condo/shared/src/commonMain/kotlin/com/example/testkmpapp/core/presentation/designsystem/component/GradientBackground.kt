package com.example.testkmpapp.core.presentation.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.testkmpapp.theme.CondoTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    hasToolbar: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val density = LocalDensity.current
    val screenWidthPx = with(density) {
        50.dp.roundToPx()
    }

    val smallDimension = minOf(
        UInt.MIN_VALUE,
        UInt.MIN_VALUE
    )

    val smallDimensionPx = with(density) {
        smallDimension.toFloat()
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    //val isAtLeastAndroid12 = Build.VERSION.SDK_INT <= Build.VERSION_CODES.S

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                // Not available in common
                /*.then(
                    if (isAtLeastAndroid12) {
                        Modifier.blur((smallDimension / 3f))
                    } else {
                        Modifier
                    }
                )$*/
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor,
                            MaterialTheme.colorScheme.background
                        ),
                        center = Offset(x = screenWidthPx / 2f, y = -100f),
                        radius = smallDimensionPx / 2f
                    )
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (hasToolbar) Modifier else Modifier.systemBarsPadding()) // push everything under this padding
        ) {
            content()
        }
    }

}

@Preview
@Composable
private fun GradientBackgroundPreview() {
    CondoTheme {
        GradientBackground(modifier = Modifier.fillMaxSize()) {

        }
    }
}