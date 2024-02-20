package com.yandex.app.test.managers;

import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.Managers;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TasksManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }
}
