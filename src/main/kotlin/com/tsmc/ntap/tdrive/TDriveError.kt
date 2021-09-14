package com.tsmc.ntap.tdrive

import javax.ws.rs.core.Response

sealed class TDriveError {
    data class DatabaseProblem(val e :Throwable):TDriveError()
    data class FileReadError(val e :Throwable):TDriveError()
    data class SomeError(val uuid: String):TDriveError()

    companion object {
        fun toResponse(e: TDriveError): Response = when (e) {
            is FileReadError -> Response.serverError().entity(e.e.stackTraceToString()).build()
            is DatabaseProblem -> Response.serverError().entity("DbError ${e.e.stackTraceToString()}")
                .build()
            is SomeError -> Response.serverError().entity("SomeError").build()
        }
    }
}