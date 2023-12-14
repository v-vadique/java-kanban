package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.StatusName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    int nextId = 1;
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все задачи были удалены");
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
        System.out.println("Все эпики были удалены");
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Integer integer : epics.keySet()) {
            epics.get(integer).clearSubtaskIds();
        }
        System.out.println("Все подзадачи были удалены");
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        System.out.println(task);
        historyManager.add(tasks.get(id));
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        System.out.println(epic);
        historyManager.add(epics.get(id));
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        System.out.println(subtask);
        historyManager.add(subtasks.get(id));
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
        System.out.println("Задача создана. Идентификатор " + task.getId());
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        System.out.println("Эпик создан. Идентификатор " + epic.getId());
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(nextId);
        nextId++;
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        reconsiderEpicStatus(subtask.getEpicId());
        System.out.println("Подзадача создана. Идентификатор " + subtask.getId());
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Задача обновлена. Идентификатор " + task.getId());
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        System.out.println("Эпик обновлен. Идентификатор " + epic.getId());
    }

    @Override
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
                case NEW:
                    isDone = false;
                    break;
                case IN_PROGRESS:
                    isNew = false;
                    isDone = false;
                    break;
                case DONE:
                    isNew = false;
                    break;
            }
        }
        if (isNew) {
            epics.get(id).setStatus(StatusName.NEW);
        } else if (isDone) {
            epics.get(id).setStatus(StatusName.DONE);
        } else {
            epics.get(id).setStatus(StatusName.IN_PROGRESS);
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        System.out.println("Задача была удалена");
    }

    @Override
    public void deleteEpic(int id) {
        Epic removedEpic = epics.remove(id);
        for (Integer subtaskId : removedEpic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
        System.out.println("Эпик был удален");
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask removedSubtask = subtasks.remove(id);
        epics.get(id).deleteSubtaskId(removedSubtask.getId());
        System.out.println("Подзадача была удалена");
    }

    @Override
    public ArrayList<Subtask> getAllEpicSubtasks(int id) {
        ArrayList<Subtask> epicsSubtasks = new ArrayList<>();
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            epicsSubtasks.add(subtasks.get(subtaskId));
        }
        return epicsSubtasks;
    }
}
