package dev.kawrl.database;

import dev.kawrl.interfaces.TaskRepositoryInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * All SQL queries for tasks and task lists live here.
 * Each method opens and closes its own connection via try-with-resources.
 */
public class TaskRepo implements TaskRepositoryInterface {
    // -------------------------------------------------------------------------
    //  Users
    // -------------------------------------------------------------------------

    /**
     * Inserts the user if they do not already exist (upsert by Discord user ID).
     */
    public void upsertUser(String userId, String username) throws SQLException {
        String sql = """
                INSERT INTO users (user_id, username)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE username = VALUES(username)
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    //  Task Lists
    // -------------------------------------------------------------------------

    /**
     * Returns true if a task list with the given name already exists for the user.
     */
    public boolean listExistsForUser(String userId, String listName) throws SQLException {
        String sql = "SELECT 1 FROM task_lists WHERE user_id = ? AND list_name = ? LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, listName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Creates a new task list for the user and returns the generated list_id.
     *
     * @throws SQLException if the insert fails (e.g. duplicate name constraint).
     */
    public long createTaskList(String userId, String listName) throws SQLException {
        String sql = "INSERT INTO task_lists (user_id, list_name) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, userId);
            ps.setString(2, listName);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("No generated key returned for new task list.");
            }
        }
    }

    public Map<String, Long> getListNamesForUser(String userID) throws SQLException {
        String sql = "SELECT list_id, list_name FROM task_lists WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, Long> lists = new LinkedHashMap<>();
                while (rs.next()) {
                    lists.put(rs.getString("list_name"), rs.getLong("list_id"));
                }
                return lists;
            }
        }
    }

    public int clearAllListsForUser(String userId) throws SQLException {
        String sql = "DELETE FROM task_lists WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            return ps.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    //  Tasks
    // -------------------------------------------------------------------------
    public Map<String, Long> getTasksFromTaskListForUser(String listID, String userID) throws SQLException{
        String sql = """
                SELECT
                	t.task_id,
                    t.task_text
                FROM tasks t
                JOIN task_lists tList
                	ON t.list_id = tList.list_id
                    WHERE tList.user_id = ?
                    AND tList.list_id = ?
                    AND NOT t.task_status = 'FINISHED';
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userID);
            ps.setString(2, listID);
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, Long> tasks = new LinkedHashMap<>();
                while (rs.next()) {
                    tasks.put(rs.getString("task_text"), rs.getLong("task_id"));
                }
                return tasks;
            }
        }
    }

    /**
     * Adds a task to an existing list.
     *
     * @param listId   the target task list
     * @param taskText the task description
     * @param priority "LOW", "MEDIUM", or "HIGH"
     * @param dueDate  optional due date (pass null to omit)
     * @return the generated task_id
     */
    public long addTask(long listId, String taskText, String priority, Date dueDate) throws SQLException {
        String sql = "INSERT INTO tasks (list_id, task_text, priority, due_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, listId);
            ps.setString(2, taskText);
            ps.setString(3, priority);
            ps.setDate(4, dueDate);  // null is valid here
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("No generated key returned for new task.");
            }
        }
    }

    /**
     * Marks a task as completed.
     *
     * @return true if a row was updated, false if the task_id didn't exist.
     */
    public boolean completeTask(long taskId, long listID) throws SQLException {
        String sql = """
                UPDATE tasks
                SET tasks.task_status = 'FINISHED'
                WHERE tasks.task_id = ? AND
                tasks.list_id = ?;
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ps.setLong(2, listID);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Prints all tasks in a list to a formatted string for Discord reply.
     * Returns an empty-list message if no tasks exist.
     */
    public String formatTaskList(long listId, String listName) throws SQLException {
        String sql = """
            SELECT
                task_id,
                task_text,
                priority,
                task_status,
                due_date
            FROM tasks
            WHERE list_id = ?
            ORDER BY task_id ASC;
            """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, listId);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                sb.append("**📋 ").append(listName).append("**\n");
                int count = 0;
                while (rs.next()) {
                    count++;
                    boolean done = rs.getString("task_status").equals("FINISHED"); // fix 1
                    String check = done ? "✅" : "⬜";
                    String due = rs.getDate("due_date") != null
                            ? " *(due: " + rs.getDate("due_date") + ")*"
                            : "";
                    sb.append(String.format("%s `#%d` **[%s]** %s%s\n",
                            check,
                            rs.getLong("task_id"),
                            rs.getString("priority"),
                            rs.getString("task_text"),
                            due));
                }
                if (count == 0) sb.append("*No tasks yet. Add one with `/add-task`!*");
                return sb.toString();
            }
        }
    }

    /**
     * Full-text search across all tasks belonging to a user.
     * Returns a page of results using LIMIT + OFFSET.
     *
     * @param userId   the Discord user ID to scope results
     * @param keyword  the search term (used in MATCH...AGAINST)
     * @param offset   number of rows to skip (for pagination)
     * @param pageSize number of rows to return
     * @return ordered list of formatted result strings, ready for display
     */
    public List<String> searchTasks(String userId, String keyword, int offset, int pageSize) throws SQLException {
        String sql = """
            SELECT
                t.task_text,
                t.task_status,
                t.priority,
                t.due_date,
                tl.list_name
            FROM tasks t
            JOIN task_lists tl ON t.list_id = tl.list_id
            WHERE tl.user_id = ?
              AND MATCH(t.task_text) AGAINST(? IN BOOLEAN MODE)
            ORDER BY MATCH(t.task_text) AGAINST(? IN BOOLEAN MODE) DESC
            LIMIT ? OFFSET ?
            """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, keyword);
            ps.setString(3, keyword);
            ps.setInt(4, pageSize);
            ps.setInt(5, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> results = new ArrayList<>();
                while (rs.next()) {
                    boolean done = "FINISHED".equals(rs.getString("task_status"));
                    String check = done ? "✅" : "⬜";
                    String due = rs.getDate("due_date") != null
                            ? " (Deadline: " + rs.getDate("due_date") + ")"
                            : "";
                    results.add(String.format("%s %s from **%s**%s",
                            check,
                            rs.getString("task_text"),
                            rs.getString("list_name"),
                            due));
                }
                return results;
            }
        }
    }

    /**
     * Returns the total number of tasks matching the keyword for a user.
     * Used to determine total page count.
     */
    public int countSearchResults(String userId, String keyword) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM tasks t
            JOIN task_lists tl ON t.list_id = tl.list_id
            WHERE tl.user_id = ?
              AND MATCH(t.task_text) AGAINST(? IN BOOLEAN MODE)
            """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, keyword);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}