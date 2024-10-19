package com.indexer.models.db

import com.indexer.models.FileMetadata
import com.indexer.models.FileMetadata.pathMapping
import slick.jdbc.SQLiteProfile.api.*

import java.nio.file.Path

class FileMetadataTable(tag: Tag) extends Table[FileMetadata](tag, "file_metadata") {
  def * = (fileName, path, size, creationDate, updateDate, fileType, checksum) <> (FileMetadata.tupled, FileMetadata.unapply)

  def fileName = column[String]("file_name", O.PrimaryKey)

  def path = column[Path]("path")

  def size = column[Long]("size")

  def creationDate = column[Option[String]]("creation_date")

  def updateDate = column[Option[String]]("update_date")

  def fileType = column[Option[String]]("file_type")

  def checksum = column[Option[String]]("checksum")
}
