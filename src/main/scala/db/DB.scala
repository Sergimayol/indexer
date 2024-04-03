package db

import java.sql.{Connection, Statement}
import java.nio.file.Path
import java.sql.DriverManager
import file.FileIndexerMetadata

class DB(db_path: Path) {
  val connection = DriverManager.getConnection(s"jdbc:sqlite:${db_path.toAbsolutePath.toString}")

  def createTable: Unit = {
    val statement = connection.createStatement()

    statement.execute(
      """
          |CREATE TABLE IF NOT EXISTS file_metadata (
          |  id INTEGER PRIMARY KEY AUTOINCREMENT,
          |  size INTEGER,
          |  creation_time INTEGER,
          |  last_access_time INTEGER,
          |  last_modified_time INTEGER,
          |  is_directory BOOLEAN,
          |  is_regular_file BOOLEAN,
          |  is_symbolic_link BOOLEAN,
          |  is_other BOOLEAN,
          |  name TEXT,
          |  path TEXT,
          |  extension TEXT,
          |  lines INTEGER
          |);
          |""".stripMargin
    )
  }

  def resetTable: Unit = {
    val statement = connection.createStatement()
    statement.execute("DELETE FROM file_metadata;")
  }

  def insertFileMetadata(metadata: FileIndexerMetadata): Unit = {
    val statement = connection.createStatement()
    val q = s"""
        |INSERT INTO file_metadata (
        |  size,
        |  creation_time,
        |  last_access_time,
        |  last_modified_time,
        |  is_directory,
        |  is_regular_file,
        |  is_symbolic_link,
        |  is_other,
        |  name,
        |  path,
        |  extension,
        |  lines
        |) VALUES 
        |${metadata.to_sql()}
        |;
        """.stripMargin

    statement.execute(q)
  }

  def insertFileMetadataBatch(metadata: List[FileIndexerMetadata]): Unit = {
    val statement = connection.createStatement()
    val q = s"""
        |INSERT INTO file_metadata (
        |  size,
        |  creation_time,
        |  last_access_time,
        |  last_modified_time,
        |  is_directory,
        |  is_regular_file,
        |  is_symbolic_link,
        |  is_other,
        |  name,
        |  path,
        |  extension,
        |  lines
        |) VALUES 
        |${metadata.map(_.to_sql()).mkString(",\n").dropRight(1)}
        |;
        """.stripMargin

    statement.execute(q)
  }

  def close: Unit = connection.close
}
