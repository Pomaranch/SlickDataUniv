import model._
import slick.jdbc.PostgresProfile.api._

import java.sql.Timestamp
import java.text.SimpleDateFormat

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object Main {
  val database : Database = Database.forURL("jdbc:postgresql://127.0.0.1/slick?user=pomaranch&password=111")

  val companyRepository = new CompanyRepository(database)
  val passengerRepository = new PassengerRepository(database)
  val tripRepository = new TripRepository(database)
  val passInTripRepository = new PassInTripRepository(database)
  val queryRepository = new QueryRepository(database)


  def main(args: Array[String]): Unit = {
    queryRepository.task102()
  }

  def init(): Unit ={
    Await.result(database.run(CompanyTable.table.schema.create), Duration.Inf)
    Await.result(database.run(PassengerTable.table.schema.create), Duration.Inf)
    Await.result(database.run(TripTable.table.schema.create), Duration.Inf)
    Await.result(database.run(PassInTripTable.table.schema.create), Duration.Inf)
  }

  def fill(): Unit ={
    val companyList = Source.company.split("\n").toList
    for (i <- companyList){
      val temp  =i.replaceAll("[,)(\"]", "")
      val company = Company(Some(temp.head), temp.tail)
      Await.result(companyRepository.create(company), Duration.Inf)
    }

    val passengerList = Source.passenger
    for (i <- passengerList){
        val passenger = Passenger(Some(i._1), i._2)
        Await.result(passengerRepository.create(passenger), Duration.Inf)
      }

    val tripList = Source.trip
    val dateFormat = new SimpleDateFormat("yyyyMMdd hh:mm:ss.SSS")
    for (i <- tripList){
      val trip = Trip(Some(i._1), i._2, i._3, i._4, i._5, new Timestamp(dateFormat.parse(i._6).getTime), new Timestamp(dateFormat.parse(i._7).getTime))
      Await.result(tripRepository.create(trip), Duration.Inf)
    }

    val passInTripList = Source.pass_in_trip
      for (i <- passInTripList){
        val passInTrip = PassInTrip(Some(i._1), Some(new Timestamp(dateFormat.parse(i._2).getTime)), i._3, i._4)
        Await.result(passInTripRepository.create(passInTrip), Duration.Inf)
      }
  }
}
