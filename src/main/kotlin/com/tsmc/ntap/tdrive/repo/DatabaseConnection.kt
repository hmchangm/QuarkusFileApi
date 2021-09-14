package com.tsmc.ntap.tdrive.repo

import com.vladsch.kotlin.jdbc.SessionImpl
import com.vladsch.kotlin.jdbc.sqlQuery
import com.vladsch.kotlin.jdbc.usingDefault
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.sql.DataSource


@ApplicationScoped
class DatabaseConnection(val dataSource: DataSource) {
    fun onStart(@Observes ev: StartupEvent?) {
        SessionImpl.defaultDataSource = { dataSource }
        usingDefault { session ->
            session.execute(
                sqlQuery(
                    """create table Fruit(uuid varchar(100) PRIMARY KEY, `name` varchar(100), description varchar(100) )"""
                )
            )
            session.execute(
                sqlQuery(
                    """INSERT INTO Fruit(uuid,name,description) VALUES ('aa7526d1-0950-4b0a-b2d0-cca76e5a492b','Apple','Winter fruit');"""
                )
            )
        }

    }

    fun onStop(@Observes ev: ShutdownEvent?) {
        usingDefault { session ->
            session.execute(
                sqlQuery(
                    """drop table Fruit"""
                )
            )

        }
    }

}