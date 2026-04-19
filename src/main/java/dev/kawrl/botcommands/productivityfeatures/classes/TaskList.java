package dev.kawrl.botcommands.productivityfeatures.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
    @JsonProperty("user_ID")
    private final String userID;
    @JsonProperty("taskList_name")
    private final String listName;
    @JsonProperty("taskList_items")
    private final List<Task> myTasks;

    public TaskList(@JsonProperty("user_ID") String userID, @JsonProperty("taskList_name") String listName) {
        this.userID = userID;
        this.listName = listName;
        this.myTasks = new ArrayList<>();
    }

    public String getListName() {
        return listName;
    }

    public void addTask(Task task){
        this.myTasks.add(task);
    }

    public int taskCount(){
        return this.myTasks.size();
    }

    public List<Task> getMyTasks() {
        return myTasks;
    }

    public String getUserID() {
        return userID;
    }
}
