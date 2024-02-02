package com.yandex.app;

import com.yandex.app.model.StatusName;
import com.yandex.app.service.*;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("data.csv");
        FileBackedTasksManager fileBackedTasksManager = Managers.getFileBackedTasksManager();

        fileBackedTasksManager.createTask(new Task("имя1", "описание1"));
        fileBackedTasksManager.createTask(new Task("имя2", "описание2"));
        fileBackedTasksManager.createEpic(new Epic("имя3", "описание3"));
        fileBackedTasksManager.createSubtask(new Subtask("имя4", "описание4", StatusName.NEW, 3));

        fileBackedTasksManager.getTaskById(2);
        fileBackedTasksManager.getSubtaskById(4);
        fileBackedTasksManager.getEpicById(3);

        FileBackedTasksManager fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(file);
        System.out.println(fileBackedTasksManager2.getAllTasks());
        System.out.println(fileBackedTasksManager2.getAllEpics());
        System.out.println(fileBackedTasksManager2.getAllSubtasks());
        System.out.println(fileBackedTasksManager2.getHistory());

    }
}
