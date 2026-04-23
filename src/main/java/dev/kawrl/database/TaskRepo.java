package dev.kawrl.database;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * All SQL queries for tasks and task lists live here.
 * Each method opens and closes its own connection via try-with-resources.
 */
public class TaskRepo {
    // -------------------------------------------------------------------------
    //  Users
    // -------------------------------------------------------------------------

    /**
     * Inserts the user if they do not already exist (upsert by Discord user ID).
     */
    public static void upsertUser(String userId, String username) throws SQLException {
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
    public static boolean listExistsForUser(String userId, String listName) throws SQLException {
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
    public static long createTaskList(String userId, String listName) throws SQLException {
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

    public static Map<String, Long> getListNamesForUser(String userID) throws SQLException{
        String sql = "SELECT list_id, list_name from task_lists WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement(sql);{
                preparedStatement.setString(1,userID);
                try (ResultSet result = preparedStatement.executeQuery()){
                    Map<String, Long> lists = new LinkedHashMap<>();
                    while (result.next()) {
                        lists.put(result.getString("list_name"), result.getLong("list_id"));
                    }
                    return lists;
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    //  Tasks
    // -------------------------------------------------------------------------
    public static Map<String, Long> getTasksFromTaskListForUser(String listID, String userID) throws SQLException{
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
    public static long addTask(long listId, String taskText, String priority, Date dueDate) throws SQLException {
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
    public static boolean completeTask(long taskId, long listID) throws SQLException {
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
    public static String formatTaskList(long listId, String listName) throws SQLException {
        String sql = """
                SELECT task_id, task_text, priority, is_completed, due_date
                FROM tasks
                WHERE list_id = ?
                ORDER BY task_id ASC
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
                    boolean done = rs.getBoolean("is_completed");
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
}