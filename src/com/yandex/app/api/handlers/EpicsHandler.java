package com.yandex.app.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.api.Endpoint;
import com.yandex.app.api.HttpTaskServer;
import com.yandex.app.model.Epic;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod(), body);

        switch (endpoint) {
            case Endpoint.GET_ALL_EPICS:
                handleGetAllEpics(exchange);
                break;
            case Endpoint.GET_EPIC_BY_ID:
                handleGetEpicById(exchange);
                break;
            case Endpoint.GET_ALL_EPIC_SUBTASKS:
                handleGetAllEpicSubtasks(exchange);
                break;
            case Endpoint.CREATE_EPIC:
                handleCreateEpic(exchange, body);
                break;
            case Endpoint.UPDATE_EPIC:
                handleUpdateEpic(exchange, body);
                break;
            case Endpoint.DELETE_EPIC:
                handleDeleteEpic(exchange);
                break;
            default:
                System.out.println("Такой команды не существует");
                sendNotFound(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(manager.getAllEpics()));
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = HttpTaskServer.getTaskId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int epicId = epicIdOpt.get();
        for (Epic epic : manager.getAllEpics()) {
            if (epic.getId() == epicId) {
                sendText(exchange, gson.toJson(manager.getEpicById(epicId)));
                return;
            }
        }
        sendNotFound(exchange);
    }

    private void handleGetAllEpicSubtasks(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = HttpTaskServer.getTaskId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int epicId = epicIdOpt.get();
        for (Epic epic : manager.getAllEpics()) {
            if (epic.getId() == epicId) {
                sendText(exchange, gson.toJson(manager.getAllEpicSubtasks(epicId)));
                return;
            }
        }
        sendNotFound(exchange);
    }

    private void handleCreateEpic(HttpExchange exchange, String taskJson) throws IOException {
        try {
            Epic newEpic = gson.fromJson(taskJson, Epic.class);
            manager.createEpic(newEpic);
            sendOk(exchange);
        } catch (Exception e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleUpdateEpic(HttpExchange exchange, String taskJson) throws IOException {
        try {
            Epic newEpic = gson.fromJson(taskJson, Epic.class);
            manager.updateEpic(newEpic);
            sendOk(exchange);
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = HttpTaskServer.getTaskId(exchange);
        if (epicIdOpt.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int epicId = epicIdOpt.get();
        manager.deleteEpic(epicId);
        sendText(exchange, "Эпик был удален");
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod, String body) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod) {
            case "GET":
                if (pathParts.length == 2) {
                    return Endpoint.GET_ALL_EPICS;
                } else if (pathParts.length == 3) {
                    return Endpoint.GET_EPIC_BY_ID;
                } else {
                    return Endpoint.GET_ALL_EPIC_SUBTASKS;
                }
            case "POST":
                if (body.contains("\"id\": 0") || body.contains("\"id\":0")) {
                    return Endpoint.CREATE_EPIC;
                } else {
                    return Endpoint.UPDATE_EPIC;
                }
            case "DELETE":
                return Endpoint.DELETE_EPIC;
            default:
                return Endpoint.UNKNOWN_ENDPOINT;
        }
    }
}
