package com.yandex.app.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.api.Endpoint;
import com.yandex.app.api.HttpTaskServer;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler{
    public SubtasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod(), body);

        switch (endpoint) {
            case Endpoint.GET_ALL_SUBTASKS:
                handleGetAllSubtasks(exchange);
                break;
            case Endpoint.GET_SUBTASK_BY_ID:
                handleGetSubtaskById(exchange);
                break;
            case Endpoint.CREATE_SUBTASK:
                handleCreateSubtask(exchange, body);
                break;
            case Endpoint.UPDATE_SUBTASK:
                handleUpdateSubtask(exchange, body);
                break;
            case Endpoint.DELETE_SUBTASK:
                handleDeleteSubtask(exchange);
                break;
            default:
                System.out.println("Такой команды не существует");
                sendNotFound(exchange);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(manager.getAllSubtasks()));
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskIdOpt = HttpTaskServer.getTaskId(exchange);
        if (subtaskIdOpt.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int subtaskId = subtaskIdOpt.get();
        for (Subtask subtask : manager.getAllSubtasks()) {
            if (subtask.getId() == subtaskId) {
                sendText(exchange, gson.toJson(manager.getSubtaskById(subtaskId)));
                return;
            }
        }
        sendNotFound(exchange);
    }

    private void handleCreateSubtask(HttpExchange exchange, String taskJson) throws IOException {
        try {
            Subtask newSubtask = gson.fromJson(taskJson, Subtask.class);
            manager.createSubtask(newSubtask);
            sendOk(exchange);
        } catch (Exception e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange, String taskJson) throws IOException {
        try {
            Subtask updateSubtask = gson.fromJson(taskJson, Subtask.class);
            manager.updateSubtask(updateSubtask);
            sendOk(exchange);
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskIdOpt = HttpTaskServer.getTaskId(exchange);
        if (subtaskIdOpt.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int subtaskId = subtaskIdOpt.get();
        manager.deleteSubtask(subtaskId);
        sendText(exchange, "Задача была удалена");
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod, String body) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod) {
            case "GET":
                if (pathParts.length == 2) {
                    return Endpoint.GET_ALL_SUBTASKS;
                } else {
                    return Endpoint.GET_SUBTASK_BY_ID;
                }
            case "POST":
                if (body.contains("\"id\": 0") || body.contains("\"id\":0")) {
                    return Endpoint.CREATE_SUBTASK;
                } else {
                    return Endpoint.UPDATE_SUBTASK;
                }
            case "DELETE":
                return Endpoint.DELETE_SUBTASK;
            default:
                return Endpoint.UNKNOWN_ENDPOINT;
        }
    }
}
