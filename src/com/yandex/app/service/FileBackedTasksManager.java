package com.yandex.app.service;

import com.yandex.app.history.HistoryManager;
import com.yandex.app.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager = Managers.getFileBackedTasksManager();
        try {
            fileBackedTasksManager = loadFromFile();
        } catch (Exception e) {
            System.out.println("Данные для загрузки не найдены");
            e.printStackTrace();
        }

        fileBackedTasksManager.createTask(new Task("имя1", "описание1"));
        fileBackedTasksManager.createTask(new Task("имя2", "описание2"));
        fileBackedTasksManager.createEpic(new Epic("имя3", "описание3"));
        fileBackedTasksManager.createSubtask(new Subtask("имя4", "описание4", StatusName.NEW, 3));

        fileBackedTasksManager.getTaskById(2);
        fileBackedTasksManager.getSubtaskById(4);
        fileBackedTasksManager.getEpicById(3);
    }

    public void save() throws ManagerSaveException {
        List<String> saveString = new ArrayList<>();
        saveString.add("id,type,name,status,description,epic");
        try (Writer writer = new FileWriter("data.csv")) {
            for (int i = 1; i < nextId; i++) {
                if (tasks.get(i) != null) {
                    saveString.add(toString(tasks.get(i)));
                } else if (epics.get(i) != null) {
                    saveString.add(toString(epics.get(i)));
                } else {
                    saveString.add(toString(subtasks.get(i)));
                }
            }
            saveString.add("");
            if (historyManager.getHistory().size() > 0) {
                saveString.add(historyToString(historyManager));
            }
            for (String s : saveString) {
                writer.write(s + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public static FileBackedTasksManager loadFromFile() throws ManagerSaveException {
        File file = new File("data.csv");
        FileBackedTasksManager fileBackedTasksManager = Managers.getFileBackedTasksManager();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String[] lines;
            reader.readLine();
            while (reader.ready()) {
                lines = reader.readLine().split(",");
                if (lines[0].isBlank()) {
                    break;
                }
                switch(lines[1]) {
                    case("TASK"):
                        fileBackedTasksManager.createTask(fileBackedTasksManager.fromString(lines));
                        break;
                    case("EPIC"):
                        fileBackedTasksManager.createEpic((Epic)fileBackedTasksManager.fromString(lines));
                        break;
                    case("SUBTASK"):
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
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + ",";
    }

    public String toString(Subtask subtask) {
        return subtask.getId() + "," + subtask.getType() + "," + subtask.getName() + "," + subtask.getStatus() + "," +
                subtask.getDescription() + "," + subtask.getEpicId();
    }

    public Task fromString(String[] valueArray) {
        switch (valueArray[1]) {
            case ("TASK"):
                return new Task(valueArray[2], valueArray[4], StatusName.valueOf(valueArray[3]));
            case ("EPIC"):
                return new Epic(valueArray[2], valueArray[4]);
            case ("SUBTASK"):
                return new Subtask(valueArray[2], valueArray[4], StatusName.valueOf(valueArray[3]),
                        Integer.valueOf(valueArray[5]));
            default:
                return null;
        }
    }

    static String historyToString(HistoryManager manager) {
        String history = "";
        for (Task task : manager.getHistory()) {
            history += task.getId() + ",";
        }
        return history.substring(0, history.length() - 1);
    }

    static List<Integer> historyFromString(String value) {
        String[] historyArray = value.split(",");
        List<Integer> historyList = new ArrayList<>();
        for (String n : historyArray) {
            historyList.add(Integer.valueOf(n));
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
