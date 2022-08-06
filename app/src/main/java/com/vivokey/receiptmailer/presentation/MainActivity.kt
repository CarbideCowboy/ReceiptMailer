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
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.vivokey.receiptmailer.R
import com.vivokey.receiptmailer.ui.theme.ReceiptMailerTheme
import dagger.hilt.android.AndroidEntryPoint
import com.vivokey.receiptmailer.presentation.email_builder.BuildEmailViewModel
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
            val focusManager = LocalFocusManager.current
            val interactionSource = remember { MutableInteractionSource() }
            ReceiptMailerTheme {
                Box {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(30.dp),
                        painter = painterResource(id = R.drawable.background),
                        contentDescription = "",
                        contentScale = ContentScale.FillHeight
                    )
                    Scaffold(
                        modifier = Modifier
                            .padding(0.dp)
                            .clickable(interactionSource = interactionSource, indication = null) {
                                focusManager.clearFocus()
                            },
                        backgroundColor = Color.Transparent
                    ) {
                        Surface(
                            color = Color.Transparent
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(it)
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(
                                            bottom = 32.dp,
                                            top = 32.dp,
                                            start = 16.dp,
                                            end = 16.dp
                                        )
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    TextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(0.dp, 74.dp),
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = Color.DarkGray,
                                            textColor = Color.White
                                        ),
                                        label = { Text("Enter recipient email") },
                                        value = viewModel.recipient ?: "",
                                        onValueChange = {
                                            viewModel.updateRecipient(
                                                applicationContext,
                                                it
                                            )
                                        })
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text("Enter subject line (optional)") },
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = Color.DarkGray,
                                            textColor = Color.White
                                        ),
                                        value = viewModel.subject,
                                        onValueChange = {
                                            viewModel.subject = it
                                        })
                                    val bodyLabel =
                                        remember { mutableStateOf("Describe what business expense this receipt is for and who you were with. For example, if it is a lunch receipt, explain it is for lunch with XXXX to discuss YYYY.") }
                                    TextField(
                                        label = { Text(bodyLabel.value) },
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                            .onFocusChanged { focusState ->
                                                when {
                                                    focusState.isFocused ->
                                                        bodyLabel.value =
                                                            "Enter body text (optional)"
                                                    !focusState.isFocused && viewModel.body.isEmpty() ->
                                                        bodyLabel.value =
                                                            "Describe what business expense this receipt is for and who you were with. For example, if it is a lunch receipt, explain it is for lunch with XXXX to discuss YYYY."
                                                }
                                            },
                                        value = viewModel.body,
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = Color.DarkGray,
                                            textColor = Color.White
                                        ),
                                        onValueChange = {
                                            viewModel.body = it
                                        },
                                        trailingIcon = {
                                            if (viewModel.body.isNotEmpty()) {
                                                Icon(
                                                    Icons.Default.Clear,
                                                    contentDescription = "clear text",
                                                    modifier = Modifier.clickable {
                                                        viewModel.body = ""
                                                    }
                                                )
                                            }
                                        })

                                }
                                if (viewModel.image == null) {
                                    CameraView(
                                        modifier =
                                        if (!viewModel.shouldShowCameraFullScreen.value) {
                                            Modifier
                                                .animateContentSize()
                                                .weight(1f)
                                                .width(200.dp)
                                                .padding(bottom = 8.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable {
                                                    focusManager.clearFocus()
                                                    viewModel.shouldShowCameraFullScreen.value =
                                                        !viewModel.shouldShowCameraFullScreen.value
                                                }
                                        } else {
                                            Modifier
                                                .animateContentSize()
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.shouldShowCameraFullScreen.value =
                                                        !viewModel.shouldShowCameraFullScreen.value
                                                }
                                        },
                                        onError = { println("View Error $it") },
                                    )
                                }
                                if (viewModel.image != null) {
                                    onSendEmailPressed()
                                    Image(
                                        painter = rememberAsyncImagePainter(model = viewModel.image),
                                        contentDescription = "",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.FillHeight
                                    )
                                }
                            }
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

        if(viewModel.recipient.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Please specify an email address for your recipient",
                Toast.LENGTH_SHORT
            ).show()
        } else {
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
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cameraExecutor.shutdown()
    }

    override fun onBackPressed() {
        if(viewModel.image != null) {
            viewModel.image = null
            return
        }
        if(viewModel.shouldShowCameraFullScreen.value) {
            viewModel.shouldShowCameraFullScreen.value = false
        } else {
            super.onBackPressed()
        }
    }
}