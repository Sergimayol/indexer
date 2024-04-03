package file

import java.nio.file.attribute.BasicFileAttributes
import scala.compiletime.ops.boolean
import java.sql.Date

class FileIndexerMetadata {

  var size: Long = 0
  var creationTime: Date = null
  var lastAccessTime: Date = null
  var lastModifiedTime: Date = null
  var isDirectory: Boolean = false
  var isRegularFile: Boolean = false
  var isSymbolicLink: Boolean = false
  var isOther: Boolean = false
  var extension: String = ""
  var name: String = ""
  var path: String = ""
  var lines: Long = 0

  def from_file_attributes(fileAttributes: BasicFileAttributes, path: String, lines: Long, isWin: Boolean): FileIndexerMetadata = {
    this.size = fileAttributes.size
    val toDate = (time: Long) => new Date(time)
    this.creationTime = toDate(fileAttributes.creationTime.toMillis)
    this.lastAccessTime = toDate(fileAttributes.lastAccessTime.toMillis)
    this.lastModifiedTime = toDate(fileAttributes.lastModifiedTime.toMillis)
    this.isDirectory = fileAttributes.isDirectory
    this.isRegularFile = fileAttributes.isRegularFile
    this.isSymbolicLink = fileAttributes.isSymbolicLink
    this.isOther = fileAttributes.isOther
    this.name = if (isWin) path.split("\\\\").last else path.split("/").last
    this.path = path
    this.extension = if (this.isRegularFile) {
      val parts = this.name.split("\\.")
      if (parts.length > 1) parts.last else ""
    } else null
    this.lines = lines
    this
  }

  def to_sql(): String = {
    val isDirectory = if (this.isDirectory) 1 else 0
    val isRegularFile = if (this.isRegularFile) 1 else 0
    val isSymbolicLink = if (this.isSymbolicLink) 1 else 0
    val isOther = if (this.isOther) 1 else 0
    s"""
       |(
       |  ${size},
       |  '${creationTime.getTime}',
       |  '${lastAccessTime.getTime}',
       |  '${lastModifiedTime.getTime}',
       |  ${isDirectory},
       |  ${isRegularFile},
       |  ${isSymbolicLink},
       |  ${isOther},
       |  '${name}',
       |  '${path}',
       |  '${extension}',
       |  ${lines}
       |)
       |""".stripMargin
  }

}
