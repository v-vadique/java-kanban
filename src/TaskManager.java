import java.util.HashMap;
import java.util.Scanner;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    int nextId = 1;

    public void printAllTasks() {
        int taskNumber = 1;

        System.out.println("Задачи: ");
        for (Integer integer : tasks.keySet()) {
            System.out.println(taskNumber + ". " + tasks.get(integer));
            taskNumber++;
        }
    }

    public void printAllEpics() {
        int epicNumber = 1;

        System.out.println("Эпики: ");
        for (Integer integer : epics.keySet()) {
            System.out.println(epicNumber + ". " + epics.get(integer));
            epicNumber++;
        }
    }

    public void printAllSubtasks() {
        int subtaskNumber = 1;

        System.out.println("Подзадачи: ");
        for (Integer integer : subtasks.keySet()){
            System.out.println(subtaskNumber + ". " + subtasks.get(integer));
            subtaskNumber++;
        }
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
        System.out.println("Все подзадачи были удалены");
    }

    public Task getTaskById(int id) {
        System.out.println(tasks.get(id));
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        System.out.println(epics.get(id));
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        System.out.println(subtasks.get(id));
        return subtasks.get(id);
    }
    public void createTask(Task task) {
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
        tasks.get(task.getId()).setStatus("NEW");
        System.out.println("Задача создана. Идентификатор " + task.getId());
    }

    public void createEpic(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        epics.get(epic.getId()).setStatus("NEW");
        System.out.println("Эпик создан. Идентификатор " + epic.getId());
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(nextId);
        nextId++;
        subtasks.put(subtask.getId(), subtask);
        subtasks.get(subtask.getId()).setStatus("NEW");
        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        System.out.println("Подзадача создана. Идентификатор " + subtask.getId());
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        if (tasks.get(task.getId()).getStatus().equals("NEW")) {
            tasks.get(task.getId()).setStatus("IN PROGRESS");
        }
        System.out.println("Задача обновлена. Идентификатор " + task.getId());
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        if (epics.get(epic.getId()).getStatus().equals("NEW")) {
            epics.get(epic.getId()).setStatus("IN PROGRESS");
        }
        System.out.println("Эпик обновлен. Идентификатор " + epic.getId());
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        if (subtasks.get(subtask.getId()).getStatus().equals("NEW")) {
            subtasks.get(subtask.getId()).setStatus("IN PROGRESS");
        }
        System.out.println("Подзадача обновлена. Индентификатор " + subtask.getId());
        isDone(epics.get(subtask.getEpicId()));
    }

    public void isDone(Epic epic){
        for (Integer subtaskId : epic.getSubtaskIds()) {
            if (subtasks.get(subtaskId).getStatus() != "DONE"){
                return;
            }
        }
        epics.get(epic.getId()).setStatus("DONE");
    }

    public void deleteTask(int id) {
        tasks.remove(id);
        System.out.println("Задача была удалена");
    }

    public void deleteEpic(int id) {
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
        System.out.println("Эпик был удален");
    }

    public void deleteSubtask(int id) {
        subtasks.remove(id);
        System.out.println("Подзадача была удалена");
    }

    public void getAllEpicSubtasks(int id) {
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            System.out.println(subtasks.get(subtaskId));
        }
    }
}
