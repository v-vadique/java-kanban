package com.yandex.app.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();
    protected LocalDateTime endTime;
    protected static TaskTypes type = TaskTypes.EPIC;

    public Epic(String name, String description) {
        super(name, description);
        this.duration = Duration.ofSeconds(0);
        this.startTime = LocalDateTime.parse("01.01.2000-00:00", FORMATTER);
        this.endTime = LocalDateTime.parse("01.01.2000-00:00", FORMATTER);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
        this.status = StatusName.NEW;
    }

    public void deleteSubtaskId(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    @Override
    public String getType() {
        return type + "";
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getName() + "," + getStatus() + "," + getDescription() + ","
                + getDuration().toMinutes() + "," + getStartTime().format(FORMATTER) + "," + getEndTime() + ",";
    }
}
