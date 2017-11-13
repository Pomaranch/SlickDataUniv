package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._



case class Company(id:Option[Long], name: String)

class CompanyTable(tag: Tag) extends Table[Company](tag, "company"){
  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val name = column[String]("name", O.Length(10))

  def * = (id.?, name) <> (Company.apply _ tupled, Company.unapply)

}

object CompanyTable{
  val table = TableQuery[CompanyTable]
}