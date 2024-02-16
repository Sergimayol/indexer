package worker

import java.nio.file.{Files, Paths}
import java.util.concurrent.ConcurrentLinkedQueue
import scala.jdk.CollectionConverters._
import scala.collection.mutable.Buffer

class IndexWorker(queue: ConcurrentLinkedQueue[String]) extends Thread {

  val queueRef: ConcurrentLinkedQueue[String] = queue
  var wordCount: Map[String, Int] = Map()

  def readFile(file: String): Buffer[String] = {
    try {
      val lines = Files.readAllLines(Paths.get(file)).asScala
      return lines
    } catch {
      case fileNotFound: java.nio.file.NoSuchFileException =>
        println(s"[ERROR] File not found: $file")
      // Thrown when the files is not readable (.zip, .tar, etc.)
      case e: Exception => println(s"[ERROR] Error reading file: $file")
    }
    return Buffer()
  }

  def index: Unit = {
    val file = queueRef.poll()
    println(s"[INFO] Indexing file: $file")
    val lines = readFile(file)
    if (lines.isEmpty) return
    // TODO: Improve word tokenization
    val wordCount = lines
      .flatMap(_.split(" "))
      .map(_.toLowerCase)
      .filter(_.nonEmpty)
      .groupBy(identity)
      .mapValues(_.size)

    println(s"[INFO] Word count for file: $file is ${wordCount.size}")

    // TODO: Store the word count taken in count the file name and the word count
    this.wordCount = this.wordCount ++ wordCount.map { case (k, v) =>
      k -> (v + this.wordCount.getOrElse(k, 1))
    }
  }

  def getMap: Map[String, Int] = wordCount

  override def run: Unit = {
    println("Index Worker thread is running")
    while (!queueRef.isEmpty) { index }
    println("Index Worker thread is finished")
    println(s"[INFO] Word count: ${wordCount.size}")
  }

}
