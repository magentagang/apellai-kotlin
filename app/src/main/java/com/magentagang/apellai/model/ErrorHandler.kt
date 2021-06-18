package com.magentagang.apellai.model

import timber.log.Timber


// TODO(use this error handler in most else blocks of api calls)
interface ErrorHandler {
    fun logErrorMessage(subsonicResponseError: SubsonicResponseError){
        Timber.i("[ERROR CODE: ${subsonicResponseError.code}. MESSAGE: ${subsonicResponseError.message}]")
    }
}