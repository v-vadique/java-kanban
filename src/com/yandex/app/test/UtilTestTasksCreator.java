package com.yandex.app.test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.StatusName;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

public class UtilTestTasksCreator {

    public static Task createTestTask() {
        return new Task("test", "test", StatusName.NEW, 1, "01.01.2000-00:00");
    }

    public static Epic createTestEpic() {
        return new Epic ("test", "test");
    }

    public static Subtask createTestSubtask(int epicId) {
        return new Subtask("test", "test", StatusName.NEW, 1,
                "01.01.2000-00:00", epicId);
    }
}
