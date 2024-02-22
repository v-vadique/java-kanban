package com.yandex.app.test.managers;

import com.yandex.app.model.*;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static com.yandex.app.test.UtilTestTasksCreator.*;

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
        taskManager.createTask(createTestTask());
        taskManager.createTask(createTestTask());
        assertEquals(2, taskManager.getAllTasks().size());
    }

    @Test
    public void getAllEpicsShouldNotBeEmptyWhenEpicsAdded() {
        taskManager.createEpic(createTestEpic());
        assertEquals(1, taskManager.getAllEpics().size());
    }

    @Test
    public void getAllSubtasksShouldNotBeEmptyWhenSubtasksAdded() {
        taskManager.createEpic(createTestEpic());
        taskManager.createSubtask(createTestSubtask(1));
        taskManager.createSubtask(createTestSubtask(1));
        assertEquals(2, taskManager.getAllSubtasks().size());
    }

    @Test
    public void tasksShouldBeEmptyWhenDeleteAllTasksCalled() {
        taskManager.createTask(createTestTask());
        taskManager.createTask(createTestTask());
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    public void epicsShouldBeEmptyWhenDeleteAllEpicsCalled() {
        taskManager.createEpic(createTestEpic());
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    public void subtasksShouldBeEmptyWhenDeleteAllSubtasksCalled() {
        taskManager.createEpic(createTestEpic());
        taskManager.createSubtask(createTestSubtask(1));
        taskManager.createSubtask(createTestSubtask(1));
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    public void getTaskByIdShouldReturnTask() {
        taskManager.createTask(createTestTask());
        Task task = taskManager.getTaskById(1);
        assertEquals(task, taskManager.getTaskById(1));
    }

    @Test
    public void getTaskByIdShouldReturnNullWhenNoSuchId() {
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    public void getEpicByIdShouldReturnEpic() {
        taskManager.createEpic(createTestEpic());
        Epic epic = taskManager.getEpicById(1);
        assertEquals(epic, taskManager.getEpicById(1));
    }

    @Test
    public void getEpicByIdShouldReturnNullWhenNoSuchId() {
        assertNull(taskManager.getEpicById(1));
    }

    @Test
    public void getSubtaskByIdShouldReturnSubtask() {
        taskManager.createEpic(createTestEpic());
        taskManager.createSubtask(createTestSubtask(1));
        Subtask subtask = taskManager.getSubtaskById(2);
        assertEquals(subtask, taskManager.getSubtaskById(2));
    }

    @Test
    public void getSubtaskByIdShouldReturnNullWhenNoSuchId() {
        assertNull(taskManager.getSubtaskById(1));
    }

    @Test
    public void createTaskShouldCreateNewTask() {
        Task task = createTestTask();
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
        Epic epic = createTestEpic();
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpicById(1));
    }

    @Test
    public void createEpicShouldThrowExceptionWhenNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> taskManager.createEpic(null));
        assertNull(exception.getMessage());
    }

    @Test
    public void createSubtaskShouldCreateNewSubtask() { // тесты метода countEpicTime() вставил сюда, т.к. он
        taskManager.createEpic(createTestEpic());       // приватный и вызывается из createSubtask()
        taskManager.createSubtask(createTestSubtask(1));
        assertNotNull(taskManager.getSubtaskById(2), "Новый Subtask не создается");
        assertEquals(taskManager.getEpicById(1).getStartTime(), taskManager.getSubtaskById(2).getStartTime(),
                "Время начала эпика устанавливается неправильно");
        assertEquals(taskManager.getEpicById(1).getEndTime(), taskManager.getSubtaskById(2).getEndTime(),
                "Время конца эпика устанавливается неправильно");
        assertEquals(taskManager.getEpicById(1).getDuration(), taskManager.getSubtaskById(2).getDuration(),
                "Продолжительность эпика устанавливается неправильно");
    }

    @Test
    public void createSubtaskShouldThrowExceptionWhenNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> taskManager.createSubtask(null));
        assertNull(exception.getMessage());
    }

    @Test
    public void deleteTaskShouldDeleteTask() {
        taskManager.createTask(createTestTask());
        taskManager.deleteTask(1);
        assertNull(taskManager.getTaskById(1));
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    public void deleteEpicShouldDeleteEpic() {
        taskManager.createEpic(createTestEpic());
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
        taskManager.createEpic(createTestEpic());
        taskManager.createSubtask(createTestSubtask(1));
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
        taskManager.createEpic(createTestEpic());
        taskManager.createSubtask(createTestSubtask(1));
        taskManager.createSubtask(createTestSubtask(1));
        assertEquals(2, taskManager.getAllEpicSubtasks(1).size());
    }

    @Test
    public void getAllEpicSubtasksShouldReturn0WhenEmpty() {
        taskManager.createEpic(createTestEpic());
        assertEquals(0, taskManager.getAllEpicSubtasks(1).size());
    }

    @Test
    public void getAllEpicSubtasksShouldThrowExceptionWhenWrongId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                taskManager.getAllEpicSubtasks(2));
        assertNull(exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource
    public void checkForCrossingsTest(String startTime1, String startTime2, boolean expected) {
        taskManager.createTask(new Task ("test", "test", StatusName.NEW, 180, startTime1));
        taskManager.createTask(new Task ("test", "test", StatusName.NEW, 180, startTime2));
        boolean isTimeAvailable = taskManager.checkForCrossings();
        assertEquals(expected, isTimeAvailable);
    }

    static Stream<Arguments> checkForCrossingsTest() {
        return Stream.of(Arguments.of("01.01.2024-00:00", "01.02.2024-00:00", true),
                Arguments.of("01.01.2024-00:00", "01.01.2024-01:00", false));
    }
}
