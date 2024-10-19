package com.indexer.models.db

object IndexerContentDb {
  def tupled = (IndexerContentDb.apply _).tupled
}

case class IndexerContentDb(fileName: String, word: String, count: Int)
