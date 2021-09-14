package com.tsmc.ntap.tdrive.repo

import arrow.core.Either
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
    }
}