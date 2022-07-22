package com.vivokey.receiptmailer.presentation.email_builder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
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
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class BuildEmailViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val buildEmailUseCase: BuildEmailUseCase,
    private val takePictureUseCase: TakePictureUseCase,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    var images: List<Uri> by mutableStateOf(emptyList())
    var recipient: String? by mutableStateOf(sharedPreferences.getString(context.getString(R.string.preference_recipient), ""))
    var subject: String? by mutableStateOf(sharedPreferences.getString(context.getString(R.string.preference_subject), ""))
    var body: String? by mutableStateOf(sharedPreferences.getString(context.getString(R.string.preference_body), ""))

    var outputDirectory: File
    var cameraExecutor: ExecutorService

    var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    init {
        outputDirectory = getOutputDirectory(context)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun updateRecipient(context: Context, value: String) {
        recipient = value
        sharedPreferences.edit().putString(context.getString(R.string.preference_recipient), value).apply()
    }

    fun updateSubject(context: Context, value: String) {
        subject = value
        sharedPreferences.edit().putString(context.getString(R.string.preference_subject), value).apply()
    }

    fun updateBody(context: Context, value: String) {
        body = value
        sharedPreferences.edit().putString(context.getString(R.string.preference_body), value).apply()
    }

    private fun handleImageCapture(uri: Uri) {
        shouldShowCamera.value = false
        images = images + uri
    }

    private fun getOutputDirectory(context: Context): File {
        val dir = Environment.getExternalStorageDirectory().absolutePath.let {
            File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return dir
    }

    fun takePhoto(
        context: Context,
        filenameFormat: String,
        imageCapture: ImageCapture,
        outputDirectory: File,
        executor: Executor,
        onError: (ImageCaptureException) -> Unit
    ) {
        takePictureUseCase.takePhoto(
            context,
            filenameFormat,
            imageCapture,
            outputDirectory,
            executor,
            ::handleImageCapture,
            onError
        )
    }

    fun getEmailIntent(): Intent {
        return buildEmailUseCase.buildEmail(recipient!!, subject, body, images)
    }
}