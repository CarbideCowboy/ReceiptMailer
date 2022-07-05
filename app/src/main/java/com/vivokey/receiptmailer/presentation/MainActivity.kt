package com.vivokey.receiptmailer.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vivokey.receiptmailer.ui.theme.ReceiptMailerTheme
import dagger.hilt.android.AndroidEntryPoint
import com.vivokey.receiptmailer.presentation.email_builder.BuildEmailViewModel
import com.vivokey.receiptmailer.presentation.email_builder.components.AttachmentList

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: BuildEmailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReceiptMailerTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "Enter email recipient:")
                        TextField(value = viewModel.recipient ?: "", onValueChange = {viewModel.updateRecipient(applicationContext, it)})
                        Text(text = "Enter subject line:")
                        TextField(value = viewModel.subject ?: "", onValueChange = {viewModel.updateSubject(applicationContext, it)})
                        AttachmentList()
                    }
                }
            }
        }
    }

    fun GetAttachments() {
        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        val getContent = registerForActivityResult(Intent.createChooser(intent, "Select Attachment"), viewModel.updateAttachments(data))
    }
}