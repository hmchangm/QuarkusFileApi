package tw.idv.quarkus.arrow.repo

import arrow.core.*
import tw.idv.quarkus.arrow.KaqAppError
import tw.idv.quarkus.arrow.bean.Fruit
import com.vladsch.kotlin.jdbc.Row
import com.vladsch.kotlin.jdbc.SqlQuery
import com.vladsch.kotlin.jdbc.sqlQuery
import com.vladsch.kotlin.jdbc.usingDefault
import java.util.*

class FruitRepo {

    companion object {
        private val toFruit: (Row) -> Fruit = { row ->
            Fruit(
                row.string("uuid"),
                row.string("name"),
                row.string("description")
            )
        }
        private val allFruitQuery: SqlQuery = sqlQuery("select * from fruit")
        val findAll: suspend () -> Either<KaqAppError, List<Fruit>> =
            {
                Either.catch {
                    usingDefault { session ->
                        session.list(allFruitQuery, toFruit)
                    }
                }.mapLeft { KaqAppError.DatabaseProblem(it) }
            }

        private const val findByIdSql = "select * from fruit where uuid = ?"
        val findById: suspend (uuid: String) -> Either<KaqAppError, Fruit> = { uuid->
            runCatching {usingDefault { session ->
                session.first(sqlQuery(findByIdSql,uuid), toFruit)
            }}.fold(
            { fruit->
                 when (val one = fruit.toOption()) {
                    is Some -> one.value.right()
                    is None -> KaqAppError.SomeError(uuid).left()
                }
            }, {  Either.Left(KaqAppError.DatabaseProblem(it)) })
        }

        private const val insertSql: String = "insert into fruit (uuid,name,description) values (?, ?, ?)"
        val add: suspend (fruit: Fruit) -> Either<KaqAppError, Fruit> = { fruit->
            Either.catch {
                usingDefault { session ->
                    val one = fruit.copy(uuid = UUID.randomUUID().toString())
                    session.transaction { tx ->
                        tx.update(sqlQuery(insertSql, one.uuid, one.name, one.description))
                    }
                    one
                }
            }.mapLeft { KaqAppError.DatabaseProblem(it) }
        }

        private const val updateSql: String = "update fruit set name=?,description=? where uuid=?"
        val update: suspend (fruit: Fruit) -> Either<KaqAppError,Unit> = { one->
            Either.catch {
                usingDefault { session ->
                    session.transaction { tx ->
                        tx.update(sqlQuery(updateSql,  one.name, one.description,one.uuid))
                    }
                }
                Unit
            }.mapLeft { KaqAppError.DatabaseProblem(it) }
        }

    }
}