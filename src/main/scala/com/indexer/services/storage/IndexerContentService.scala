package com.indexer.services.storage

import akka.Done
import com.indexer.models.IndexerContent

import scala.concurrent.Future

trait IndexerContentService {
  def save(content: IndexerContent): Future[Done]
  def saveBatch(content: List[IndexerContent]): Future[Done]
}
