package worker

import java.util.concurrent.ConcurrentLinkedQueue
import java.io.File

class ExplorerWorker(rootPath: String, queue: ConcurrentLinkedQueue[String], debug: Int) extends Thread {

  val root: String = rootPath
  val queueRef: ConcurrentLinkedQueue[String] = queue

  def explore(path: String): Unit = {
    try {
      val files = new File(path).listFiles
      files.foreach(file => {
        if (file.isDirectory) {
          explore(file.getAbsolutePath)
        } else {
          queueRef.add(file.getAbsolutePath)
        }
      })
    } catch {
      case e: Exception => println(s"[ERROR] ${e.getMessage}")
    }
  }

  override def run(): Unit = {
    if (debug >= 1) {
      println(s"[DEBUG] Explorer Worker thread is running with root path: $root")
    }
    explore(root)
    if (debug >= 1) {
      println("[DEBUG] Explorer Worker thread is finished")
    }
  }
}
