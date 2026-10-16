package com.amix.tutor.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amix.tutor.R
import com.amix.tutor.ui.theme.AmixBg
import com.amix.tutor.ui.theme.AmixMuted

/** Экран-превью, как в веб-версии: голосового режима пока нет. Анимация орбиты будет перенесена позже. */
@Composable
fun VoiceScreen(onBack: () -> Unit) {
    ScreenColumn {
        PanelCard {
            Chip(stringResource(R.string.voice_badge))
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.voice_overline),
                color = AmixMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.voice_title), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.voice_description), color = AmixMuted, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.voice_status_idle), color = AmixMuted, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.voice_note), color = AmixMuted, fontSize = 12.sp)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AmixBg)
            ) {
                Text(stringResource(R.string.voice_back_chat), fontWeight = FontWeight.Bold)
            }
        }
    }
}
