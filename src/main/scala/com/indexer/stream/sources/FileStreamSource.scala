package com.indexer.stream.sources

import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import com.indexer.services.worker.FileExplorer

import java.io.File
import scala.concurrent.Future

object FileStreamSource {
  def apply(baseDir: String): FileStreamSource = new FileStreamSource(baseDir)
}

class FileStreamSource(baseDir: String) extends FileSource {
  private val fileIndexer = FileExplorer

  override def getSource: Source[File, Future[Done]] = {
    val fileIterator = () => fileIndexer.listFiles(new File(baseDir)).iterator
    val fileSource: Source[File, NotUsed] = Source.fromIterator(fileIterator)
    fileSource.watchTermination() { (_, done) => done }
  }
}
