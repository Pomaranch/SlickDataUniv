package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._


case class Passenger(id: Option[Long], name : String)

class PassengerTable(tag :Tag) extends Table[Passenger](tag, "passenger"){
  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val name = column[String]("name", O.Length(20))

  def * = (id.?, name) <> (Passenger.apply _ tupled, Passenger.unapply)
}

object PassengerTable{
  val table = TableQuery[PassengerTable]
}