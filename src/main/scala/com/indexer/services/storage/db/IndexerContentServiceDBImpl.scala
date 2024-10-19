package com.indexer.services.storage.db

import akka.Done
import com.indexer.models.IndexerContent
import com.indexer.models.db.{FileMetadataTable, IndexerContentDb, IndexerContentTable}
import com.indexer.services.storage.IndexerContentService
import slick.jdbc.SQLiteProfile.api.*

import scala.concurrent.{ExecutionContext, Future}

object IndexerContentServiceDBImpl {
  def apply()(implicit ec: ExecutionContext): IndexerContentServiceDBImpl = new IndexerContentServiceDBImpl()
}

class IndexerContentServiceDBImpl(implicit ec: ExecutionContext) extends IndexerContentService {
  val db = Database.forConfig("sqlite")
  private val fileMetadataTable = TableQuery[FileMetadataTable]
  private val indexerContentTable = TableQuery[IndexerContentTable]

  override def save(content: IndexerContent): Future[Done] = {
    val insertFileMetadata = fileMetadataTable.insertOrUpdate(content.metadata)
    val insertWordCounts = DBIO.sequence(content.wordRPI.map { case (word, count) =>
      indexerContentTable += IndexerContentDb(content.metadata.fileName, word, count)
    })

    db.run(DBIO.seq(insertFileMetadata, insertWordCounts)).map(_ => Done)
  }

  override def saveBatch(contents: List[IndexerContent]): Future[Done] = {
    val insertActions = contents.flatMap { content =>
      val insertFileMetadata = fileMetadataTable.insertOrUpdate(content.metadata)
      val insertWordCounts = content.wordRPI.map { case (word, count) =>
        indexerContentTable += IndexerContentDb(content.metadata.fileName, word, count)
      }
      insertFileMetadata :: insertWordCounts.toList
    }
    db.run(DBIO.sequence(insertActions)).map(_ => Done)
  }

  db.run(
    DBIO.seq(
      fileMetadataTable.schema.createIfNotExists,
      indexerContentTable.schema.createIfNotExists
    )
  )
}
