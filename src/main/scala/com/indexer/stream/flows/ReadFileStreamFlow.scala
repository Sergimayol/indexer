package com.indexer.stream.flows

import akka.NotUsed
import akka.stream.scaladsl.{FileIO, Flow}
import akka.util.ByteString
import com.indexer.models.FileMetadata

object ReadFileStreamFlow {
  def apply(): ReadFileStreamFlow = new ReadFileStreamFlow()
}

class ReadFileStreamFlow extends ReadFileFlow {
  override def getFlow: Flow[FileMetadata, (FileMetadata, ByteString), NotUsed] =
    Flow[FileMetadata].flatMapConcat(metadata =>
      FileIO
        .fromPath(metadata.path)
        .fold(ByteString.empty)(_ ++ _)
        .map(byteString => (metadata, byteString))
    )
}
