package com.yandex.app.test.history;

import com.yandex.app.history.HistoryManager;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.yandex.app.test.UtilTestTasksCreator.*;

public class HistoryManagerTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldAddTask() {
        Task task = createTestTask();
        task.setId(1);
        Epic epic = createTestEpic();
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
        Task task = createTestTask();
        task.setId(1);
        Epic epic = createTestEpic();
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

    private void createTaskAndEpic() {

    }
}
