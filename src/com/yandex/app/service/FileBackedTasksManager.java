package com.yandex.app.service;

import com.yandex.app.history.HistoryManager;
import com.yandex.app.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private void save() throws ManagerSaveException {
        List<String> saveString = new ArrayList<>();
        saveString.add("id,type,name,status,description,epic");
        try (Writer writer = new FileWriter("data.csv")) {
            for (int i = 1; i < nextId; i++) {
                if (tasks.get(i) != null) {
                    saveString.add(tasks.get(i).toString());
                } else if (epics.get(i) != null) {
                    saveString.add(epics.get(i).toString());
                } else {
                    saveString.add(subtasks.get(i).toString());
                }
            }
            saveString.add("");
            if (!historyManager.getHistory().isEmpty()) {
                saveString.add(historyToString(historyManager));
            }
            for (String line : saveString) {
                writer.write(line + "\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException();
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTasksManager fileBackedTasksManager = Managers.getFileBackedTasksManager();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String[] lines;
            reader.readLine();
            while (reader.ready()) {
                lines = reader.readLine().split(",");
                if (lines[0].isBlank()) {
                    break;
                }
                switch(TaskTypes.valueOf(lines[1])) {
                    case TASK:
                        fileBackedTasksManager.createTask(fileBackedTasksManager.fromString(lines));
                        break;
                    case EPIC:
                        fileBackedTasksManager.createEpic((Epic)fileBackedTasksManager.fromString(lines));
                        break;
                    case SUBTASK:
                        fileBackedTasksManager.createSubtask((Subtask)fileBackedTasksManager.fromString(lines));
                        break;
                }
            }
            List<Integer> historyIds = historyFromString(reader.readLine());
            for (Integer id : historyIds) {
                if (fileBackedTasksManager.tasks.containsKey(id)) {
                    fileBackedTasksManager.historyManager.add(fileBackedTasksManager.tasks.get(id));
                } else if (fileBackedTasksManager.epics.containsKey(id)) {
                    fileBackedTasksManager.historyManager.add(fileBackedTasksManager.epics.get(id));
                } else {
                    fileBackedTasksManager.historyManager.add(fileBackedTasksManager.subtasks.get(id));
                }
            }
            fileBackedTasksManager.save();
            return fileBackedTasksManager;
        } catch (IOException exception) {
            throw new ManagerSaveException();
        }
    }

    private Task fromString(String[] valueArray) {
        switch (TaskTypes.valueOf(valueArray[1])) {
            case TASK:
                return new Task(valueArray[2], valueArray[4], StatusName.valueOf(valueArray[3]));
            case EPIC:
                return new Epic(valueArray[2], valueArray[4]);
            case SUBTASK:
                return new Subtask(valueArray[2], valueArray[4], StatusName.valueOf(valueArray[3]),
                        Integer.parseInt(valueArray[5]));
            default:
                return null;
        }
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder history = new StringBuilder();
        for (Task task : manager.getHistory()) {
            history.append(task.getId()).append(",");
        }
        return history.substring(0, history.length() - 1);
    }

    private static List<Integer> historyFromString(String value) {
        String[] historyArray = value.split(",");
        List<Integer> historyList = new ArrayList<>();
        for (String line : historyArray) {
            historyList.add(Integer.valueOf(line));
        }
        return historyList;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }
}
