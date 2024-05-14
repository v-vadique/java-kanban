package com.yandex.app.test.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.app.api.DurationAdapter;
import com.yandex.app.api.HttpTaskServer;
import com.yandex.app.api.LocalDateTimeAdapter;
import com.yandex.app.model.Epic;
import com.yandex.app.model.StatusName;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EpicsHandlerTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer httpTaskServer;
    Gson gson;
    @BeforeEach
    public void beforeEach() throws IOException {
        httpTaskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.intializeGson();
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
    public void getEpicsTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String bodyString = response.body();

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getAllEpics()), bodyString, "Данные не совпадают");
    }

    @Test
    public void getEpicByIdTest() throws IOException, InterruptedException {
        manager.createEpic(new Epic("имя3", "описание3"));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epic = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(3, epic.getId(), "Неверный ID эпика");
        assertEquals("имя3", epic.getName(), "Неверное имя эпика");
    }

    @Test
    public void createEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("имя3", "описание3");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(2, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("имя3", epicsFromManager.get(1).getName(), "Некорректное имя эпика");
    }

    @Test
    public void updateEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("имя3", "описание3");
        epic.setId(1);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic updatedEpic = manager.getEpicById(1);

        assertEquals(201, response.statusCode());
        assertEquals("имя3", updatedEpic.getName(), "Неверное имя эпика");
    }

    @Test
    public void deleteEpicTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Subtask> subtasks = manager.getAllSubtasks();

        assertEquals(200, response.statusCode());
        assertEquals(0, subtasks.size(), "Неверный размер");
    }

    @Test
    public void getAllEpicSubtasksTest() throws IOException, InterruptedException {
        manager.createSubtask(new Subtask("имя2", "описание2", StatusName.NEW,
                600, "31.12.2023-17:00", 1));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String bodyString = response.body();

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(manager.getAllEpicSubtasks(1)), bodyString, "Данные не совпадают");
    }
}
