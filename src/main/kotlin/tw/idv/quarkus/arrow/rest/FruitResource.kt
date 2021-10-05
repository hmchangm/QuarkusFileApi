package tw.idv.quarkus.arrow.rest

import arrow.core.Either
import tw.idv.quarkus.arrow.KaqAppError
import tw.idv.quarkus.arrow.bean.Fruit
import tw.idv.quarkus.arrow.bean.Greeting
import tw.idv.quarkus.arrow.repo.FruitRepo
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/")
class FruitResource {

    @GET
    @Path("/greeting")
    @Produces(MediaType.TEXT_PLAIN)
    suspend fun hello() = Greeting("hello")

    @GET
    @Path("/fruits")
    @Produces(MediaType.APPLICATION_JSON)
    suspend fun getAllFruits(): Response =
        FruitRepo.findAll().fold(
            ifRight = { err -> Response.ok(err).build() },
            ifLeft = { fruits -> KaqAppError.toResponse(fruits) }
        )


    @POST
    @Path("/fruits")
    @Produces(MediaType.APPLICATION_JSON)
    suspend fun addFruit(fruit: Fruit): Response = FruitRepo.add(fruit).fold(
        ifRight = { err -> Response.ok(err).build() },
        ifLeft = { fruit -> KaqAppError.toResponse(fruit) }
    )
}