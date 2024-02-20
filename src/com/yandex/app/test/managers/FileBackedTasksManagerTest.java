package com.yandex.app.test.managers;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import com.yandex.app.service.FileBackedTasksManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TasksManagerTest<FileBackedTasksManager> {
    static File file;
    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager();
        file = new File("testData.csv");
    }

    @Test
    public void emptyTaskList() {
        taskManager.deleteAllTasks(); // у save() приватный доступ, поэтому вызываю его через другой метод
        TaskManager taskManagerTest = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager, taskManagerTest);
    }

    @Test
    public void epicWithNoSubtasks() {
        taskManager.createEpic(new Epic("test", "test"));
        taskManager.getEpicById(1);
        TaskManager taskManagerTest = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager, taskManagerTest);
    }

    @Test
    public void emptyHistory() {
        taskManager.createTask(new Task("test", "test"));
        taskManager.createTask(new Task("test", "test"));
        taskManager.createEpic(new Epic("test", "test"));
        TaskManager taskManagerTest = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager, taskManagerTest);
    }
}
