# 🤖 My Personal Discord Bot 🤖

This passion project showcases a lightweight Discord bot built with Java and Kotlin using the [Java Discord API](https://github.com/discord-jda/JDA), featuring a simple slash command architecture, structured logging, and MySQL database integration via HikariCP.
___
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
   java -jar build/libs/MyDiscordBotProject-v1.0.0.jar
 
   # Register slash commands (only needed once, or after adding new commands)
   java -jar build/libs/MyDiscordBotProject-v1.0.0.jar --register
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

| Variable      | Description                                                      |
|---------------|------------------------------------------------------------------|
| `BOT_API`     | Your Discord bot token from the Developer Portal                 |
| `USER_ID`     | Your Discord user ID — grants access to owner-only commands      |
| `DB_HOST`     | MySQL server host (e.g. `localhost`)                             |
| `DB_PORT`     | MySQL server port (default: `3306`)                              |
| `DB_NAME`     | Name of the database to connect to                               |
| `DB_USER`     | MySQL username                                                   |
| `DB_PASSWORD` | MySQL password                                                   |

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
    task_id      BIGINT       AUTO_INCREMENT PRIMARY KEY,
    list_id      BIGINT       NOT NULL,
    task_text    TEXT         NOT NULL,
    priority     ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'MEDIUM',
    is_completed TINYINT   NOT NULL DEFAULT 0,
    due_date     DATE,
    FOREIGN KEY (list_id) REFERENCES task_lists(list_id)
);
```

---

## Usage / Commands

| Command        | Description                                                                                         | Restricted       |
|----------------|-----------------------------------------------------------------------------------------------------|------------------|
| `/ping`        | Checks if the bot is alive, returns gateway latency ![Ping Command.png](images/Ping%20Command.png)  | No               |
| `/shutdown`    | Gracefully shuts down the bot ![Shutdown Bot.png](images/Shutdown%20Bot.png)                        | Yes — owner only |
| `/create-list` | Creates a new personal task list ![Create New Task List.png](images/Create%20New%20Task%20List.png) | No               |

Slash commands must be registered before use by running the bot once with the `--register` flag.
 
---

## Project Structure

```
src/main/java/dev/kawrl/
├── MyDiscordBot.kt                # Entry point — builds and starts the JDA instance
├── Listeners.java                 # Routes slash command events to the correct handler
├── interfaces/
│   └── SlashCommandInterface.java # Interface all commands implement
├── botcommands/
│   ├── PingCommand.kt             # /ping implementation (Kotlin)
│   ├── ShutdownCommand.kt         # /shutdown implementation (Kotlin)
│   └── productivityfeatures/
│       └── CreateNewTaskListCommand.java  # /create-list implementation
└── database/
    ├── DatabaseManager.java       # HikariCP connection pool setup and lifecycle
    └── TaskRepo.java              # All SQL queries for users, task lists, and tasks
 
src/main/resources/
└── logback.xml                    # Logging config (console + rolling file output)
 
logs/                              # Runtime log output (git-ignored)
```
 
---

## Planned Features
- [x] **MySQL Database Integration**
- **To-Do List Commands**
  - [ ] `/add-task` — Add a task to an existing list with priority and optional due date
  - [ ] `/view-list` — Display all tasks in a chosen list
  - [ ] `/complete-task` — Mark a task as done by its ID
  - [ ] `/delete-task` — Remove a task by its ID
  - [ ] `/search-task` — Search across all lists by keyword
  - [ ] `/stats` — View task statistics and completion insights
