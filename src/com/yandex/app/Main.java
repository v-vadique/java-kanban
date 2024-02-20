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

        fileBackedTasksManager.createTask(new Task("имя1", "описание1", StatusName.NEW, 10,
                "10.01.2024-12:00"));
        fileBackedTasksManager.createTask(new Task("имя2", "описание2", StatusName.DONE, 180,
                "31.05.2017-23:00"));
        fileBackedTasksManager.createEpic(new Epic("имя3", "описание3"));
        fileBackedTasksManager.createSubtask(new Subtask("имя4", "описание4", StatusName.NEW,
                600, "31.12.2023-17:00", 3));

        fileBackedTasksManager.getTaskById(2);
        fileBackedTasksManager.getSubtaskById(4);
        fileBackedTasksManager.getEpicById(3);

        FileBackedTasksManager fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(file);
        System.out.println(fileBackedTasksManager2.getAllTasks());
        System.out.println(fileBackedTasksManager2.getAllEpics());
        System.out.println(fileBackedTasksManager2.getAllSubtasks());
        System.out.println(fileBackedTasksManager2.getHistory());
        System.out.println(fileBackedTasksManager2.getPrioritizedTasks());

    }
}
