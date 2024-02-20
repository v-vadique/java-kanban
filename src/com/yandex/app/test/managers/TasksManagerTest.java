package com.yandex.app.test.managers;

import com.yandex.app.model.*;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

abstract class TasksManagerTest<T extends TaskManager> {
    public T taskManager;

    @Test
    public void getAllTasksShouldBeEmptyWhenNoTasksAdded() {
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    public void getAllEpicsShouldBeEmptyWhenNoEpicsAdded() {
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    public void getAllSubtasksShouldBeEmptyWhenNoSubtasksAdded() {
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    public void getAllTasksShouldNotBeEmptyWhenTasksAdded() {
        taskManager.createTask(new Task("test", "test"));
        taskManager.createTask(new Task("test", "test"));
        assertEquals(2, taskManager.getAllTasks().size());
    }

    @Test
    public void getAllEpicsShouldNotBeEmptyWhenEpicsAdded() {
        taskManager.createEpic(new Epic("test", "test"));
        assertEquals(1, taskManager.getAllEpics().size());
    }

    @Test
    public void getAllSubtasksShouldNotBeEmptyWhenSubtasksAdded() {
        taskManager.createEpic(new Epic("test", "test"));
        taskManager.createSubtask(new Subtask("test", "test", StatusName.NEW, 10,
                "01.01.2012-00:00", 1));
        taskManager.createSubtask(new Subtask("test", "test", StatusName.NEW, 10,
                "01.01.2012-00:00", 1));
        assertEquals(2, taskManager.getAllSubtasks().size());
    }

    @Test
    public void tasksShouldBeEmptyWhenDeleteAllTasksCalled() {
        taskManager.createTask(new Task("test", "test"));
        taskManager.createTask(new Task("test", "test"));
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    public void epicsShouldBeEmptyWhenDeleteAllEpicsCalled() {
        taskManager.createEpic(new Epic("test", "test"));
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    public void subtasksShouldBeEmptyWhenDeleteAllSubtasksCalled() {
        taskManager.createEpic(new Epic("test", "test"));
        taskManager.createSubtask(new Subtask("test", "test", StatusName.NEW, 10,
                "01.01.2012-00:00", 1));
        taskManager.createSubtask(new Subtask("test", "test", StatusName.NEW, 10,
                "01.01.2012-00:00", 1));
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    public void getTaskByIdShouldReturnTask() {
        taskManager.createTask(new Task("test", "test"));
        Task task = taskManager.getTaskById(1);
        assertEquals(task, taskManager.getTaskById(1));
    }

    @Test
    public void getTaskByIdShouldReturnNullWhenNoSuchId() {
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void getEpicByIdShouldReturnEpic() {
        taskManager.createEpic(new Epic("test", "test"));
        Epic epic = taskManager.getEpicById(1);
        assertEquals(epic, taskManager.getEpicById(1));
    }

    @Test
    public void getEpicByIdShouldReturnNullWhenNoSuchId() {
        assertNull(taskManager.getEpicById(1));
    }

    @Test
    public void getSubtaskByIdShouldReturnSubtask() {
        taskManager.createEpic(new Epic("test", "test"));
        taskManager.createSubtask(new Subtask("test", "test", StatusName.NEW, 10,
                "01.01.2012-00:00", 1));
        Subtask subtask = taskManager.getSubtaskById(2);
        assertEquals(subtask, taskManager.getSubtaskById(2));
    }

    @Test
    public void getSubtaskByIdShouldReturnNullWhenNoSuchId() {
        assertNull(taskManager.getSubtaskById(1));
    }

    @Test
    public void createTaskShouldCreateNewTask() {
        Task task = new Task("test", "test");
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTaskById(1));
    }

    @Test
    public void createTaskShouldThrowExceptionWhenNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> taskManager.createTask(null));
        assertNull(exception.getMessage());
    }

    @Test
    public void createEpicShouldCreateNewEpic() {
        Epic epic = new Epic("test", "test");
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpicById(1));
    }

    @Test
    public void createEpicShouldThrowExceptionWhenNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> taskManager.createEpic(null));
        assertNull(exception.getMessage());
    }

    @Test
    public void createSubtaskShouldCreateNewSubtask() {
        taskManager.createEpic(new Epic("test", "test"));
        Subtask subtask = new Subtask("test", "test", StatusName.NEW, 10,
                "01.01.2012-00:00", 1);
        taskManager.createSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtaskById(2));
    }

    @Test
    public void createSubtaskShouldThrowExceptionWhenNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> taskManager.createSubtask(null));
        assertNull(exception.getMessage());
    }

    @Test
    public void deleteTaskShouldDeleteTask() {
        taskManager.createTask(new Task("test", "test"));
        taskManager.deleteTask(1);
        assertNull(taskManager.getTaskById(1));
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    public void deleteTaskShouldThrowExceptionWhenWrongId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> taskManager.deleteTask(1));
        assertNull(exception.getMessage());
    }

    @Test
    public void deleteEpicShouldDeleteEpic() {
        taskManager.createEpic(new Epic("test", "test"));
        taskManager.deleteEpic(1);
        assertNull(taskManager.getEpicById(1));
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    public void deleteEpicShouldThrowExceptionWhenWrongId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> taskManager.deleteEpic(1));
        assertNull(exception.getMessage());
    }

    @Test
    public void deleteSubtaskShouldDeleteSubtask() {
        taskManager.createEpic(new Epic("test", "test"));
        taskManager.createSubtask(new Subtask("test", "test", StatusName.NEW, 10,
                "01.01.2012-00:00", 1));
        taskManager.deleteSubtask(2);
        assertNull(taskManager.getSubtaskById(2));
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    public void deleteSubtaskShouldThrowExceptionWhenWrongId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> taskManager.deleteSubtask(1));
        assertNull(exception.getMessage());
    }

    @Test
    public void getAllEpicSubtaskShouldReturnListOfSubtasks() {
        taskManager.createEpic(new Epic("test", "test"));
        taskManager.createSubtask(new Subtask("test", "test", StatusName.NEW, 10,
                "01.01.2012-00:00", 1));
        taskManager.createSubtask(new Subtask("test", "test", StatusName.NEW, 10,
                "01.01.2012-00:00", 1));
        assertEquals(2, taskManager.getAllEpicSubtasks(1).size());
    }

    @Test
    public void getAllEpicSubtasksShouldReturn0WhenEmpty() {
        taskManager.createEpic(new Epic("test", "test"));
        assertEquals(0, taskManager.getAllEpicSubtasks(1).size());
    }

    @Test
    public void getAllEpicSubtasksShouldThrowExceptionWhenWrongId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                taskManager.getAllEpicSubtasks(2));
        assertNull(exception.getMessage());
    }
}
