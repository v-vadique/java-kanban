package com.yandex.app.model;

import com.yandex.app.service.StatusName;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, StatusName status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "com.yandex.app.model.Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                ", epicId=" + epicId +
                '}';
    }
}
