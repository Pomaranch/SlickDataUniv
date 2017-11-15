package model

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration


class QueryRepository(database: Database) {

  def task63(): Unit = {
    val query = PassengerTable.table
      .join(PassInTripTable.table)
      .on(_.id === _.id_pass)
      .groupBy { case (passenger, trip) => (trip.place, passenger.name) }
      .map { case (passenger, group) => (passenger._2, group.length, passenger._1) }
      .filter { x => x._2 > 1 }
      .map { x => x._1 -> x._3 }

    print(Await.result(database.run(query.result), Duration.Inf))
  }

  def task67(): Unit = {
    val query = TripTable.table
      .groupBy(x => (x.town_from, x.town_to))
      .map { case (x, group) => (x, group.length) }

    val result = Await.result(database.run(query.result), Duration.Inf)
    val maxAmount = result.maxBy { case (x, amount) => amount }._2

    print(result.filter { case (x, amount) => amount == maxAmount })
  }

  def task68(): Unit = {
    val query = TripTable.table
      .groupBy(x => (x.town_from, x.town_to))
      .map { case (x, group) => (x, group.length) }

    val result = Await.result(database.run(query.result), Duration.Inf)
      .map { case (x, amount) => if (x._2 > x._1) (x.swap, amount) else (x, amount) }
      .groupBy(_._1)
      .map { case (x, pairs) => (x, pairs.map(item => item._2).sum)}

    val maxAmount = result.maxBy { case (x, amount) => amount }._2

    print(result.filter { case (x, amount) => amount == maxAmount })
  }

  def task77(): Unit ={
    val query = TripTable.table
      .filter(x => x.town_from === "Rostov")
      .groupBy(x => x.time_out)
  }
}
