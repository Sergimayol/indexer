package com.indexer.models

case class IndexerContent(
    metadata: FileMetadata,
    wordRPI: Map[String, Int]
)
