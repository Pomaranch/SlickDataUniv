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
      .map { case (x, pairs) => (x, pairs.map(item => item._2).sum) }

    val maxAmount = result.maxBy { case (x, amount) => amount }._2

    print(result.filter { case (x, amount) => amount == maxAmount })
  }

  def task72(): Unit = {

    val query = TripTable.table.join(PassInTripTable.table).on(_.id === _.id_trip).join(PassengerTable.table).on(_._2.id_pass === _.id)
      .groupBy { case (trip, pass) => (pass.name, trip._1.id_comp) }
      .map { case (name, compGroup) => (name._1, name._2, compGroup.length) }
      .groupBy { case (name, companyId, length) => name -> length }
      .map { case (name, numberOfTrips) => (name._1, numberOfTrips.length, name._2) }
      .filter(x => x._2 === 1)
      .map { case (name, company, flights) => name -> flights }

    val result = Await.result(database.run(query.result), Duration.Inf)
    val maxAmount = result.maxBy(x => x._2)._2

    print(result.filter(x => x._2 == maxAmount))


  }

  def task88(): Unit = {

    val query = TripTable.table
      .join(PassInTripTable.table).on(_.id === _.id_trip)
      .join(PassengerTable.table).on(_._2.id_pass === _.id)
      .join(CompanyTable.table).on(_._1._1.id_comp === _.id)
      .groupBy { case (trip, company) => trip._2.name -> company.name }
      .map { case (name, compGroup) => (name._1, name._2, compGroup.length) }
      .groupBy { case (name, companyName, length) => (name, length, companyName) }
      .map { case (name, numberOfTrips) => (name._1, numberOfTrips.length, name._2, name._3) }
      .filter(x => x._2 === 1)
      .map { case (name, company, flights, companyName) => (name, flights, companyName) }

    val result = Await.result(database.run(query.result), Duration.Inf)
    val maxAmount = result.maxBy(x => x._2)._2

    print(result.filter(x => x._2 == maxAmount))
  }


  def task77(): Unit = {
    val query = TripTable.table
      .filter(x => x.town_from === "Rostov")
      .groupBy(x => x.time_out)
  }

  def task114(): Unit = {
    val query = PassengerTable.table
      .join(PassInTripTable.table)
      .on(_.id === _.id_pass)
      .groupBy { case (passenger, trip) => (trip.place, passenger.name) }
      .map { case (passenger, group) => (passenger._2, group.length) }
      .filter { x => x._2 > 1 }

    val result = Await.result(database.run(query.result), Duration.Inf)
    val maxAmount = result.maxBy((x) => x._2)._2

    print(result.filter(x => x._2 == maxAmount))

  }

  def task102(): Unit = {
    
    val query = TripTable.table
      .join(PassInTripTable.table).on(_.id === _.id_trip)
      .join(PassengerTable.table).on(_._2.id_pass === _.id)
      .map { case (trip, pass) => (trip._1.town_to, trip._1.town_from, pass.name) }
      .result

    var result = Await.result(database.run(query), Duration.Inf)

    result = result
      .map { case (town_to, town_from, id) =>
        if (town_to.compareTo(town_from) > 0)
          (town_to, town_from, id)
        else
          (town_from, town_to, id)
      }

    val group = result
      .groupBy { case (town_to, town_from, id) => id }
      .map { case (id, trips) => (id, trips.map(_._1).toSet) }
      .filter { case (id, sources) => sources.size == 1 }
      .keys


    print(group)
  }

  def task103(): Unit = {
    //val query = TripTable.table
    //.map(trips => trips.mapTo(trips.id).max)

  }


}
