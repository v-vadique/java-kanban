package com.yandex.app;

import com.yandex.app.service.TaskManager;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.createTask(new Task("Сдать задание", "Отправить на ревью задание по 3 спринту",
                "NEW"));
        taskManager.createTask(new Task("Доделать отчет", "Сформировать и отправить годовой отчет",
                "NEW"));
        taskManager.createEpic(new Epic("Прибраться", "Навести порядок дома", "NEW"));
        taskManager.createSubtask(new Subtask("Вынести мусор", "Собрать и вынести мусор", "NEW",
                3));
        taskManager.createSubtask(new Subtask("Собрать шерсть", "Найти и выкинуть кошачью шерсть",
                "NEW", 3));
        taskManager.createEpic(new Epic("Купить продукты", "Сходить в магазин за продуктами",
                "NEW"));
        taskManager.createSubtask(new Subtask("Составить список", "Составить список продуктов",
                "NEW", 6));

        taskManager.printAllTasks();
        taskManager.printAllEpics();
        taskManager.printAllSubtasks();

        taskManager.getTasks().get(1).setStatus("DONE");
        taskManager.updateTask(taskManager.getTasks().get(1));
        taskManager.getSubtasks().get(7).setStatus("DONE");
        taskManager.updateSubtask(taskManager.getSubtasks().get(7));

        taskManager.printAllTasks();
        taskManager.printAllEpics();
        taskManager.printAllSubtasks();

        taskManager.deleteTask(2);
        taskManager.deleteEpic(3);

        taskManager.printAllTasks();
        taskManager.printAllEpics();
        taskManager.printAllSubtasks();
    }
}
