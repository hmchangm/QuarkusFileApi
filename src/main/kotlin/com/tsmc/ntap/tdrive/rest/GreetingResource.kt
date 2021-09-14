package com.tsmc.ntap.tdrive.rest

import arrow.core.Either
import com.tsmc.ntap.tdrive.TDriveError
import com.tsmc.ntap.tdrive.bean.Fruit
import com.tsmc.ntap.tdrive.bean.Greeting
import com.tsmc.ntap.tdrive.repo.FruitRepo
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/")
class GreetingResource {

    @GET
    @Path("/greeting")
    @Produces(MediaType.TEXT_PLAIN)
    suspend fun hello() = Greeting("hello")

    @GET
    @Path("/fruits")
    @Produces(MediaType.APPLICATION_JSON)
    suspend fun getAllFruits(): Response = when (val either = FruitRepo.findAll()) {
        is Either.Right -> Response.ok(either.value).build()
        is Either.Left -> TDriveError.toResponse(either.value)
    }

    @POST
    @Path("/fruits")
    @Produces(MediaType.APPLICATION_JSON)
    suspend fun addFruit( fruit: Fruit): Response = when (val either = FruitRepo.add(fruit)) {
        is Either.Right -> Response.ok(either.value).build()
        is Either.Left -> TDriveError.toResponse(either.value)
    }
}