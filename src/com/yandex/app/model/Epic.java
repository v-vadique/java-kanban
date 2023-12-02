package com.yandex.app.model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description, String status) {
        super(name, description, status);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
        this.status = "NEW";
    }
    
    public void deleteSubtaskId(int id) {
        subtaskIds.remove(subtaskIds.indexOf(id));
    }

    @Override
    public String toString() {
        return "com.yandex.app.model.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                ", subtaskIds=" + subtaskIds.size() +
                '}';
    }
}
