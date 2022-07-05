package com.vivokey.receiptmailer.presentation.email_builder.components

import android.media.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.vivokey.receiptmailer.presentation.email_builder.BuildEmailViewModel

@Composable
fun AttachmentList(viewModel: BuildEmailViewModel = hiltViewModel()) {
    FlowRow(
        mainAxisSpacing = 10.dp,
        crossAxisSpacing = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        viewModel.images.forEach { image ->
            Attachment(image)
        }
        Box(
            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(10.dp)).background(Color.Red)
        )
    }
}