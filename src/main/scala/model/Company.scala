package model

import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.concurrent.Future


case class Company(id: Option[Long], name: String)

class CompanyTable(tag: Tag) extends Table[Company](tag, "company") {
  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val name = column[String]("name", O.Length(10))

  def * = (id.?, name) <> (Company.apply _ tupled, Company.unapply)

}

object CompanyTable {
  val table = TableQuery[CompanyTable]
}

class CompanyRepository(db: Database) {
  val companyTableQuery = TableQuery[CompanyTable]

  def create(company: Company): Future[Company] = {
    db.run(CompanyTable.table returning CompanyTable.table += company)
  }

  def update(company: Company): Future[Int] =
    db.run(companyTableQuery.filter(_.id === company.id).update(company))

  def delete(companyId: Long): Future[Int] =
    db.run(companyTableQuery.filter(_.id === companyId).delete)

  def getById(companyId: Long): Future[Option[Company]] =
    db.run(companyTableQuery.filter(_.id === companyId).result.headOption)
}