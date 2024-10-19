package com.indexer.models

import slick.jdbc.SQLiteProfile.api.*

import java.nio.file.{Path, Paths}

case class FileMetadata(
    fileName: String,
    path: Path,
    size: Long,
    creationDate: Option[String],
    updateDate: Option[String],
    fileType: Option[String],
    checksum: Option[String]
)

object FileMetadata {
  def tupled = (FileMetadata.apply _).tupled

  implicit val pathMapping: BaseColumnType[Path] = MappedColumnType.base[Path, String](
    path => path.toString,
    str => Paths.get(str)
  )
}
