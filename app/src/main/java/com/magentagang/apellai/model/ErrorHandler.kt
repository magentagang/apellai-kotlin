package com.magentagang.apellai.model

import timber.log.Timber


// TODO(use this error handler in most else blocks of api calls)
interface ErrorHandler {
    companion object {
        fun logErrorMessage(subsonicResponseError: SubsonicResponseError?) {
            if (subsonicResponseError != null) {
                Timber.i("[ERROR CODE: ${subsonicResponseError.code}. MESSAGE: ${subsonicResponseError.message}]")
            } else {
                Timber.i("[ERROR CODE: ${-1}. MESSAGE: No error object was found")
            }
        }
    }
}