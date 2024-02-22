package com.yandex.app.service;

import com.yandex.app.history.HistoryManager;
import com.yandex.app.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected int nextId = 1;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));


    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void deleteAllTasks() {
        for (Integer key : tasks.keySet()) {
            historyManager.remove(key);
        }
        for (Task task : prioritizedTasks) {
            if (task.getType().equals("TASK")) {
                prioritizedTasks.remove(task);
            }
        }
        tasks.clear();
        System.out.println("Все задачи были удалены");
    }

    @Override
    public void deleteAllEpics() {
        for (Integer key : epics.keySet()) {
            historyManager.remove(key);
        }
        epics.clear();
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
        for (Task subtask : prioritizedTasks) {
            if (subtask.getType().equals("SUBTASK")) {
                prioritizedTasks.remove(subtask);
            }
        }
        subtasks.clear();
        System.out.println("Все эпики были удалены");
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
        for (Task subtask : prioritizedTasks) {
            if (subtask.getType().equals("SUBTASK")) {
                prioritizedTasks.remove(subtask);
            }
        }
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
        prioritizedTasks.add(task);
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
        prioritizedTasks.add(subtask);
        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        reconsiderEpicStatus(subtask.getEpicId());
        countEpicTime(subtask.getEpicId());
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
        countEpicTime(subtask.getEpicId());
        System.out.println("Подзадача обновлена. Индентификатор " + subtask.getId());
    }

    private void reconsiderEpicStatus(int id) {
        boolean isNew = true;
        boolean isDone = true;
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            switch (subtasks.get(subtaskId).getStatus()) {
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

    private void countEpicTime(int id) {
        LocalDateTime startTime = epics.get(id).getStartTime();
        LocalDateTime endTime = epics.get(id).getEndTime();
        Duration duration = Duration.ofMinutes(0);
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            if (startTime == null || startTime.isAfter(subtasks.get(subtaskId).getStartTime())) {
                startTime = subtasks.get(subtaskId).getStartTime();
            }
        }
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            if (endTime == null || endTime.isBefore(subtasks.get(subtaskId).getEndTime())) {
                endTime = subtasks.get(subtaskId).getEndTime();
            }
        }
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            duration = duration.plusMinutes(subtasks.get(subtaskId).getDuration().toMinutes());
        }
        epics.get(id).setStartTime(startTime);
        epics.get(id).setEndTime(endTime);
        epics.get(id).setDuration(duration);
    }

    @Override
    public boolean checkForCrossings() {
        for (Task task : prioritizedTasks) {
            if (prioritizedTasks.higher(task) != null) {
                if (task.getEndTime().isAfter(prioritizedTasks.higher(task).getStartTime())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        System.out.println("Задача была удалена");
    }

    @Override
    public void deleteEpic(int id) {
        historyManager.remove(id);
        Epic removedEpic = epics.remove(id);
        for (Integer subtaskId : removedEpic.getSubtaskIds()) {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }
        System.out.println("Эпик был удален");
    }

    @Override
    public void deleteSubtask(int id) {
        historyManager.remove(id);
        int epicId = subtasks.get(id).getEpicId();
        prioritizedTasks.remove(subtasks.get(id));
        Subtask removedSubtask = subtasks.remove(id);
        epics.get(epicId).deleteSubtaskId(removedSubtask.getId());
        reconsiderEpicStatus(epicId);
        countEpicTime(epicId);
        System.out.println("Подзадача была удалена");
    }

    @Override
    public List<Subtask> getAllEpicSubtasks(int id) {
        ArrayList<Subtask> epicsSubtasks = new ArrayList<>();
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            epicsSubtasks.add(subtasks.get(subtaskId));
        }
        return epicsSubtasks;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
