package model

import java.sql.{Date, Timestamp}
import slick.lifted.Tag

import slick.jdbc.PostgresProfile.api._
import java.time.{LocalDate, LocalDateTime}

import slick.ast.BaseTypedType
import slick.jdbc.JdbcType

case class Trip(id:Option[Long], id_comp : Long, plane : String, town_to: String, town_from : String, time_out : Timestamp, time_in : Timestamp)

class TripTable(tag : Tag) extends Table[Trip](tag, "trip"){

//  implicit def dateTime  =
//    MappedColumnType.base[LocalDateTime, Date](
//      dateTime => new Date(dateTime.getSecond),
//      date => new LocalDateTime(date)
//    )

  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val id_comp = column[Long]("id_comp")
  val plane = column[String]("plane", O.Length(10))
  val town_from = column[String]("town_from", O.Length(25))
  val town_to = column[String]("town_to", O.Length(25))
  val time_out = column[Timestamp]("time_out")
  val time_in = column[Timestamp]("time_in")

  val id_comp_fk = foreignKey("id_comp_fk", id_comp, TableQuery[CompanyTable])(_.id)

  def * = (id.?, id_comp, plane, town_from, town_to, time_in, time_out) <> (Trip.apply _ tupled, Trip.unapply)

}

object TripTable{
  val table = TableQuery[TripTable]
}