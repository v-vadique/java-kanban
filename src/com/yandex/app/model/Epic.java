package com.yandex.app.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();
    protected static TaskTypes type = TaskTypes.EPIC;

    public Epic(String name, String description) {
        super(name, description);
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
        subtaskIds.remove(id);
    }

    @Override
    public String getType() {
        return type + "";
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getName() + "," + getStatus() + ","
                + getDescription() + ",";
    }
}
