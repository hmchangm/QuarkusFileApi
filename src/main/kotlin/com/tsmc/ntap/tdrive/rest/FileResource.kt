package com.tsmc.ntap.tdrive.rest

import arrow.core.Either
import com.tsmc.ntap.tdrive.TDriveError
import com.tsmc.ntap.tdrive.TDriveError.FileReadError
import com.tsmc.ntap.tdrive.service.FileService
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/file")
class FileResource {

    @GET
    @Path("{fileName}")
    @Produces(MediaType.TEXT_PLAIN)
    suspend fun readFile(fileName: String): Response =
        when (val e = FileService.readPart(fileName)) {
            is Either.Right -> Response.ok(e.value).build()
            is Either.Left -> TDriveError.toResponse(e.value)
        }


    @GET
    @Path("combine")
    @Produces(MediaType.TEXT_PLAIN)
    suspend fun combine(): Response =
        listOf("part1.txt", "part2.txt", "part3.txt", "part4.txt").let { fileNames ->
            when (val e = FileService.combineFiles(fileNames)) {
                is Either.Right -> Response.ok(e.value).build()
                is Either.Left -> TDriveError.toResponse(e.value)
            }
        }



}