# 🤖 My Personal Discord Bot 🤖

This passion project showcases a lightweight Discord bot built with **Java** and **Kotlin** using the [Java Discord API](https://github.com/discord-jda/JDA). The bot uses both languages side by side — Kotlin handles the entry point, slash commands, and interaction handlers, while Java covers the database layer and event routing. It features a slash command architecture with subcommand grouping, structured logging, MySQL database integration via HikariCP, and support for modals, dropdowns, and button interactions.

<!-- Unique Feature of Discord Bot here (should be a GIF file) -->

---

## Requirements

- Java 17+
- Gradle (or use the included `gradlew` wrapper)
- A Discord bot token ([Discord Developer Portal](https://discord.com/developers/applications))
- A running MySQL instance

---

## Setup & Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/MyDiscordBotProject.git
   cd MyDiscordBotProject
   ```

2. **Create a `.env` file** in the project root (see [Configuration](#configuration))

3. **Build the project**
   ```bash
   ./gradlew shadowJar
   ```

4. **Run the bot**
   ```bash
   # Normal startup
   java -jar build/libs/MyDiscordBotProject-v2.0.2.jar

   # Register slash commands (only needed once, or after adding new commands)
   java -jar build/libs/MyDiscordBotProject-v2.0.2.jar --register
   ```

---

## Configuration

Create a `.env` file in the root directory with the following variables:

```env
BOT_API=your_discord_bot_token_here
USER_ID=your_discord_user_id_here
DB_HOST=localhost
DB_PORT=3306
DB_NAME=your_database_name
DB_USER=your_mysql_username
DB_PASSWORD=your_mysql_password
```

| Variable      | Description                                                 |
|---------------|-------------------------------------------------------------|
| `BOT_API`     | Your Discord bot token from the Developer Portal            |
| `USER_ID`     | Your Discord user ID — grants access to owner-only commands |
| `DB_HOST`     | MySQL server host (e.g. `localhost`)                        |
| `DB_PORT`     | MySQL server port (default: `3306`)                         |
| `DB_NAME`     | Name of the database to connect to                          |
| `DB_USER`     | MySQL username                                              |
| `DB_PASSWORD` | MySQL password                                              |

> Note: Never commit your `.env` file. It is already listed in `.gitignore`.

---

## Database Setup

The bot uses MySQL with HikariCP for connection pooling. Ensure your MySQL instance is running and the database exists before starting the bot. The schema requires the following tables:

```sql
CREATE TABLE users (
    user_id   VARCHAR(32)  PRIMARY KEY,
    username  VARCHAR(100) NOT NULL
);

CREATE TABLE task_lists (
    list_id   BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id   VARCHAR(32)  NOT NULL,
    list_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE (user_id, list_name)
);

CREATE TABLE tasks (
    task_id     BIGINT                        AUTO_INCREMENT PRIMARY KEY,
    list_id     BIGINT                        NOT NULL,
    task_text   VARCHAR(512)                  NOT NULL,
    priority    ENUM('LOW','MEDIUM','HIGH')   NOT NULL DEFAULT 'MEDIUM',
    task_status ENUM('PENDING','FINISHED')    NOT NULL DEFAULT 'PENDING',
    due_date    DATE                          DEFAULT NULL,
    created_at  DATETIME                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT tasks_ibfk_1 FOREIGN KEY (list_id) REFERENCES task_lists (list_id) ON DELETE CASCADE
);

-- Full-text search index for /search-task
ALTER TABLE tasks ADD FULLTEXT INDEX ft_task_text (task_text);
```

---

## Usage / Commands

Productivity commands are grouped under the `/productivity-bot` subcommand. Top-level commands (`/ping`, `/shutdown`) remain standalone.

| Command                             | Demonstration                                                                                             | Restricted       |
|-------------------------------------|:----------------------------------------------------------------------------------------------------------|------------------|
| `/ping`                             | ![Ping Bot.gif](assets/demo/Ping%20Bot.gif)                                                               | No               |
| `/productivity-bot create-list`     | ![Create List.gif](assets/demo/Create%20List.gif)                                                         | No               |
| `/productivity-bot add-task`        | ![Add Task.gif](assets/demo/Add%20Task.gif)                                                               | No               |
| `/productivity-bot view-list`       | ![View List.gif](assets/demo/View%20List.gif)                                                             | No               |
| `/productivity-bot mark-task`       | ![Mark Task_1.gif](assets/demo/Mark%20Task_1.gif) </br> ![Mark Task_2.png](assets/demo/Mark%20Task_2.png) | No               |
| `/productivity-bot search-task`     | ![Search Task.gif](assets/demo/Search%20Task.gif)                                                         | No               |
| `/productivity-bot clear-all-lists` | ![Clear All Lists.gif](assets/demo/Clear%20All%20Lists.gif)                                               | No               |
| `/shutdown`                         | ![Shutdown Bot.gif](assets/demo/Shutdown%20Bot.gif)                                                       | Yes — Owner Only |

Slash commands must be registered before use by running the bot once with the `--register` flag.

### `/productivity-bot add-task` Flow

1. A dropdown menu lists all task lists belonging to the user.
2. Selecting a list opens a modal prompting for:
   - **Task** — a short description (max 512 characters)
   - **Priority** — `LOW`, `MEDIUM`, or `HIGH` (via radio group)
   - **Deadline** — optional date in `yyyy/MM/dd` format
3. On a validation error, a **Try Again** button re-opens the modal with the same list pre-selected.

### `/productivity-bot view-list` Flow

1. A dropdown menu lists all task lists belonging to the user.
2. Selecting a list displays all tasks in that list, showing each task's status (⬜ pending / ✅ finished), priority level, and optional due date.

### `/productivity-bot mark-task` Flow

1. A dropdown menu lists all task lists belonging to the user.
2. Selecting a list shows a multi-select dropdown of all pending tasks in that list.
3. After selecting one or more tasks, a confirmation prompt appears with **All good!** and **Actually, nevermind!** buttons.
4. Confirming marks all selected tasks as `FINISHED` in the database.

### `/productivity-bot search-task` Flow

1. Running the command opens a modal prompting for a **keyword** (max 50 characters).
2. Results are displayed as a numbered, read-only ephemeral list showing each task's status (⬜ pending / ✅ finished), task text, source list name, and optional deadline.
3. If results exceed 5 per page, **◀ Back** and **Next ▶** buttons appear to navigate pages.
4. If no tasks match the keyword, a plain message is shown.

### `/productivity-bot clear-all-lists` Flow

1. A confirmation prompt appears with **Yes, do it!** and **Nevermind!** buttons.
2. Confirming permanently deletes all task lists and their associated tasks for the user. This action cannot be undone.

---

## Architecture

All command types (slash commands, modals, dropdown menus, buttons) share the `CommandHandler` abstract base class, which provides a pre-configured SLF4J logger via `getLogger(getClass())`. Each interaction type is represented by a nested interface inside `CommandHandler`:

| Interface                   | Event handled                  |
|-----------------------------|--------------------------------|
| `SlashCommandInterface`     | `SlashCommandInteractionEvent` |
| `ModalInterface`            | `ModalInteractionEvent`        |
| `StringSelectMenuInterface` | `StringSelectInteractionEvent` |
| `ButtonInterface`           | `ButtonInteractionEvent`       |

`Listeners.java` maintains four separate `HashMap`s (one per interaction type) and routes every incoming event to the correct handler. Modal and button handlers that carry a dynamic ID suffix (e.g. `add-task-modal:<listId>`) are matched with `String#startsWith` so the prefix acts as the key.

---

## Project Structure

```
src/main/java/dev/kawrl/
│
├── MyDiscordBot.kt
├── Listeners.java
│     Entry point and event router. MyDiscordBot builds the JDA instance and
│     initializes the DB pool; Listeners dispatches every interaction to its handler.
│
├── interfaces/
│   └── CommandHandler.java
│         Abstract base class shared by all handlers. Defines the four nested
│         interfaces: SlashCommandInterface, ModalInterface,
│         StringSelectMenuInterface, and ButtonInterface.
│
├── botcommands/
│   ├── PingCommand.kt          /ping
│   ├── ShutdownCommand.kt      /shutdown  (owner-only)
│   │
│   └── productivityfeatures/
│       │
│       ├── taskcreation/       Handles everything related to creating lists and tasks.
│       │   ├── CreateListCommand.kt
│       │   ├── AddTaskCommand.kt
│       │   ├── AddTaskMenuHandler.java
│       │   ├── AddTaskModal.java
│       │   ├── AddTaskSubmissionHandler.kt
│       │   └── AddTaskRetryHandler.java
│       │
│       ├── taskcompletion/     Handles the full mark-as-finished interaction flow.
│       │   ├── MarkTaskCommand.kt
│       │   ├── TaskSelectionHandler.kt
│       │   ├── MarkTaskConfirmation.kt
│       │   ├── MarkTaskHandler.kt
│       │   └── MarkTaskCancelHandler.java
│       │
│       ├── taskdisplay/        Handles fetching and displaying task lists to the user.
│       │   ├── ViewListCommand.kt
│       │   └── ViewListHandler.kt
│       │
│       ├── tasksearch/         Handles keyword search across all user task lists.
│       │   ├── SearchTaskCommand.kt
│       │   ├── SearchTaskModal.java
│       │   ├── SearchTaskFormatter.kt
│       │   ├── SearchTaskSubmissionHandler.kt
│       │   └── SearchTaskPageHandler.kt
│       │
│       └── listdeletion/       Handles the full clear-all-lists confirmation flow.
│           ├── ClearAllListsCommand.kt
│           ├── ClearAllListsHandler.java
│           └── ClearAllListsCancelHandler.java
│
└── database/
    ├── DatabaseManager.java    HikariCP connection pool setup and lifecycle.
    └── TaskRepo.java           All SQL queries for users, task lists, and tasks.

src/main/resources/
└── logback.xml                 Logging config (console + rolling file appender, 7-day retention).

logs/                           Runtime log output (git-ignored).

db/
└── schema.sql                  Initial database schema and table definitions.
```

---

## Planned Features

- **To-Do List Commands**
   - [ ] `/productivity-bot stats` — View task statistics and completion insights