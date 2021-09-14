package com.tsmc.ntap.tdrive.service

import arrow.core.Either
import arrow.core.traverseEither
import com.tsmc.ntap.tdrive.TDriveError
import java.nio.charset.Charset


class FileService {

    companion object {
        private val classLoader: ClassLoader = javaClass.classLoader
        val readPart: suspend (fileName: String) -> Either<TDriveError, String> =
            { fileName ->
                runCatching { classLoader.getResourceAsStream(fileName).readAllBytes() }.fold(
                    {
                        it.toString(Charset.defaultCharset()).let { s -> Either.Right(s) }
                    },
                    {
                        Either.Left(TDriveError.FileReadError(it))
                    })
            }



        val combineFiles:suspend (files: List<String>) -> Either<TDriveError,String> =  { files ->
            files.traverseEither {
                readPart(it)
            }.map{contents-> contents.joinToString("""
                
------------------------------------------------------

                          """.trimIndent())}
        }

    }
}
