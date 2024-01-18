package com.yandex.app.service;

import com.yandex.app.history.HistoryManager;
import com.yandex.app.history.InMemoryHistoryManager;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
