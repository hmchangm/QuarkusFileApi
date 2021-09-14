package com.tsmc.ntap.tdrive.repo

import arrow.core.*
import com.tsmc.ntap.tdrive.TDriveError
import com.tsmc.ntap.tdrive.bean.Fruit
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
        val findAll: suspend () -> Either<TDriveError, List<Fruit>> =
            {
                Either.catch {
                    usingDefault { session ->
                        session.list(allFruitQuery, toFruit)
                    }
                }.mapLeft { TDriveError.DatabaseProblem(it) }
            }

        private const val findByIdSql = "select * from fruit where uuid = ?"
        val findById: suspend (uuid: String) -> Either<TDriveError, Fruit> = {uuid->
            runCatching {usingDefault { session ->
                session.first(sqlQuery(findByIdSql,uuid), toFruit)
            }}.fold(
            { fruit->
                 when (val one = fruit.toOption()) {
                    is Some -> one.value.right()
                    is None -> TDriveError.SomeError(uuid).left()
                }
            }, {  Either.Left(TDriveError.DatabaseProblem(it)) })
        }

        private const val insertSql: String = "insert into fruit (uuid,name,description) values (?, ?, ?)"
        val add: suspend (fruit: Fruit) -> Either<TDriveError, Fruit> = {fruit->
            Either.catch {
                usingDefault { session ->
                    val one = fruit.copy(uuid = UUID.randomUUID().toString())
                    session.transaction { tx ->
                        tx.update(sqlQuery(insertSql, one.uuid, one.name, one.description))
                    }
                    one
                }
            }.mapLeft { TDriveError.DatabaseProblem(it) }
        }

        private const val updateSql: String = "update fruit set name=?,description=? where uuid=?"
        val update: suspend (fruit: Fruit) -> Either<TDriveError,Unit> = {one->
            Either.catch {
                usingDefault { session ->
                    session.transaction { tx ->
                        tx.update(sqlQuery(updateSql,  one.name, one.description,one.uuid))
                    }
                }
                Unit
            }.mapLeft { TDriveError.DatabaseProblem(it) }
        }

    }
}