package dev.kawrl.botcommands.productivityfeatures.classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UserTaskData {
    @JsonProperty("username")
    private final String username;

    @JsonProperty("task_lists")
    private final List<TaskList> taskLists;

    @JsonCreator
    public UserTaskData(@JsonProperty("username") String username) {
        this.username = username;
        this.taskLists = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public List<TaskList> getTaskLists() {
        return taskLists;
    }

    /** Returns true if a list with the given name already exists. */
    public boolean hasListWithName(String name) {
        return taskLists.stream()
                .anyMatch(list -> list.getListName().equalsIgnoreCase(name));
    }

    public void addTaskList(TaskList taskList) {
        taskLists.add(taskList);
    }
}