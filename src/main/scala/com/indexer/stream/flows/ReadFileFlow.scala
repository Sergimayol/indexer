package com.indexer.stream.flows

import akka.NotUsed
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import com.indexer.models.FileMetadata

trait ReadFileFlow {
  def getFlow: Flow[FileMetadata, (FileMetadata, ByteString), NotUsed]
}
