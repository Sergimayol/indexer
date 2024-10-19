package com.indexer.services.worker

import java.io.File

object FileExplorer {
  def listFiles(dir: File): List[File] = {
    if (dir.exists && dir.isDirectory) {
      val files = dir.listFiles.filter(_.isFile).toList
      val subDirs = dir.listFiles.filter(_.isDirectory)
      files ++ subDirs.flatMap(subDir => listFiles(subDir))
    } else {
      List.empty[File]
    }
  }
}
