# MyDiscordBot

A lightweight Discord bot built with Java and JDA, featuring a simple slash command architecture and structured logging.
___
## Requirements

- Java 17+
- Gradle (or use the included `gradlew` wrapper)
- A Discord bot token ([Discord Developer Portal](https://discord.com/developers/applications))
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
```

| Variable  | Description                                               |
|-----------|-----------------------------------------------------------|
| `BOT_API` | Your Discord bot token from the Developer Portal          |
| `USER_ID` | Your Discord user ID — grants access to owner-only commands |

> ⚠️ Never commit your `.env` file. It is already listed in `.gitignore`.
 
---

## Usage / Commands

| Command     | Description                        | Restricted |
|-------------|------------------------------------|------------|
| `/ping`     | Checks if the bot is alive, returns gateway latency | No |
| `/shutdown` | Gracefully shuts down the bot      | Yes — owner only |

Slash commands must be registered before use by running the bot once with the `--register` flag.
 
---

## Project Structure

```
src/main/java/dev/kawrl/
├── MyDiscordBot.java              # Entry point — builds and starts the JDA instance
├── Listeners.java                 # Routes slash command events to the correct handler
├── interfaces/
│   └── SlashCommandInterface.java # Interface all commands implement
└── botcommands/
    ├── PingCommand.java           # /ping implementation
    └── ShutdownCommand.java       # /shutdown implementation
 
src/main/resources/
└── logback.xml                    # Logging config (console + rolling file output)
 
logs/                              # Runtime log output (git-ignored)
```
 
---

## Planned Features
- [ ] More slash commands
- [ ] MySQL Database Integration