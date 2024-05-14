package com.yandex.app.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.service.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(manager.getHistory()));
    }
}
