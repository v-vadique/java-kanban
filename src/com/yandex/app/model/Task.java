package com.yandex.app.model;

public class Task {
    protected String name;
    protected String description;
    protected StatusName status;
    protected int id;

    public Task(String name, String description, StatusName status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = StatusName.NEW;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(StatusName status) {
        this.status = status;
    }

    public StatusName getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "com.yandex.app.model.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
