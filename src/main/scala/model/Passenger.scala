package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future


case class Passenger(id: Option[Long], name : String)

class PassengerTable(tag :Tag) extends Table[Passenger](tag, "passenger"){
  val id = column[Long]("id", O.PrimaryKey)
  val name = column[String]("name", O.Length(20))

  def * = (id.?, name) <> (Passenger.apply _ tupled, Passenger.unapply)
}

object PassengerTable{
  val table = TableQuery[PassengerTable]
}

class PassengerRepository(db : Database){
  val passengerTableQuery = TableQuery[PassengerTable]

  def create(passenger: Passenger): Future[Passenger] ={
    db.run(PassengerTable.table returning PassengerTable.table += passenger)
  }
  def update(passenger: Passenger): Future[Int] =
    db.run(passengerTableQuery.filter(_.id === passenger.id).update(passenger))

  def delete(passengerId: Long): Future[Int] =
    db.run(passengerTableQuery.filter(_.id === passengerId).delete)

  def getById(passengerId: Long): Future[Option[Passenger]] =
    db.run(passengerTableQuery.filter(_.id === passengerId).result.headOption)
}