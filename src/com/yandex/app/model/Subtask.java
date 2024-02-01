package com.yandex.app.model;

public class Subtask extends Task {
    private int epicId;

    protected static TaskTypes type = TaskTypes.SUBTASK;

    public Subtask(String name, String description, StatusName status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    @Override
    public String getType() {
        return type + "";
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getName() + "," + getStatus() + "," +
                getDescription() + "," + getEpicId();
    }
}
