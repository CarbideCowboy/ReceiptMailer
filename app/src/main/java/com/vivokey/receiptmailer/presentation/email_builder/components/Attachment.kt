package com.vivokey.receiptmailer.presentation.email_builder.components

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.vivokey.receiptmailer.R

@Composable
fun Attachment(image: Image) {
    Box(
        modifier = Modifier.size(100.dp).clip(RoundedCornerShape(10.dp)).background(Color.Magenta)
    )
}