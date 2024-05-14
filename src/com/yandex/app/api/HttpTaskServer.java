package com.yandex.app.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.api.handlers.*;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.manager = manager;
        httpServer.createContext("/tasks", new TasksHandler(this.manager));
        httpServer.createContext("/epics", new EpicsHandler(this.manager));
        httpServer.createContext("/subtasks", new SubtasksHandler(this.manager));
        httpServer.createContext("/history", new HistoryHandler(this.manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(this.manager));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту.");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен.");
    }

    public static Gson intializeGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public static Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getFileBackedTasksManager());
        httpTaskServer.start();
    }
}