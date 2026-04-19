package dev.kawrl.botcommands.productivityfeatures.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Task(@JsonProperty("task_id") int id, @JsonProperty("task") String task) {
    public Task(int id, String task) {
        this.id = id;
        this.task = task;
    }
}
