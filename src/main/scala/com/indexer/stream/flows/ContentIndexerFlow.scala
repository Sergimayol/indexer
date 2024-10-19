package com.indexer.stream.flows

import akka.NotUsed
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import com.indexer.models.{FileMetadata, IndexerContent}

trait ContentIndexerFlow {
  def getFlow: Flow[(FileMetadata, ByteString), IndexerContent, NotUsed]
}
