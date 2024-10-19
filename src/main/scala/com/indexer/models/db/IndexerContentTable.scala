package com.indexer.models.db

import slick.jdbc.SQLiteProfile.api.*

class IndexerContentTable(tag: Tag) extends Table[IndexerContentDb](tag, "indexer_content") {
  def pk = primaryKey("pk_indexer_content", (fileName, word)) // Composite key to avoid duplicates

  def * = (fileName, word, count) <> (IndexerContentDb.tupled, IndexerContentDb.unapply)

  def fileName = column[String]("file_name")

  def word = column[String]("word")

  def count = column[Int]("count")
}
