package com.yandex.app.test.epic;
import com.yandex.app.model.Epic;
import com.yandex.app.model.StatusName;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
public class EpicTest {
    private TaskManager inMemoryTaskManager;
    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = Managers.getDefault();
        inMemoryTaskManager.createEpic(new Epic("testEpic", "testEpicDescription"));
    }

    @Test
    public void shouldBeNewWhenNoSubtasks() {
        assertEquals(StatusName.NEW, inMemoryTaskManager.getEpicById(1).getStatus());
    }

    @ParameterizedTest
    @MethodSource
    public void checkEpicReconsiderMethod(StatusName status1, StatusName status2, StatusName expected) {
        inMemoryTaskManager.createSubtask(new Subtask("testSubtask", "testSubtaskDescription",
                status1, 10, "01.01.2012-00:00", 1));
        inMemoryTaskManager.createSubtask(new Subtask("testSubtask", "testSubtaskDescription",
                status2, 10, "01.01.2012-00:00", 1));
        assertEquals(expected, inMemoryTaskManager.getEpicById(1).getStatus());
    }

    static Stream<Arguments> checkEpicReconsiderMethod() {                  // спасибо за статью, полезно
        return Stream.of(Arguments.of(StatusName.NEW, StatusName.NEW, StatusName.NEW),
                Arguments.of(StatusName.DONE, StatusName.DONE, StatusName.DONE),
                Arguments.of(StatusName.NEW, StatusName.DONE, StatusName.IN_PROGRESS),
                Arguments.of(StatusName.IN_PROGRESS, StatusName.IN_PROGRESS, StatusName.IN_PROGRESS));
    }
}
