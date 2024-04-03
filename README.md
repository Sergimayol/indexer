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

2. Compile the project

```bash
sbt clean assembly
```

3. Run the project

```bash
java -jar target/scala-3.x.x/indexer.jar <directory> <database>
```

> [!NOTE]
> The directory and database args are optional. If not provided, the program will use the current directory.

## Example

```bash
java -jar target/scala-3.3.1/indexer.jar /home/user/Documents /home/user/Documents/database.db
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
