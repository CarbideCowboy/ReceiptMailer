package com.vivokey.receiptmailer.presentation.email_builder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.vivokey.receiptmailer.R
import com.vivokey.receiptmailer.domain.use_case.email_builder.BuildEmailUseCase
import com.vivokey.receiptmailer.domain.use_case.email_builder.TakePictureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.concurrent.ExecutorService
import javax.inject.Inject

@HiltViewModel
class BuildEmailViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val buildEmailUseCase: BuildEmailUseCase,
    private val takePictureUseCase: TakePictureUseCase,
    private val sharedPreferences: SharedPreferences,
    private val outputDirectory: File,
    val cameraExecutor: ExecutorService,
) : ViewModel() {

    var image: Uri? by mutableStateOf(null)
    var recipient: String? by mutableStateOf(sharedPreferences.getString(context.getString(R.string.preference_recipient), ""))
    var subject: String by mutableStateOf("Receipt")
    var body: String by mutableStateOf("")

    var shouldShowCameraFullScreen: MutableState<Boolean> = mutableStateOf(false)
    var shouldStartIntent: MutableState<Boolean> = mutableStateOf(false)

    fun updateRecipient(context: Context, value: String) {
        recipient = value
        sharedPreferences.edit().putString(context.getString(R.string.preference_recipient), value).apply()
    }

    private fun handleImageCapture(uri: Uri) {
        image = uri
        shouldStartIntent.value = true
    }


    fun takePhoto(
        context: Context,
        filenameFormat: String,
        imageCapture: ImageCapture,
        onError: (ImageCaptureException) -> Unit
    ) {
        takePictureUseCase.takePhoto(
            context,
            filenameFormat,
            imageCapture,
            outputDirectory,
            cameraExecutor,
            ::handleImageCapture,
            onError
        )
    }

    fun getEmailIntent(): Intent {
        return buildEmailUseCase.buildEmail(recipient!!, subject, body, image!!)
    }
}