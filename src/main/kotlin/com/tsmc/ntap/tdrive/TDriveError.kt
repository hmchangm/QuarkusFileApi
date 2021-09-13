package com.tsmc.ntap.tdrive

sealed class TDriveError {

    data class FileReadError(val e :Throwable):TDriveError()

}