package com.yandex.app.test.epic;
import com.yandex.app.model.Epic;
import com.yandex.app.model.StatusName;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class EpicTest {
    private static TaskManager inMemoryTaskManager;
    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = Managers.getDefault();
        inMemoryTaskManager.createEpic(new Epic("testEpic", "testEpicDescription"));
    }

    @Test
    public void shouldBeNewWhenNoSubtasks() {
        assertEquals(StatusName.NEW, inMemoryTaskManager.getEpicById(1).getStatus());
    }

    @Test
    public void shouldBeNewWhenAllSubtasksNew() {
        inMemoryTaskManager.createSubtask(new Subtask("testSubtask", "testSubtaskDescription",
                StatusName.NEW, 10, "01.01.2012-00:00", 1));
        inMemoryTaskManager.createSubtask(new Subtask("testSubtask", "testSubtaskDescription",
                StatusName.NEW, 10, "01.01.2012-00:00", 1));
        assertEquals(StatusName.NEW, inMemoryTaskManager.getEpicById(1).getStatus());
    }

    @Test
    public void shouldBeDoneWhenAllSubtasksDone() {
        inMemoryTaskManager.createSubtask(new Subtask("testSubtask", "testSubtaskDescription",
                StatusName.DONE, 10, "01.01.2012-00:00", 1));
        inMemoryTaskManager.createSubtask(new Subtask("testSubtask", "testSubtaskDescription",
                StatusName.DONE, 10, "01.01.2012-00:00", 1));
        assertEquals(StatusName.DONE, inMemoryTaskManager.getEpicById(1).getStatus());
    }

    @Test
    public void shouldBeInProgressWhenSubtasksNewAndDone() {
        inMemoryTaskManager.createSubtask(new Subtask("testSubtask", "testSubtaskDescription",
                StatusName.DONE, 10, "01.01.2012-00:00", 1));
        inMemoryTaskManager.createSubtask(new Subtask("testSubtask", "testSubtaskDescription",
                StatusName.NEW, 10, "01.01.2012-00:00", 1));
        assertEquals(StatusName.IN_PROGRESS, inMemoryTaskManager.getEpicById(1).getStatus());
    }

    @Test
    public void shouldBeInProgressWhenAllSubtasksInProgress() {
        inMemoryTaskManager.createSubtask(new Subtask("testSubtask", "testSubtaskDescription",
                StatusName.IN_PROGRESS, 10, "01.01.2012-00:00", 1));
        inMemoryTaskManager.createSubtask(new Subtask("testSubtask", "testSubtaskDescription",
                StatusName.IN_PROGRESS, 10, "01.01.2012-00:00", 1));
        assertEquals(StatusName.IN_PROGRESS, inMemoryTaskManager.getEpicById(1).getStatus());
    }
}
