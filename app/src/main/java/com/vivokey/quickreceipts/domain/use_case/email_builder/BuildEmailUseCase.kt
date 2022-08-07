package com.vivokey.quickreceipts.domain.use_case.email_builder

import android.content.Intent
import android.net.Uri
import javax.inject.Inject

class BuildEmailUseCase @Inject constructor() {
    fun buildEmail(email: String, subject: String?, body: String?, attachment: Uri): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        intent.putExtra(Intent.EXTRA_STREAM, attachment)
        return intent
    }
}