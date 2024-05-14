package com.yandex.app.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.api.Endpoint;
import com.yandex.app.api.HttpTaskServer;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod(), body);

        switch (endpoint) {
            case Endpoint.GET_ALL_TASKS:
                handleGetAllTasks(exchange);
                break;
            case Endpoint.GET_TASK_BY_ID:
                handleGetTaskById(exchange);
                break;
            case Endpoint.CREATE_TASK:
                handleCreateTask(exchange, body);
                break;
            case Endpoint.UPDATE_TASK:
                handleUpdateTask(exchange, body);
                break;
            case Endpoint.DELETE_TASK:
                handleDeleteTask(exchange);
                break;
            default:
                System.out.println("Такой команды не существует");
                sendNotFound(exchange);
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(manager.getAllTasks()));
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = HttpTaskServer.getTaskId(exchange);
        if (taskIdOpt.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int taskId = taskIdOpt.get();
        for (Task task : manager.getAllTasks()) {
            if (task.getId() == taskId) {
                sendText(exchange, gson.toJson(manager.getTaskById(taskId)));
                return;
            }
        }
        sendNotFound(exchange);
    }

    private void handleCreateTask(HttpExchange exchange, String taskJson) throws IOException {
        try {
            Task newTask = gson.fromJson(taskJson, Task.class);
            manager.createTask(newTask);
            sendOk(exchange);
        } catch (Exception e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleUpdateTask(HttpExchange exchange, String taskJson) throws IOException {
        try {
            Task updateTask = gson.fromJson(taskJson, Task.class);
            manager.updateTask(updateTask);
            sendOk(exchange);
        } catch (Exception e) {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = HttpTaskServer.getTaskId(exchange);
        if (taskIdOpt.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int taskId = taskIdOpt.get();
        manager.deleteTask(taskId);
        sendText(exchange, "Задача была удалена");
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod, String body) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod) {
            case "GET":
                if (pathParts.length == 2) {
                    return Endpoint.GET_ALL_TASKS;
                } else {
                    return Endpoint.GET_TASK_BY_ID;
                }
            case "POST":
                if (body.contains("\"id\": 0") || body.contains("\"id\":0")) {
                    return Endpoint.CREATE_TASK;
                } else {
                    return Endpoint.UPDATE_TASK;
                }
            case "DELETE":
                return Endpoint.DELETE_TASK;
            default:
                return Endpoint.UNKNOWN_ENDPOINT;
        }
    }
}
