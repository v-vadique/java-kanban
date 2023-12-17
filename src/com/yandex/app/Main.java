package com.yandex.app;

import com.yandex.app.model.StatusName;
import com.yandex.app.service.*;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        taskManager.createTask(new Task("Сдать задание", "Отправить на ревью задание по 3 спринту",
                StatusName.NEW));
        taskManager.createTask(new Task("Доделать отчет", "Сформировать и отправить годовой отчет",
                StatusName.NEW));
        taskManager.createEpic(new Epic("Прибраться", "Навести порядок дома"));
        taskManager.createSubtask(new Subtask("Вынести мусор", "Собрать и вынести мусор", StatusName.NEW,
                3));
        taskManager.createSubtask(new Subtask("Собрать шерсть", "Найти и выкинуть кошачью шерсть",
                StatusName.NEW, 3));
        taskManager.createEpic(new Epic("Купить продукты", "Сходить в магазин за продуктами"));
        taskManager.createSubtask(new Subtask("Составить список", "Составить список продуктов",
                StatusName.NEW, 6));

        taskManager.getTaskById(1);

        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(4);

        System.out.println(taskManager.getHistory());

        taskManager.getEpicById(6);

        System.out.println(taskManager.getHistory());
    }
}
