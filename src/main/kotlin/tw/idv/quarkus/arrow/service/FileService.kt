package tw.idv.quarkus.arrow.service

import arrow.core.Either
import arrow.core.right
import arrow.core.traverseEither
import tw.idv.quarkus.arrow.KaqAppError
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.nio.charset.Charset

typealias FileUid = String

class FileService {

    companion object {
        private val classLoader: ClassLoader = javaClass.classLoader
        val readPart: suspend (fileName: String) -> Either<KaqAppError, String> =
            { fileName ->
                runCatching { classLoader.getResourceAsStream(fileName).readAllBytes() }.fold(
                    {
                        it.toString(Charset.defaultCharset()).let { s -> Either.Right(s) }
                    },
                    {
                        Either.Left(KaqAppError.FileReadError(it))
                    })
            }


        val combineFiles: suspend (files: List<String>) -> Either<KaqAppError, String> = { files ->
            files.traverseEither {
                readPart(it)
            }.map { contents ->
                contents.joinToString(
                    """
                
------------------------------------------------------

                          """.trimIndent()
                )
            }
        }


        val uploadFilesToS3: suspend (upFiles: List<FileUpload>) -> Either<KaqAppError, List<FileUid>> = { upFiles ->
            upFiles.traverseEither { file ->
                uploadFileToS3(file)
            }
        }


        val uploadFileToS3: suspend (uploadFile: FileUpload) -> Either<KaqAppError, FileUid> = {
            "S3Uuid".right()
        }

    }
}
