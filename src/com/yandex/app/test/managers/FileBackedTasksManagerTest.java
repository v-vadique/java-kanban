package com.yandex.app.test.managers;

import com.yandex.app.model.Epic;
import com.yandex.app.model.StatusName;
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
        file = new File("data.csv");
    }

    @Test
    public void emptyTaskList() {
        taskManager.deleteAllTasks(); // у save() приватный доступ, поэтому вызываю его через другой метод
        TaskManager taskManagerTest = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager.getHistory(), taskManagerTest.getHistory());
    }

    @Test
    public void epicWithNoSubtasks() {
        taskManager.createEpic(new Epic("test", "test"));
        TaskManager taskManagerTest = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager.getHistory(), taskManagerTest.getHistory());
    }

    @Test
    public void emptyHistory() {
        taskManager.createTask(new Task("test", "test", StatusName.NEW,
                10, "01.01.2000-00:00"));
        taskManager.createTask(new Task("test", "test", StatusName.NEW,
                10, "01.01.2000-00:00"));
        taskManager.createEpic(new Epic("test", "test"));
        TaskManager taskManagerTest = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager.getHistory(), taskManagerTest.getHistory());
    }
}
