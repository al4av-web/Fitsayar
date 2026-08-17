package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ExerciseType
import com.example.ui.theme.ImmersiveBackground
import com.example.ui.theme.ImmersiveGreen
import com.example.ui.theme.ImmersiveOnPrimary
import com.example.ui.theme.ImmersiveOutline
import com.example.ui.theme.ImmersivePrimary
import com.example.ui.theme.ImmersivePrimaryContainer
import com.example.ui.theme.ImmersiveSurface
import com.example.ui.theme.ImmersiveSurfaceVariant
import com.example.ui.theme.ImmersiveTextPrimary
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun ExerciseHelpDialog(
    selectedExercise: ExerciseType,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("exercise_help_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = ImmersivePrimaryContainer,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = ImmersivePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Nasıl Kullanılır?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ImmersiveTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Camera placement tips
                Text(
                    text = "Kamera Kurulum İpuçları",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ImmersivePrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                HelpTipItem(
                    icon = Icons.Default.Videocam,
                    title = "Mesafe ve Açı",
                    description = "Telefonunuzu yaklaşık 1.5 - 2.5 metre mesafeye koyun. Vücudunuzun tamamının (baş, gövde, kollar, bacaklar) kadrajda olmasına dikkat edin."
                )

                HelpTipItem(
                    icon = Icons.Default.WbSunny,
                    title = "Işıklandırma",
                    description = "Odanın iyi aydınlatılmış olduğundan ve arkanızdan gelen güçlü ışık (ters ışık) olmadığından emin olun."
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Exercise specific guide
                Text(
                    text = "${selectedExercise.title} Kuralları",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ImmersiveGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ImmersiveSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = selectedExercise.instruction,
                            fontSize = 13.sp,
                            color = ImmersiveTextPrimary,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Algılama Kriteri: ${selectedExercise.keyPointsDesc}",
                            fontSize = 12.sp,
                            color = ImmersivePrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImmersivePrimary,
                        contentColor = ImmersiveOnPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Anladım", fontWeight = FontWeight.Bold, color = ImmersiveOnPrimary)
                }
            }
        }
    }
}

@Composable
fun HelpTipItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ImmersivePrimary,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ImmersiveTextPrimary
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = ImmersiveTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
