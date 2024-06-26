import java.nio.file.{Files, Paths}
import worker.{ExplorerWorker, IndexWorker}
import java.util.concurrent.ConcurrentLinkedQueue
import db.DB
import scala.annotation.static

object Main {

  val DEBUG = sys.env.getOrElse("DEBUG", "0").toInt
  val WORKERS = sys.env.getOrElse("WORKERS", "5").toInt

  def main(args: Array[String]): Unit = {
    if (DEBUG == 1) {
      println(s"[DEBUG] Running in debug mode (level = $DEBUG) with $WORKERS workers")
    }

    val queue = new ConcurrentLinkedQueue[String]()
    val root = if (args.length > 0) args(0) else "."
    if (!Files.exists(Paths.get(root))) {
      println(s"[ERROR] Directory $root does not exist")
      return
    }
    println(s"[INFO] Root directory: $root")
    val explorerWorker = new ExplorerWorker(root, queue, DEBUG)
    val indexers = (1 to WORKERS).map(_ => new IndexWorker(queue, DEBUG))

    val startTimestamp = System.currentTimeMillis()
    explorerWorker.start()
    Thread.sleep(500) // Wait for explorer worker to add some files to the queue
    indexers.foreach(_.start())

    explorerWorker.join()
    indexers.foreach(_.join())

    val endTimestamp = System.currentTimeMillis()
    val duration = endTimestamp - startTimestamp
    println(s"[INFO] Indexing completed in $duration ms")

    val dbName = if (args.length > 1) args(1) else "indexer.db"
    val dbPath = Paths.get(dbName)
    println(s"[INFO] SQLite database path: $dbPath")
    val db = new DB(dbPath)
    try {
      db.createTable
      if (DEBUG >= 2) {
        println("[DEBUG] Resetting database table")
      }
      db.resetTable
      if (DEBUG >= 2) {
        println("[DEBUG] Inserting file metadata into database")
      }
      db.insertFileMetadataBatch(indexers.flatMap(_.getMetadata).toList)
    } catch {
      case e: Exception => println(s"[ERROR] ${e.getMessage}")
    } finally {
      if (DEBUG >= 2) {
        println("[DEBUG] Closing database connection")
      }
      db.close
    }
    println(s"[INFO] Indexed directory: $root (recursive) to $dbPath")
  }

}
