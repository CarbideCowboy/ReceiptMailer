package com.vivokey.receiptmailer.presentation.email_builder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import android.os.IBinder
import android.provider.Settings.Global.getString
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.vivokey.receiptmailer.R
import com.vivokey.receiptmailer.domain.use_case.email_builder.BuildEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class BuildEmailViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val buildEmailUseCase: BuildEmailUseCase,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    var images: List<Image> by mutableStateOf(emptyList())
    var recipient: String? by mutableStateOf(sharedPreferences.getString(context.getString(R.string.preference_recipient), ""))
    var subject: String? by mutableStateOf(sharedPreferences.getString(context.getString(R.string.preference_subject), ""))

    fun updateRecipient(context: Context, value: String) {
        recipient = value
        sharedPreferences.edit().putString(context.getString(R.string.preference_recipient), value).apply()
    }

    fun updateSubject(context: Context, value: String) {
        subject = value
        sharedPreferences.edit().putString(context.getString(R.string.preference_subject), value).apply()
    }

    fun updateAttachments(context: Context, value: List<Image>) {
        images = value
    }
}