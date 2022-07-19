package com.vivokey.receiptmailer.presentation.email_builder.components

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.vivokey.receiptmailer.BuildConfig
import com.vivokey.receiptmailer.presentation.email_builder.BuildEmailViewModel
import java.io.File

@Composable
fun AttachmentList(viewModel: BuildEmailViewModel = hiltViewModel()) {

    val context = LocalContext.current

    var tempUri: Uri? = null

    FlowRow(
        mainAxisSpacing = 10.dp,
        crossAxisSpacing = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        viewModel.images.forEach { image ->
            Attachment(image)
        }
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Red)
                .clickable{
                    viewModel.shouldShowCamera.value = true
                }
        )
    }
}

fun Context.findActivity(): AppCompatActivity? = when(this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}