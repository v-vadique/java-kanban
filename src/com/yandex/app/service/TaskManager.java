package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getAllTasks();
    ArrayList<Epic> getAllEpics();
    ArrayList<Subtask> getAllSubtasks();
    void deleteAllTasks();
    void deleteAllEpics();
    void deleteAllSubtasks();
    Task getTaskById(int id);
    Epic getEpicById(int id);
    Subtask getSubtaskById(int id);
    void createTask(Task task);
    void createEpic(Epic epic);
    void createSubtask(Subtask subtask);
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);
    void deleteTask(int id);
    void deleteEpic(int id);
    void deleteSubtask(int id);
    ArrayList<Subtask> getAllEpicSubtasks(int id);
}
