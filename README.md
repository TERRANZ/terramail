# Terramail

A desktop email client built with Java 21 and Swing, featuring local database storage, offline mode support, and a modern three-pane interface.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
  - [Project Structure](#project-structure)
  - [Layered Design](#layered-design)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Database Schema](#database-schema)
- [Core Components](#core-components)
  - [Models](#models)
  - [Services](#services)
  - [Repositories](#repositories)
  - [UI Panels](#ui-panels)
- [Workflow](#workflow)
- [Testing](#testing)
- [Build & Run](#build--run)

---

## Overview

**Terramail** is a lightweight, self-contained desktop email client that bridges IMAP/SMTP mail servers with a local MySQL database. It provides a familiar email client experience with folder navigation, message listing, content viewing, composition, and synchronization -- all running on Java Swing with a clean, split-pane layout.

The application is designed for users who want full control over their email data by storing messages locally, enabling offline access, and supporting queued message sending when connectivity is restored.

---

## Features

| Feature | Description |
|---|---|
| **IMAP Email Fetching** | Retrieves messages from any IMAP-compatible mail server, including body extraction (plain text and HTML) and attachment metadata. |
| **SMTP Email Sending** | Sends emails via SMTP with authentication support. |
| **Local Database Storage** | Persists all messages, folders, and account settings in a MySQL database using HikariCP connection pooling. |
| **Folder Management** | Auto-discovers and syncs mail server folders (Inbox, Sent, Drafts, Trash, and custom folders). |
| **Three-Pane UI** | Classic email client layout: folder tree (left), message list (top-right), message content (bottom-right). |
| **Sorting** | Sort messages by date, subject, from, or to in ascending or descending order. |
| **Offline Mode** | Toggle online/offline mode. Composed messages are queued locally and sent automatically when the user goes back online and triggers a sync. |
| **Attachment Handling** | Downloads and stores email attachments in a local `attachments/` directory. |
| **Settings Management** | Configurable IMAP/SMTP hosts, ports, SSL options, and database connection details via a dedicated Settings dialog. |
| **Sync Progress** | Real-time status updates during folder and message synchronization. |

---

## Architecture

### Project Structure

```
terramail/
├── build.gradle                          # Gradle build configuration
├── settings.gradle                       # Project settings
├── gradlew / gradlew.bat                 # Gradle wrapper
├── attachments/                          # Local attachment storage directory
├── gradle/wrapper/                       # Gradle wrapper JAR and properties
└── src/
    ├── main/
    │   └── java/com/terramail/
    │       ├── app/
    │       │   └── TerramailApp.java     # Application entry point
    │       ├── model/                    # Domain models
    │       │   ├── AccountSettings.java
    │       │   ├── AttachmentInfo.java
    │       │   ├── Folder.java
    │       │   ├── Message.java
    │       │   └── SortOrder.java
    │       ├── repository/               # Data access layer
    │       │   ├── AccountSettingsRepository.java
    │       │   ├── AccountSettingsRepositoryImpl.java
    │       │   ├── FolderRepository.java
    │       │   ├── FolderRepositoryImpl.java
    │       │   ├── MessageRepository.java
    │       │   └── MessageRepositoryImpl.java
    │       ├── service/                  # Business logic layer
    │       │   ├── AppState.java
    │       │   ├── AttachmentService.java
    │       │   ├── DatabaseService.java
    │       │   └── EmailService.java
    │       ├── ui/
    │       │   ├── main/                 # Main window panels
    │       │   │   ├── FolderTreePanel.java
    │       │   │   ├── MainFrame.java
    │       │   │   ├── MessageContentPanel.java
    │       │   │   └── MessageTablePanel.java
    │       │   ├── model/                # Table/tree models
    │       │   │   ├── FolderTreeModel.java
    │       │   │   └── MessageTableModel.java
    │       │   └── panels/               # Dialog panels
    │       │       ├── ComposePanel.java
    │       │   │   ├── SettingsPanel.java
    │       │   │   └── SyncStatusPanel.java
    │       │   └── util/
    │       │       ├── ConfigManager.java
    │       │       └── MessageFormatter.java
    └── test/
        └── java/com/terramail/
            ├── model/                    # Model unit tests
            ├── repository/               # Repository unit tests
            ├── service/                  # Service unit tests
            ├── ui/model/                 # UI model unit tests
            └── util/                     # Utility unit tests
```

### Layered Design

Terramail follows a clean **three-layer architecture**:

1. **UI Layer** (`com.terramail.ui.*`) -- Swing-based user interface handling user interactions, displaying data, and capturing input.
2. **Service Layer** (`com.terramail.service.*`) -- Business logic including email protocol operations (IMAP/SMTP), database management, attachment handling, and application state management.
3. **Repository Layer** (`com.terramail.repository.*`) -- Data access objects (DAOs) that interact with the MySQL database using JDBC.
4. **Model Layer** (`com.terramail.model.*`) -- Plain domain objects (POJOs) representing entities like `Message`, `Folder`, `AccountSettings`, etc.

Data flows: **IMAP Server -> EmailService -> MessageRepository -> MessageTableModel -> UI**

---

## Technologies Used

| Technology | Purpose | Version |
|---|---|---|
| **Java** | Programming language | 21 |
| **Swing** | GUI framework | JDK built-in |
| **Jakarta Mail API** | Email protocol support (IMAP/SMTP) | 2.1.3 |
| **Angus Jakarta Mail** | Implementation of Jakarta Mail | 2.0.4 |
| **HikariCP** | High-performance JDBC connection pool | 5.1.0 |
| **MySQL Connector/J** | MySQL database driver | 8.3.0 |
| **JUnit Jupiter** | Unit testing framework | 5.10.2 |
| **Mockito** | Mocking framework | 5.11.0 |
| **H2** | In-memory database for testing | 2.2.224 |
| **Gradle** | Build automation tool | -- |

---

## Prerequisites

- **JDK 21** or later (Amazon Corretto, OpenJDK, or Oracle JDK)
- **MySQL Server** running and accessible (for local data storage)
- **Gradle 8+** (or use the included Gradle wrapper)
- An **IMAP/SMTP email account** (Gmail, Outlook, corporate email, etc.)

---

## Installation

### 1. Clone or Download the Project

```bash
cd terramail
```

### 2. Ensure MySQL is Running

Create the database and user:

```sql
CREATE DATABASE terramail;
CREATE USER 'terramail'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON terramail.* TO 'terramail'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Build the Project

```bash
./gradlew build
```

This compiles all source files, runs tests, and packages the application.

---

## Configuration

### Account Settings

On first launch, Terramail creates a default account with these settings:

| Setting | Default Value | Description |
|---|---|---|
| Account Name | `default` | Identifier for this email account |
| IMAP Host | `localhost` | IMAP server address |
| IMAP Port | `143` | IMAP port (use `993` for SSL) |
| IMAP User | *(empty)* | Email username for IMAP |
| IMAP Password | *(empty)* | Email password/app password for IMAP |
| IMAP SSL | `false` | Enable SSL/TLS for IMAP |
| SMTP Host | `localhost` | SMTP server address |
| SMTP Port | `25` | SMTP port (use `465` or `587` for SSL) |
| SMTP User | *(empty)* | Email username for SMTP |
| SMTP Password | *(empty)* | Email password/app password for SMTP |
| SMTP SSL | `false` | Enable SSL/TLS for SMTP |
| Database URL | `jdbc:mysql://192.168.1.3:3306/terramail` | MySQL connection string |
| Database User | `terramail` | MySQL username |
| Database Password | `123` | MySQL password |

### Updating Settings

1. Open the application.
2. Go to **File -> Settings** from the menu bar.
3. Update IMAP/SMTP and database connection details.
4. Click **Save** to persist the changes.

### Popular Email Provider Settings

| Provider | IMAP Host | IMAP Port | SSL | SMTP Host | SMTP Port | SSL |
|---|---|---|---|---|---|---|
| Gmail | imap.gmail.com | 993 | Yes | smtp.gmail.com | 587 | Yes |
| Outlook | imap-mail.outlook.com | 993 | Yes | smtp-mail.outlook.com | 587 | Yes |
| Yahoo | imap.mail.yahoo.com | 993 | Yes | smtp.mail.yahoo.com | 587 | Yes |

> **Note:** For Gmail and other providers requiring "App Passwords", generate one from your account security settings and use it instead of your regular password.

---

## Usage

### Launching the Application

```bash
./gradlew runApp
```

Or run the main class directly:

```bash
java -cp build/libs/terramail-1.0.0.jar com.terramail.app.TerramailApp
```

### Main Interface

The main window consists of three panels:

1. **Folder Tree (Left)** -- Displays all synced mail folders in a tree structure. Click a folder to view its messages.
2. **Message List (Top Right)** -- Shows messages in the selected folder with columns for sender, subject, date, and read status. Click a message to view its content.
3. **Message Content (Bottom Right)** -- Displays the full body of the selected message with attachment information.

### Menu Bar

- **File**
  - **Settings** -- Open account configuration dialog.
  - **Compose** -- Open the email composition window.
  - **Exit** -- Close the application.
- **View**
  - **Online Mode** -- Toggle between online and offline modes.
  - **Sync** -- Trigger synchronization with the mail server.
  - **Load Folders** -- Discover and add new folders from the mail server.

### Synchronization

1. Click **View -> Sync** (or use the sync button in the status bar).
2. The application iterates through all folders, fetching messages from the IMAP server.
3. Progress is displayed in the status bar at the bottom.
4. After sync, any queued (offline) messages are automatically sent.

### Composing Messages

1. Click **File -> Compose**.
2. Fill in recipient (To), CC (optional), subject, and message body.
3. Click **Send**.
   - If **online**: the message is sent immediately via SMTP.
   - If **offline**: the message is queued and will be sent on the next sync.

### Sorting Messages

Click the column headers in the message list to sort by that field. Click again to toggle between ascending and descending order.

---

## Database Schema

Terramail uses MySQL with three tables automatically created on startup:

### `account_settings`

Stores email account configuration.

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT AUTO_INCREMENT | Primary key |
| `account_name` | VARCHAR(255) | Account identifier |
| `imap_host` | VARCHAR(255) | IMAP server hostname |
| `imap_port` | INT | IMAP server port |
| `imap_user` | VARCHAR(255) | IMAP username |
| `imap_password` | VARCHAR(255) | IMAP password |
| `imap_ssl` | BOOLEAN | IMAP SSL enabled |
| `smtp_host` | VARCHAR(255) | SMTP server hostname |
| `smtp_port` | INT | SMTP server port |
| `smtp_user` | VARCHAR(255) | SMTP username |
| `smtp_password` | VARCHAR(255) | SMTP password |
| `smtp_ssl` | BOOLEAN | SMTP SSL enabled |
| `db_url` | VARCHAR(500) | MySQL JDBC URL |
| `db_user` | VARCHAR(255) | MySQL username |
| `db_password` | VARCHAR(255) | MySQL password |

### `folders`

Maps mail server folders to this account.

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT AUTO_INCREMENT | Primary key |
| `account_id` | BIGINT | Foreign key to `account_settings.id` |
| `name` | VARCHAR(255) | Folder name (e.g., "Inbox", "Sent") |
| `type` | VARCHAR(50) | Folder type: `INBOX`, `SENT`, `DRAFTS`, `TRASH`, `CUSTOM` |

### `messages`

Stores fetched email messages.

| Column | Type | Description |
|---|---|---|
| `id` | BIGINT AUTO_INCREMENT | Primary key |
| `folder_id` | BIGINT | Foreign key to `folders.id` |
| `from` | VARCHAR(255) | Sender address |
| `to` | VARCHAR(255) | Recipient addresses |
| `cc` | VARCHAR(255) | CC addresses |
| `subject` | VARCHAR(500) | Email subject |
| `date` | TIMESTAMP | Sent date |
| `body` | TEXT | Email body (plain text) |
| `seen` | BOOLEAN | Read/unread status |
| `flagged` | BOOLEAN | Starred/favorite status |
| `attachments` | TEXT | Attachment metadata (JSON) |

---

## Core Components

### Models

| Model | File | Description |
|---|---|---|
| [`AccountSettings`](src/main/java/com/terramail/model/AccountSettings.java) | `AccountSettings.java` | Holds all email account and database configuration. Serializable. |
| [`Message`](src/main/java/com/terramail/model/Message.java) | `Message.java` | Represents a single email with from, to, cc, subject, date, body, seen/flagged flags, and attachments. |
| [`Folder`](src/main/java/com/terramail/model/Folder.java) | `Folder.java` | Represents a mail folder with type classification (`INBOX`, `SENT`, `DRAFTS`, `TRASH`, `CUSTOM`). |
| [`AttachmentInfo`](src/main/java/com/terramail/model/AttachmentInfo.java) | `AttachmentInfo.java` | Immutable value object holding attachment name, size, and content type. |
| [`SortOrder`](src/main/java/com/terramail/model/SortOrder.java) | `SortOrder.java` | Immutable sort specification with field (`DATE`, `SUBJECT`, `FROM`, `TO`, `CC`) and direction (`ASCENDING`, `DESCENDING`). |

### Services

| Service | File | Description |
|---|---|---|
| [`EmailService`](src/main/java/com/terramail/service/EmailService.java) | `EmailService.java` | Core email protocol handler. Connects to IMAP to fetch messages and to SMTP to send messages. Handles body extraction (plain text and HTML-to-plain), attachment extraction, and folder listing. |
| [`DatabaseService`](src/main/java/com/terramail/service/DatabaseService.java) | `DatabaseService.java` | Manages the HikariCP connection pool and creates the database schema tables on startup. |
| [`AttachmentService`](src/main/java/com/terramail/service/AttachmentService.java) | `AttachmentService.java` | Handles saving email attachments to the local `attachments/` directory. |
| [`AppState`](src/main/java/com/terramail/service/AppState.java) | `AppState.java` | Manages application-wide state: online/offline mode, active folder, sync status, and queued messages. |

### Repositories

| Repository | File | Description |
|---|---|---|
| [`MessageRepository`](src/main/java/com/terramail/repository/MessageRepository.java) | `MessageRepository.java` | CRUD operations for messages: save, find by folder with sort, update seen status. |
| [`FolderRepository`](src/main/java/com/terramail/repository/FolderRepository.java) | `FolderRepository.java` | Folder operations: save, find by account, find by name. |
| [`AccountSettingsRepository`](src/main/java/com/terramail/repository/AccountSettingsRepository.java) | `AccountSettingsRepository.java` | Account settings operations: save, find by account name. |

### UI Panels

| Panel | File | Description |
|---|---|---|
| [`MainFrame`](src/main/java/com/terramail/ui/main/MainFrame.java) | `MainFrame.java` | Main application window. Orchestrates all sub-panels, menu actions, sync operations, and dialog management. |
| [`FolderTreePanel`](src/main/java/com/terramail/ui/main/FolderTreePanel.java) | `FolderTreePanel.java` | Left sidebar showing the folder tree using `JTree`. |
| [`MessageTablePanel`](src/main/java/com/terramail/ui/main/MessageTablePanel.java) | `MessageTablePanel.java` | Message list using `JTable` with sortable columns. |
| [`MessageContentPanel`](src/main/java/com/terramail/ui/main/MessageContentPanel.java) | `MessageContentPanel.java` | Message body viewer with attachment display. |
| [`ComposePanel`](src/main/java/com/terramail/ui/panels/ComposePanel.java) | `ComposePanel.java` | Email composition form (To, CC, Subject, Body fields + Send button). |
| [`SettingsPanel`](src/main/java/com/terramail/ui/panels/SettingsPanel.java) | `SettingsPanel.java` | Account configuration form with all IMAP/SMTP/DB fields. |
| [`SyncStatusPanel`](src/main/java/com/terramail/ui/panels/SyncStatusPanel.java) | `SyncStatusPanel.java` | Status bar showing sync progress messages. |

### UI Models

| Model | File | Description |
|---|---|---|
| [`MessageTableModel`](src/main/java/com/terramail/ui/model/MessageTableModel.java) | `MessageTableModel.java` | `TableModel` for the message list. Handles data binding, sorting, and row updates. |
| [`FolderTreeModel`](src/main/java/com/terramail/ui/model/FolderTreeModel.java) | `FolderTreeModel.java` | `TreeModel` for the folder tree. Groups folders by type. |

---

## Workflow

### Message Fetching (Sync) Flow

```
User clicks "Sync"
    |
    v
MainFrame.triggerSync()
    |
    v
EmailService.fetchMessages(folder)
    |
    +-- Connects to IMAP server
    +-- Opens folder (READ_ONLY)
    +-- Iterates through messages
    +-- Extracts body (plain text / HTML)
    +-- Extracts attachment metadata
    +-- Returns List<Message>
    |
    v
MessageRepository.save(message)
    |
    +-- Inserts/updates in MySQL
    |
    v
AppState.setSyncStatus("Synced {folder}")
```

### Message Sending Flow

```
User composes message in ComposePanel
    |
    v
User clicks "Send"
    |
    +-- If Online: EmailService.sendMessage() -> SMTP Transport.send()
    |
    +-- If Offline: AppState.addQueuedMessage() -> stored for later
    |
    v
On next Sync: queued messages are sent automatically
```

### Folder Loading Flow

```
User clicks "Load Folders"
    |
    v
EmailService.listAvailableFolders()
    |
    +-- Connects to IMAP
    +-- Lists all folders from DefaultFolder
    +-- Detects folder type (INBOX, SENT, DRAFTS, TRASH, CUSTOM)
    |
    v
FolderRepository.save() for each new folder
    |
    v
FolderTreeModel.setFolders() updates the UI tree
```

---

## Testing

Terramail includes comprehensive unit tests for all core components:

```bash
./gradlew test
```

### Test Coverage

| Package | Tests |
|---|---|
| `com.terramail.model` | [`AccountSettingsTest`](src/test/java/com/terramail/model/AccountSettingsTest.java), [`AttachmentInfoTest`](src/test/java/com/terramail/model/AttachmentInfoTest.java), [`FolderTest`](src/test/java/com/terramail/model/FolderTest.java), [`MessageTest`](src/test/java/com/terramail/model/MessageTest.java), [`SortOrderTest`](src/test/java/com/terramail/model/SortOrderTest.java) |
| `com.terramail.repository` | [`FolderRepositoryImplTest`](src/test/java/com/terramail/repository/FolderRepositoryImplTest.java), [`MessageRepositoryImplTest`](src/test/java/com/terramail/repository/MessageRepositoryImplTest.java), [`TestDatabaseUtil`](src/test/java/com/terramail/repository/TestDatabaseUtil.java) |
| `com.terramail.service` | [`AppStateTest`](src/test/java/com/terramail/service/AppStateTest.java), [`AttachmentServiceTest`](src/test/java/com/terramail/service/AttachmentServiceTest.java), [`DatabaseServiceTest`](src/test/java/com/terramail/service/DatabaseServiceTest.java), [`EmailServiceTest`](src/test/java/com/terramail/service/EmailServiceTest.java) |
| `com.terramail.ui.model` | [`FolderTreeModelTest`](src/test/java/com/terramail/ui/model/FolderTreeModelTest.java), [`MessageTableModelTest`](src/test/java/com/terramail/ui/model/MessageTableModelTest.java) |
| `com.terramail.util` | [`MessageFormatterTest`](src/test/java/com/terramail/util/MessageFormatterTest.java) |

Tests use **H2 in-memory database** for isolated database testing and **Mockito** for mocking external dependencies.

---

## Build & Run

### Gradle Commands

| Command | Description |
|---|---|
| `./gradlew build` | Compile, test, and package |
| `./gradlew test` | Run all unit tests |
| `./gradlew runApp` | Launch the application |
| `./gradlew clean` | Remove build directory |
| `./gradlew clean build` | Full clean rebuild |

### Running with Custom Classpath

```bash
./gradlew build -x test
java -cp "build/libs/*:build/classes/java/main" com.terramail.app.TerramailApp
```

---

## License

This project is provided as-is for educational and personal use.

---

## Author

Built with Java 21, Swing, Jakarta Mail, and MySQL.
