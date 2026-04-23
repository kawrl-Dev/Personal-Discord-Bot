# 🤖 My Personal Discord Bot 🤖

This passion project showcases a lightweight Discord bot built with Java and Kotlin using the [Java Discord API](https://github.com/discord-jda/JDA), featuring a slash command architecture, structured logging, MySQL database integration via HikariCP, and support for modals, dropdowns, and button interactions.

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
   java -jar build/libs/MyDiscordBotProject-v1.0.3.jar

   # Register slash commands (only needed once, or after adding new commands)
   java -jar build/libs/MyDiscordBotProject-v1.0.3.jar --register
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
```

---

## Usage / Commands

| Command        | Description                                                                                                                                                                    | Restricted       |
|----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------|
| `/ping`        | Checks if the bot is alive, returns gateway latency<br/><br/> ![1_PingCommand.png](images/1_PingCommand.png)                                                                   | No               |
| `/shutdown`    | Gracefully shuts down the bot <br/><br/> ![2_Create-List_0.png](images/2_Create-List_0.png) <br/> ![2_Create-List_1.png](images/2_Create-List_1.png)                           | Yes — owner only |
| `/create-list` | Creates a new personal task list <br/><br/> ![3_AddTaskToList_0.png](images/3_AddTaskToList_0.png) <br/> ![3_AddTaskToList_1.png](images/3_AddTaskToList_1.png)                | No               |
| `/add-task`    | Opens a dropdown of your lists, then a modal to fill in task details <br/><br/> ![4_MarkTask_0.png](images/4_MarkTask_0.png)<br/> ![4_MarkTask_1.png](images/4_MarkTask_1.png) | No               |
| `/mark-task`   | Opens a dropdown of your lists, then lets you select tasks to mark as finished <br/><br/> ![5_ShutdownCommand.png](images/5_ShutdownCommand.png)                               | No               |

Slash commands must be registered before use by running the bot once with the `--register` flag.

### `/add-task` Flow

1. A dropdown menu lists all task lists belonging to the user.
2. Selecting a list opens a modal prompting for:
    - **Task** — a short description (max 512 characters)
    - **Priority** — `LOW`, `MEDIUM`, or `HIGH` (via radio group)
    - **Deadline** — optional date in `yyyy/MM/dd` format
3. On a validation error, a **Try Again** button re-opens the modal with the same list pre-selected.

### `/mark-task` Flow

1. A dropdown menu lists all task lists belonging to the user.
2. Selecting a list shows a multi-select dropdown of all tasks in that list.
3. After selecting one or more tasks, a confirmation prompt appears with **All good!** and **Actually, nevermind!** buttons.
4. Confirming marks all selected tasks as `FINISHED` in the database.

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
├── MyDiscordBot.kt                          # Entry point — builds and starts the JDA instance
├── Listeners.java                           # Routes all interaction events to the correct handler
├── interfaces/
│   └── CommandHandler.java                  # Abstract base class + nested interfaces for all interaction types
├── botcommands/
│   ├── PingCommand.kt                       # /ping implementation
│   ├── ShutdownCommand.kt                   # /shutdown implementation (owner-only)
│   └── productivityfeatures/
│       ├── CreateNewTaskListCommand.kt       # /create-list implementation
│       ├── addTaskToListCommand/
│       │   ├── AddTaskCommand.kt            # /add-task — fetches lists, shows dropdown
│       │   ├── CreateTaskModal.java         # Dropdown handler — opens the add-task modal
│       │   ├── TaskModalFactory.java        # Builds the reusable add-task Modal object
│       │   ├── AddTaskModalHandler.kt       # Modal submission handler — validates & writes to DB
│       │   └── RetryAddTaskButton.java      # "Try Again" button handler — re-opens the modal
│       └── markTaskAsFinishedCommand/
│           ├── MarkTasksAsCompleteCommand.kt  # /mark-task — fetches lists, shows dropdown
│           ├── MarkSelectedTaskFactory.kt     # Dropdown handler — shows task multi-select for chosen list
│           ├── ApproveSelectedTasksPrompt.kt  # Confirmation prompt with Yes/No buttons
│           ├── ConfirmMarkedTasks.kt          # "All good!" button handler — marks tasks as FINISHED
│           └── CancelMarkTasks.java           # "Actually, nevermind!" button handler — cancels flow
└── database/
    ├── DatabaseManager.java                 # HikariCP connection pool setup and lifecycle
    └── TaskRepo.java                        # All SQL queries for users, task lists, and tasks

src/main/resources/
└── logback.xml                              # Logging config (console + rolling file output)

logs/                                        # Runtime log output (git-ignored)
```

---

## Planned Features

- [x] **MySQL Database Integration**
- [x] **`/create-list`** — Create a personal task list
- [x] **`/add-task`** — Add a task with priority and optional due date
- [x] **`/mark-task`** — Mark one or more tasks as finished
- **To-Do List Commands**
    - [ ] `/view-list` — Display all tasks in a chosen list
    - [ ] `/search-task` — Search across all lists by keyword
    - [ ] `/stats` — View task statistics and completion insights