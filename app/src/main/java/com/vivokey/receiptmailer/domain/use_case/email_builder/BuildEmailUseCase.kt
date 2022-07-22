package com.vivokey.receiptmailer.domain.use_case.email_builder

import android.content.Intent
import android.net.Uri
import javax.inject.Inject

class BuildEmailUseCase @Inject constructor() {
    fun buildEmail(email: String, subject: String, attachments: List<Uri>): Intent {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_STREAM, ArrayList(attachments))
        return intent
    }
}