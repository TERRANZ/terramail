# Terramail - Desktop Email Client

A modern desktop email client built with Java 21 and JavaFX, using MySQL for local storage, and supporting IMAP/SMTP protocols for email synchronization.

## Features

- **Multi-account Support**: Manage multiple email accounts in one place
- **IMAP Synchronization**: Automatically sync emails from your mail server
- **SMTP Sending**: Send emails with support for attachments
- **Local Storage**: All emails stored locally in MySQL for fast access
- **Folder Management**: Browse and manage email folders
- **Message Viewer**: View emails with HTML support
- **Search**: Search through your emails by subject
- **Flag & Read Status**: Track which emails you've read and flagged

## Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Java 21 |
| Build System | Gradle (Kotlin DSL) |
| UI Framework | JavaFX 21+ |
| Database | MySQL 8.x |
| Email Protocols | JavaMail API (IMAP/SMTP) |
| Logging | SLF4J + Logback |

## Prerequisites

- Java 21 or later
- MySQL 8.x server
- Gradle 8.x (included via wrapper)

## Project Structure

```
terramail/
├── build.gradle.kts          # Gradle build configuration
├── settings.gradle.kts       # Gradle settings
├── gradle/                   # Gradle wrapper
├── src/
│   ├── main/
│   │   ├── java/com/terramail/
│   │   │   ├── TerramailApp.java       # Application entry point
│   │   │   ├── controller/             # JavaFX controllers
│   │   │   ├── view/                   # JavaFX view components
│   │   │   ├── service/                # Business logic services
│   │   │   ├── repository/             # Data access layer
│   │   │   ├── model/                  # Entity classes
│   │   │   ├── mail/                   # IMAP/SMTP handlers
│   │   │   ├── util/                   # Utility classes
│   │   │   └── config/                 # Configuration classes
│   │   └── resources/
│   │       ├── fxml/                   # JavaFX FXML files
│   │       ├── styles/                 # CSS stylesheets
│   │       ├── application.properties  # Application configuration
│   │       └── logback.xml            # Logging configuration
│   └── test/                           # Unit tests
├── sql/
│   └── schema.sql                      # Database schema
└── gradlew                             # Gradle wrapper script
```

## Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd terramail
```

### 2. Set Up MySQL Database

Create the database and tables:

```bash
mysql -u root -p < sql/schema.sql
```

### 3. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/terramail?useSSL=false&serverTimezone=UTC
db.username=root
db.password=your_password
```

### 4. Build the Application

```bash
./gradlew build
```

### 5. Run the Application

```bash
./gradlew run
```

## Building

### Build JAR

```bash
./gradlew build
```

### Create Executable JAR with Dependencies

```bash
./gradlew shadowJar
```

The executable JAR will be in `build/libs/`.

### Run Tests

```bash
./gradlew test
```

## Configuration

### Application Properties

| Property | Default | Description |
|----------|---------|-------------|
| `db.url` | `jdbc:mysql://localhost:3306/terramail` | Database connection URL |
| `db.username` | `root` | Database username |
| `db.password` | (empty) | Database password |
| `db.pool.size` | `10` | Connection pool size |
| `imap.default.port` | `993` | Default IMAP port |
| `smtp.default.port` | `587` | Default SMTP port |
| `sync.interval.seconds` | `300` | Sync interval in seconds |

## Usage

### Adding an Email Account

1. Click the "Settings" button in the main window
2. Go to the "Accounts" tab
3. Click "Add Account"
4. Fill in your email account details:
   - Display name
   - Email address
   - IMAP server settings
   - SMTP server settings
5. Click "Test Connection" to verify settings
6. Click "Save"

### Browsing Folders

- Folders are displayed in a tree view on the left panel
- Click on a folder to view its messages
- Double-click an account to expand/collapse its folders

### Reading Emails

- Click on a message in the table to view it
- Double-click to mark as read
- HTML emails are rendered in the viewer

### Sending Emails

- Click the "Compose" button (or use Ctrl+N)
- Fill in recipient, subject, and message body
- Attach files if needed
- Click "Send"

### Synchronization

- Emails are automatically synchronized at regular intervals
- Click the "Sync" button to manually sync
- Initial sync occurs on startup

## Database Schema

The application uses the following tables:

- **ACCOUNT**: Email account configuration
- **FOLDER**: Email folders per account
- **MESSAGE**: Email messages
- **RECIPIENT**: Email recipients (To, Cc, Bcc)
- **ATTACHMENT**: Email attachments

See [`sql/schema.sql`](sql/schema.sql) for the complete schema.

## Development

### Running in Development Mode

```bash
./gradlew run
```

### Code Style

This project follows standard Java conventions. Run the formatter before submitting:

```bash
./gradlew format
```

### Adding New Features

1. Create a new issue or pick an existing one
2. Create a feature branch
3. Implement your changes
4. Write tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- JavaFX for the UI framework
- MySQL for the database
- Eclipse JavaMail for email protocol support
