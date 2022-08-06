package com.vivokey.receiptmailer.domain.use_case.email_builder

import java.io.File
import javax.inject.Inject

class PurgeImageFilesUseCase @Inject constructor(
    private val outputDirectory: File
) {
    fun purgeImageFiles() {
        purgeImageFile(outputDirectory)
    }

    private fun purgeImageFile(fileOrDirectory: File) {
        if(fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles()?.forEach {
                purgeImageFile(it)
            }
        }

        fileOrDirectory.delete()
    }
}