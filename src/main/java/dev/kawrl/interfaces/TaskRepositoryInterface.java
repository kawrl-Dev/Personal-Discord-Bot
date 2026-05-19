package dev.kawrl.interfaces;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface TaskRepositoryInterface {
    void upsertUser(String userId, String username) throws SQLException;
    boolean listExistsForUser(String userId, String listName) throws SQLException;
    long createTaskList(String userId, String listName) throws SQLException ;
    Map<String, Long> getListNamesForUser(String userID) throws SQLException;
    int clearAllListsForUser(String userId) throws SQLException ;
    Map<String, Long> getTasksFromTaskListForUser(String listID, String userID) throws SQLException;
    long addTask(long listId, String taskText, String priority, Date dueDate) throws SQLException;
    boolean completeTask(long taskId, long listID) throws SQLException;
    String formatTaskList(long listId, String listName) throws SQLException;
    List<String> searchTasks(String userId, String keyword, int offset, int pageSize) throws SQLException;
    int countSearchResults(String userId, String keyword) throws SQLException;
}
