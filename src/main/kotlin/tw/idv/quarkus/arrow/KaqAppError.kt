package tw.idv.quarkus.arrow

import javax.ws.rs.core.Response

sealed class KaqAppError {
    data class DatabaseProblem(val e :Throwable): KaqAppError()
    data class FileReadError(val e :Throwable): KaqAppError()
    data class SomeError(val uuid: String): KaqAppError()

    companion object {
        fun toResponse(e: KaqAppError): Response = when (e) {
            is FileReadError -> Response.serverError().entity(e.e.stackTraceToString()).build()
            is DatabaseProblem -> Response.serverError().entity("DbError ${e.e.stackTraceToString()}")
                .build()
            is SomeError -> Response.serverError().entity("SomeError").build()
        }
    }
}