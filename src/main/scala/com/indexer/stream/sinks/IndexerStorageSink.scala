package com.indexer.stream.sinks

import akka.Done
import akka.stream.scaladsl.Sink
import com.indexer.models.IndexerContent
import com.indexer.services.storage.IndexerContentService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object IndexerStorageSink {
  private lazy val batchSize = 20
  def apply(indexerContentService: IndexerContentService): IndexerStorageSink =
    new IndexerStorageSink(indexerContentService, batchSize)
}

class IndexerStorageSink(indexerContentService: IndexerContentService, batchSize: Int) extends IndexerSink {
  override def getSink: Sink[IndexerContent, Future[Done]] = Sink
    .fold(List.empty[IndexerContent]) { (acc: List[IndexerContent], content: IndexerContent) =>
      if (acc.size < batchSize) acc :+ content
      else {
        indexerContentService.saveBatch(acc).onComplete {
          case Success(_)         => println(s"[INFO] Batch saved: $acc")
          case Failure(exception) => println(s"[ERROR] Error saving batch: $exception")
        }
        List(content)
      }
    }
    .mapMaterializedValue { future =>
      future.onComplete {
        case scala.util.Success(acc) if acc.nonEmpty =>
          indexerContentService.saveBatch(acc).onComplete {
            case Success(_)         => println(s"[INFO] Last batch saved: $acc")
            case Failure(exception) => println(s"[ERROR] Error saving last batch: $exception")
          }
        case _ => println("[INFO] Sink ended successfully.")
      }
      future.map(_ => Done)
    }
}
