package com.indexer.stream.flows

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.indexer.models.FileMetadata

import java.io.File

trait MetadataFileFlow {
  def getFlow: Flow[File, FileMetadata, NotUsed]
}
