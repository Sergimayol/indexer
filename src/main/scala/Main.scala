import java.nio.file.{Files, Paths}
import worker.{ExplorerWorker, IndexWorker}
import java.util.concurrent.ConcurrentLinkedQueue

object Main {

  def main(args: Array[String]): Unit = {
    val queue = new ConcurrentLinkedQueue[String]()

    val explorerWorker = new ExplorerWorker(".", queue)
    val indexers = (1 to 5).map(_ => new IndexWorker(queue))

    explorerWorker.start()
    // Wait for explorer worker to add some files to the queue
    Thread.sleep(500)
    indexers.foreach(_.start())

    explorerWorker.join()
    indexers.foreach(_.join())

    // TODO: Improve word count aggregation
    val wordCount = indexers.map(_.getMap).reduce { (a, b) =>
      a ++ b.map { case (k, v) => k -> (v + a.getOrElse(k, 0)) }
    }

    println(s"Word count: ${wordCount.size}")
  }

}
