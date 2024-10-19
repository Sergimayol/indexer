# Indexer

Indexer is a Scala program that allows to get all the file metadata and map it to a SQLite database from a given directory.

## Requirements

- Scala 3
- sbt
- SQLite

## Usage

1. Clone the repository

```bash
git clone https://github.com/Sergimayol/indexer.git
```

2. Configure the `application.conf` settings as you want, by default:

```hocon
sqlite {
  url = "jdbc:sqlite:.indexer.db"
  driver = "org.sqlite.JDBC"
  connectionPool = disabled
  keepAliveConnection = true
}

indexer {
  initialPath = "./"
  insertBatchSize = 100
}
```

3. Run the project

```bash
sbt run
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
