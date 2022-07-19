package com.vivokey.receiptmailer.domain.use_case.email_builder

import android.content.Intent
import android.net.Uri
import javax.inject.Inject

class BuildEmailUseCase @Inject constructor() {
    fun buildEmail(email: String, subject: String, attachments: ArrayList<Uri>) {
        val intent = Intent()
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, email)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachments)
    }
}