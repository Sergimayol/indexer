package worker

import java.nio.file.{Files, Paths}
import java.util.concurrent.ConcurrentLinkedQueue
import scala.jdk.CollectionConverters._
import scala.collection.mutable.{Buffer, ListBuffer}
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.NoSuchFileException

import file.FileIndexerMetadata

class IndexWorker(queue: ConcurrentLinkedQueue[String]) extends Thread {

  val queueRef: ConcurrentLinkedQueue[String] = queue
  val files = ListBuffer[FileIndexerMetadata]()
  val isWin: Boolean = System.getProperty("os.name").toLowerCase().contains("win")

  def readFile(file: String): Buffer[String] = {
    try {
      return Files.readAllLines(Paths.get(file)).asScala
    } catch {
      case fileNotFound: NoSuchFileException => println(s"[ERROR] File not found: $file")
      // Thrown when the files is not readable (.zip, .tar, etc.)
      case e: Exception => println(s"[ERROR] Error reading file: $file")
    }
    return Buffer()
  }

  def getFileMetadata(file: String): FileIndexerMetadata = {
    try {
      val fileAttributes = Files.readAttributes(Paths.get(file), classOf[BasicFileAttributes])
      val lines = readFile(file).size
      return new FileIndexerMetadata().from_file_attributes(fileAttributes, file, lines, isWin)
    } catch {
      case e: Exception => println(s"[ERROR] Error reading file attributes: $file")
    }
    return null;
  }

  def index: Unit = {
    val file = queueRef.poll()
    println(s"[INFO] Indexing file: $file")
    val metadata = getFileMetadata(file)
    if (metadata != null) {
      this.files += metadata
    }
  }

  def getMetadata: List[FileIndexerMetadata] = this.files.toList

  override def run: Unit = {
    println("Index Worker thread is running")
    while (!queueRef.isEmpty) { index }
    println("Index Worker thread is finished")
  }

}
