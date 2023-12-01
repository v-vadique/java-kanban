public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.createTask(new Task("Сдать задание", "Отправить на ревью задание по 3 спринту"));
        taskManager.createTask(new Task("Доделать отчет", "Сформировать и отправить годовой отчет"));
        taskManager.createEpic(new Epic("Прибраться", "Навести порядок дома"));
        taskManager.createSubtask(new Subtask("Вынести мусор", "Собрать и вынести мусор", 3));
        taskManager.createSubtask(new Subtask("Собрать шерсть", "Найти и выкинуть кошачью шерсть", 3));
        taskManager.createEpic(new Epic("Купить продукты", "Сходить в магазин за продуктами"));
        taskManager.createSubtask(new Subtask("Составить список", "Составить список продуктов", 6));

        taskManager.printAllTasks(); // В задании было сказано распечатать через System.out.println,
        taskManager.printAllEpics(); // но данные методы работают по аналогии, поэтому печатаем через них
        taskManager.printAllSubtasks();

        taskManager.tasks.get(1).setStatus("DONE");
        taskManager.updateTask(taskManager.tasks.get(1));
        taskManager.subtasks.get(7).setStatus("DONE");
        taskManager.updateSubtask(taskManager.subtasks.get(7));

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
