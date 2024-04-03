import java.nio.file.{Files, Paths}
import worker.{ExplorerWorker, IndexWorker}
import java.util.concurrent.ConcurrentLinkedQueue
import db.DB

object Main {

  def main(args: Array[String]): Unit = {
    val queue = new ConcurrentLinkedQueue[String]()

    val explorerWorker = new ExplorerWorker(".", queue)
    val indexers = (1 to 5).map(_ => new IndexWorker(queue))

    explorerWorker.start()
    Thread.sleep(500) // Wait for explorer worker to add some files to the queue
    indexers.foreach(_.start())

    explorerWorker.join()
    indexers.foreach(_.join())

    val dbPath = Paths.get("indexer.db")
    val db = new DB(dbPath)
    try {
      db.createTable
      println("[INFO] Resetting database table")
      db.resetTable
      println("[INFO] Inserting file metadata into database")
      db.insertFileMetadataBatch(indexers.flatMap(_.getMetadata).toList)
    } catch {
      case e: Exception => println(s"[ERROR] ${e.getMessage}")
    } finally {
      println("[INFO] Closing database connection")
      db.close
    }
  }

}
