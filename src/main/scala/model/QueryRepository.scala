package model

import java.text.SimpleDateFormat

import slick.jdbc.PostgresProfile.api._

import scala.collection.mutable.ListBuffer
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

  //

  def task87(): Unit = {

    val travelledFromMoscow = Await.result(database.run(
      TripTable.table
        .join(PassInTripTable.table).on(_.id === _.id_trip)
        .join(PassengerTable.table).on(_._2.id_pass === _.id)
        .map { case (tripInfo, pass) => (tripInfo._1.town_from, pass.id) }
        .filter { case (townFrom, id) => townFrom === "Moscow" }
        .map { case (townFrom, id) => id }
        .result),
      Duration.Inf)

    var livedInMoscowBuffer = new ListBuffer[Long]()

    travelledFromMoscow.distinct.foreach { id => {
      var query = Await.result(database.run(
        TripTable.table
          .join(PassInTripTable.table).on(_.id === _.id_trip)
          .filter { case (trip, passenger) => passenger.id_pass === id }
          .map { case (trip, passenger) => trip.town_from }
          .result
          .headOption),
        Duration.Inf)

      val firstTownFrom = query.get

      if (firstTownFrom == "Moscow") {
        livedInMoscowBuffer += id
      }
    }
    }

    val livedInMoscow = livedInMoscowBuffer.toList

    val travelledInMoscow = Await.result(database.run(TripTable.table
      .join(PassInTripTable.table).on(_.id === _.id_trip)
      .join(PassengerTable.table).on(_._2.id_pass === _.id)
      .map { case (tripInfo, pass) => (tripInfo._1.town_to, pass.id, pass.name) }
      .filter { case (townTo, id, name) => townTo === "Moscow" }
      .map { case (townTo, id, name) => (id, name) }
      .result),
      Duration.Inf)

    val visitors = travelledInMoscow
      .filterNot(passenger => livedInMoscow.contains(passenger._1))
      .toMap

    val moreThanOnce = Await.result(database.run(TripTable.table
      .join(PassInTripTable.table).on(_.id === _.id_trip)
      .filter { case (trip, passenger) => trip.town_to === "Moscow" }
      .groupBy { case (trip, passenger) => passenger.id_pass }
      .map { case (id, group) => (id, group.length) }
      .filter { case (id, amount) => amount > 1 }
      .result),
      Duration.Inf)

    moreThanOnce
      .foreach { passenger => {
        print(visitors(passenger._1), passenger._2)
      }
      }
  }

  //

  def task122(): Unit = {
    val passengers = Await.result(database.run(
      PassInTripTable.table
        .join(PassengerTable.table).on(_.id_pass === _.id)
        .map { case (id, info) => id.id_pass -> info.name }
        .result),
      Duration.Inf)
      .distinct

    val firstTown = collection.mutable.Map[Long, String]()
    val lastTown = collection.mutable.Map[Long, String]()

    passengers.foreach { case (id, name) =>
      val firstTownFromQuery = Await.result(database.run(
        TripTable.table
          .join(PassInTripTable.table).on(_.id === _.id_trip)
          .sortBy { case (trip, passenger) => (passenger.date, trip.time_out) }
          .filter { case (trip, passenger) => passenger.id_pass === id }
          .map { case (trip, passenger) => trip.town_from }
          .result
          .headOption),
        Duration.Inf)

      val lastTownToQuery = Await.result(database.run(
        TripTable.table
          .join(PassInTripTable.table).on(_.id === _.id_trip)
          .sortBy { case (trip, passenger) => (passenger.date.desc, trip.time_out.desc) }
          .filter { case (trip, passenger) => passenger.id_pass === id }
          .map { case (trip, passenger) => trip.town_to }
          .result
          .headOption),
        Duration.Inf)

      firstTown += (id -> firstTownFromQuery.get)
      lastTown += (id -> lastTownToQuery.get)
    }

    passengers
      .filter { case (id, name) => firstTown(id) != lastTown(id) }
      .foreach { case (id, name) =>
        println(name + " " + firstTown(id))
      }
  }

  //

  def task124(): Unit = {
    val query = Await.result(database.run(
      PassengerTable.table
        .join(PassInTripTable.table).on(_.id === _.id_pass)
        .join(TripTable.table).on(_._2.id_trip === _.id)
        .groupBy { case (passenger, trip) => (passenger._1.id, passenger._1.name, trip.id_comp) }
        .map { case (pass, group) => (pass._1, pass._2, group.length) }
        .result
    ),
      Duration.Inf)

    print(query.groupBy { case (id, name, amount) => (id, name) }
      .filter { case (id, group) => group.length > 1 && group.distinct.length == 1 }
      .map { case (id, group) => id._2 })
  }

  def task66(): Unit = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")

    val query = TripTable.table
      .join(PassInTripTable.table).on(_.id === _.id_trip)
      .join(PassengerTable.table).on(_._2.id_pass === _.id)
      .groupBy {
        joinedTables => (joinedTables._2.name, joinedTables._1._1.town_from, joinedTables._1._2.date)
      }.map {
      groupedByName => (groupedByName._1._1, groupedByName._1._2, groupedByName._1._3)
    }

    val result = Await.result(database.run(query.result), Duration.Inf)

    result.filter { x =>
      x._2 == "Rostov" &&
        x._3.getTime >= dateFormat.parse("2003-04-01").getTime &&
        x._3.getTime <= dateFormat.parse("2003-04-07").getTime
    }.foreach(println)
  }

  def task131(): Unit = {
    val letters = List('a', 'e', 'i', 'o', 'u')
    val query = Await.result(database.run(
      TripTable.table
        .map(x => (x.town_to, x.town_from))
        .result), Duration.Inf)
      .toMap

    val cities = (query.keys ++ query.values).toSet

    cities.foreach(item => {
      val occurences = collection.mutable.Map[Long, Int]()
      letters.foreach(char => {
        occurences(char) = item.count(_ == char)
      })

      val groups = occurences
        .groupBy { case (key, amount) => amount }
        .filter { case (amount, group) => amount > 0}

      if (groups.size == 1 && groups.head._2.size > 1) {
        println(item)
      }
    })


  }

}
