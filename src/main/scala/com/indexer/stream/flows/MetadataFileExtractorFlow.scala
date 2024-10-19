package com.indexer.stream.flows

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.indexer.models.FileMetadata

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object MetadataFileExtractorFlow {
  def apply(): MetadataFileExtractorFlow = new MetadataFileExtractorFlow()
}

class MetadataFileExtractorFlow extends MetadataFileFlow {

  override def getFlow: Flow[File, FileMetadata, NotUsed] = Flow[File].map { file =>
    val path = file.toPath
    val attributes = Files.readAttributes(path, classOf[BasicFileAttributes])

    val fileName = file.getName
    val fileSize = attributes.size()
    val creationDate = Option(attributes.creationTime())
      .map(_.toInstant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    val updateDate = Option(attributes.lastModifiedTime())
      .map(_.toInstant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    val fileType = Files.probeContentType(path)
    val checksum = calculateChecksum(file)

    FileMetadata(
      fileName = fileName,
      path = path,
      size = fileSize,
      creationDate = creationDate,
      updateDate = updateDate,
      fileType = Some(fileType),
      checksum = checksum
    )
  }

  private def calculateChecksum(file: File): Option[String] = {
    try {
      val buffer = new Array[Byte](8192)
      val md = MessageDigest.getInstance("SHA-256")
      val stream = Files.newInputStream(file.toPath)
      try LazyList.continually(stream.read(buffer)).takeWhile(_ != -1).foreach(md.update(buffer, 0, _))
      finally stream.close()
      Some(md.digest().map("%02x".format(_)).mkString)
    } catch {
      case _: Exception => None
    }
  }

}
