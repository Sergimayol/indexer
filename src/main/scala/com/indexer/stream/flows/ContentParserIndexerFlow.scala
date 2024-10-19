package com.indexer.stream.flows

import akka.NotUsed
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import com.indexer.models.{FileMetadata, IndexerContent}

import scala.xml.XML

object ContentParserIndexerFlow {
  def apply(): ContentParserIndexerFlow = new ContentParserIndexerFlow()
}

class ContentParserIndexerFlow extends ContentIndexerFlow {
  override def getFlow: Flow[(FileMetadata, ByteString), IndexerContent, NotUsed] = {
    Flow[(FileMetadata, ByteString)].map { case (metadata, content) =>
      val contentAsString = getContentFromType(metadata.fileType, content)
      println(s"[INFO] ${metadata.path} => ${content.utf8String.length} (${metadata.fileType}) (${metadata.checksum.get})")
      IndexerContent(metadata, indexFileContent(contentAsString))
    }
  }

  private def getContentFromType(fileType: Option[String], content: ByteString): String = {
    fileType match {
      case Some("text/plain")       => content.utf8String
      case Some("text/markdown")    => content.utf8String
      case Some("application/json") => getContentFromJson(content)
      case Some("text/xml")         => getContentFromXml(content)
      // Add more cases ...
      case _ => content.utf8String
    }
  }

  private def getContentFromJson(content: ByteString): String =
    content.utf8String
      .replaceAll("[{}\\[\\],:]", " ")
      .replaceAll("\"", "")
      .replaceAll("\\s+", " ")
      .trim

  private def getContentFromXml(content: ByteString): String =
    XML
      .loadString(content.utf8String)
      .text
      .trim

  private def indexFileContent(content: String): Map[String, Int] = {
    val tokens = content.toLowerCase
      .split("\\s+")
      .filter(_.nonEmpty)

    tokens.groupBy(identity).view.mapValues(_.length).toMap
  }
}
