package com.magentagang.apellai.model

import timber.log.Timber

interface ErrorHandler {
    fun logErrorMessage(subsonicResponseError: SubsonicResponseError){
        Timber.i("[ERROR CODE: ${subsonicResponseError.code}. MESSAGE: ${subsonicResponseError.message}]")
    }
}