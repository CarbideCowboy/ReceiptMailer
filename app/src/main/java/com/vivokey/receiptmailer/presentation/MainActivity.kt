package com.vivokey.receiptmailer.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vivokey.receiptmailer.ui.theme.ReceiptMailerTheme
import dagger.hilt.android.AndroidEntryPoint
import com.vivokey.receiptmailer.presentation.email_builder.BuildEmailViewModel
import com.vivokey.receiptmailer.presentation.email_builder.components.AttachmentList
import com.vivokey.receiptmailer.presentation.email_builder.components.CameraView

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: BuildEmailViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        println("Camera permission $it")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestCameraPermission()
        requestSharedStoragePermission()

        setContent {
            ReceiptMailerTheme {
                Scaffold(floatingActionButton = {
                    if(!viewModel.shouldShowCamera.value) {
                        FloatingActionButton(onClick = {
                            onSendEmailPressed()
                        }) {
                            Icon(Icons.Filled.Send, "")
                        }
                    }
                }) {
                    Surface(
                        modifier = Modifier.padding(top = 32.dp),
                        color = MaterialTheme.colors.background) {
                        Column(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            TextField(
                                label = { Text("Enter recipient email") },
                                value = viewModel.recipient ?: "",
                                onValueChange = {
                                    viewModel.updateRecipient(
                                        applicationContext,
                                        it
                                    )
                                })
                            TextField(
                                label = { Text("Enter subject line (optional)") },
                                value = viewModel.subject ?: "",
                                onValueChange = { viewModel.updateSubject(applicationContext, it) })
                            TextField(
                                label = { Text("Enter body text (optional)") },
                                maxLines = 5,
                                value = viewModel.body ?: "",
                                onValueChange = { viewModel.updateBody(applicationContext, it) })
                            AttachmentList()
                        }

                        if (viewModel.shouldShowCamera.value) {
                            CameraView(
                                this,
                                outputDirectory = viewModel.outputDirectory,
                                executor = viewModel.cameraExecutor,
                                onError = { println("View Error $it") }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestSharedStoragePermission() {
        if(!Environment.isExternalStorageManager()) {
            val intent = Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                println("Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA
            ) -> println("Show permission dialog")

            else -> requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun onSendEmailPressed() {
        if(viewModel.recipient != "") {
            val intent = viewModel.getEmailIntent()
            try {
                startActivity(Intent.createChooser(intent, "Send mail..."))
            } catch (exception: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    "There are no email clients installed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        else {
            Toast.makeText(
                this,
                "Please specify an email address for your recipient",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onBackPressed() {
        if(viewModel.shouldShowCamera.value) {
            viewModel.shouldShowCamera.value = false
        }
        else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cameraExecutor.shutdown()
    }
}