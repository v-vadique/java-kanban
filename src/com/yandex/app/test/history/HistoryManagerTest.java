package com.yandex.app.test.history;

import com.yandex.app.history.HistoryManager;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    static HistoryManager historyManager;
    static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldAddTask() {
        Task task = new Task("test", "test");
        task.setId(1);
        Epic epic = new Epic("test", "test");
        epic.setId(2);
        historyManager.add(task);
        historyManager.add(epic);
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    public void shouldBeEmptyWhenNoTasksAdded() {
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    public void shouldRemoveTask() {
        Task task = new Task("test", "test");
        task.setId(1);
        Epic epic = new Epic("test", "test");
        epic.setId(2);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.remove(1);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void removeShouldThrowExceptionWhenHistoryEmpty() {
        assertEquals(0, historyManager.getHistory().size());
    }
}
