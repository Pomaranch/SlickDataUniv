package model

import java.sql._

import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.concurrent.Future

case class PassInTrip(id_trip: Option[Long], date: Option[Timestamp], id_pass: Long, place: String)

class PassInTripTable(tag: Tag) extends Table[PassInTrip](tag, "pass_in_trip") {


  val id_trip = column[Long]("id_trip")
  val date = column[Timestamp]("date")
  val id_pass = column[Long]("id_pass")
  val place = column[String]("place", O.Length(10))

  val tripFk = foreignKey("id_trip_fk", id_trip, TableQuery[TripTable])(_.id)
  val passFk = foreignKey("id_pass_fk", id_pass, TableQuery[PassengerTable])(_.id)
  val passInTripPk = primaryKey("pit_pk", (id_trip, id_pass, date))

  def * = (id_trip.?, date.?, id_pass, place) <> (PassInTrip.apply _ tupled, PassInTrip.unapply)
}

object PassInTripTable {
  val table = TableQuery[PassInTripTable]
}

class PassInTripRepository(database: Database) {
  def create(passInTrip: PassInTrip): Future[PassInTrip] = {
    database.run(PassInTripTable.table returning PassInTripTable.table += passInTrip)
  }
}