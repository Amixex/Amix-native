package com.amix.tutor.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amix.tutor.R
import com.amix.tutor.ui.theme.AmixBg
import com.amix.tutor.ui.theme.AmixMuted

/** Заготовка экрана Pro. Цена, оплата, промокоды и дневной лимит подключаются на этапах «Промокоды» и «Оплата». */
@Composable
fun ProScreen() {
    ScreenColumn {
        PanelCard {
            Text(
                stringResource(R.string.pro_eyebrow),
                color = AmixMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.title_pro), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.pro_description), color = AmixMuted, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("200 ₽", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.pro_month), color = AmixMuted, fontSize = 14.sp)
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { /* оплата появится на этапе «Оплата» */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AmixBg)
            ) {
                Text(stringResource(R.string.buy_pro), fontWeight = FontWeight.Bold)
            }
        }
    }
}
