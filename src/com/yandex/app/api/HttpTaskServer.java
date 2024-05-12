package com.yandex.app.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final TaskManager manager;
    private static final String DEFAULT_CHARSET = "UTF-8";

    public HttpTaskServer(TaskManager manager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.manager = manager;
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/epics", new EpicsHandler());
        httpServer.createContext("/subtasks", new SubtasksHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту.");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен.");
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getFileBackedTasksManager());

        httpTaskServer.start();
    }

    class TasksHandler extends BaseHttpHandler {
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
            Optional<Integer> taskIdOpt = getTaskId(exchange);
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
            Optional<Integer> taskIdOpt = getTaskId(exchange);
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

    class SubtasksHandler extends BaseHttpHandler {
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
            Optional<Integer> subtaskIdOpt = getTaskId(exchange);
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
            Optional<Integer> subtaskIdOpt = getTaskId(exchange);
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

    class EpicsHandler extends BaseHttpHandler {
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
            Optional<Integer> epicIdOpt = getTaskId(exchange);
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
            Optional<Integer> epicIdOpt = getTaskId(exchange);
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
            Optional<Integer> epicIdOpt = getTaskId(exchange);
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

    class HistoryHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendText(exchange, gson.toJson(manager.getHistory()));
        }
    }

    class PrioritizedHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendText(exchange, gson.toJson(manager.getPrioritizedTasks()));
        }
    }

    public Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}