package com.yandex.app.test.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.app.api.DurationAdapter;
import com.yandex.app.api.HttpTaskServer;
import com.yandex.app.api.LocalDateTimeAdapter;
import com.yandex.app.model.Epic;
import com.yandex.app.model.StatusName;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubtasksHandlerTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer httpTaskServer;
    Gson gson;
    @BeforeEach
    public void beforeEach() throws IOException {
        httpTaskServer = new HttpTaskServer(manager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime .class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration .class, new DurationAdapter())
                .create();
        manager.createEpic(new Epic("имя1", "описание1"));
        manager.createSubtask(new Subtask("имя2", "описание2", StatusName.NEW,
                600, "31.12.2023-17:00", 1));
        httpTaskServer.start();
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
    }

    @Test
    public void getSubtasksTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String bodyString = response.body();

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getAllSubtasks()), bodyString, "Данные не совпадают");
    }

    @Test
    public void getSubtaskByIdTest() throws IOException, InterruptedException {
        manager.createSubtask(new Subtask("имя3", "описание3", StatusName.NEW,
                600, "31.12.2023-17:00", 1));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals(2, subtask.getId(), "Неверный ID подзадачи");
        assertEquals("имя2", subtask.getName(), "Неверное имя подзадачи");
    }

    @Test
    public void createSubtaskTest() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("имя3", "описание3", StatusName.NEW,
                600, "31.12.2023-17:00", 1);
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("имя3", subtasksFromManager.get(1).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void updateSubtaskTest() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("имя3", "описание3", StatusName.IN_PROGRESS,
                600, "31.12.2023-17:00", 1);
        subtask.setId(2);
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask updatedSubtask = manager.getSubtaskById(2);

        assertEquals(201, response.statusCode());
        assertEquals("имя3", updatedSubtask.getName(), "Неверное имя подзадачи");
        assertEquals(StatusName.IN_PROGRESS, updatedSubtask.getStatus(), "Неверный статус подзадачи");
    }

    @Test
    public void deleteSubtaskTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Subtask> subtasks = manager.getAllSubtasks();

        assertEquals(200, response.statusCode());
        assertEquals(0, subtasks.size(), "Неверный размер");
    }
}
