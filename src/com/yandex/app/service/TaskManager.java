package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    int nextId = 1;

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все задачи были удалены");
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
        System.out.println("Все эпики были удалены");
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Integer integer : epics.keySet()) {
            epics.get(integer).clearSubtaskIds();
        }
        System.out.println("Все подзадачи были удалены");
    }

    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        System.out.println(task);
        return task;
    }

    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        System.out.println(epic);
        return epic;
    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        System.out.println(subtask);
        return subtask;
    }
    public void createTask(Task task) {
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
        System.out.println("Задача создана. Идентификатор " + task.getId());
    }

    public void createEpic(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        System.out.println("Эпик создан. Идентификатор " + epic.getId());
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(nextId);
        nextId++;
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        reconsiderEpicStatus(subtask.getEpicId());
        System.out.println("Подзадача создана. Идентификатор " + subtask.getId());
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Задача обновлена. Идентификатор " + task.getId());
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        System.out.println("Эпик обновлен. Идентификатор " + epic.getId());
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        reconsiderEpicStatus(subtask.getEpicId());
        System.out.println("Подзадача обновлена. Индентификатор " + subtask.getId());
    }

    private void reconsiderEpicStatus(int id) {
        boolean isNew = true;
        boolean isDone = true;
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            switch(subtasks.get(subtaskId).getStatus()){
                case("NEW"):
                    isDone = false;
                    break;
                case("IN PROGRESS"):
                    isNew = false;
                    isDone = false;
                    break;
                case("DONE"):
                    isNew = false;
                    break;
            }
        }
        if (isNew) {
            epics.get(id).setStatus("NEW");
        } else if (isDone) {
            epics.get(id).setStatus("DONE");
        } else {
            epics.get(id).setStatus("IN PROGRESS");
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
        System.out.println("Задача была удалена");
    }

    public void deleteEpic(int id) {
        Epic removedEpic = epics.remove(id);
        for (Integer subtaskId : removedEpic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
        System.out.println("Эпик был удален");
    }

    public void deleteSubtask(int id) {
        Subtask removedSubtask = subtasks.remove(id);
        epics.get(id).deleteSubtaskId(removedSubtask.getId());
        System.out.println("Подзадача была удалена");
    }

    public ArrayList<Subtask> getAllEpicSubtasks(int id) {
        ArrayList<Subtask> epicsSubtasks = new ArrayList<>();
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            epicsSubtasks.add(subtasks.get(subtaskId));
        }
        return epicsSubtasks;
    }
}
